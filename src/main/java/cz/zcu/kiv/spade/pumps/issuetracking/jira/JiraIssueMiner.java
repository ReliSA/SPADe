package cz.zcu.kiv.spade.pumps.issuetracking.jira;

import com.atlassian.jira.rest.client.JiraRestClient;
import com.atlassian.jira.rest.client.domain.*;
import cz.zcu.kiv.spade.App;
import cz.zcu.kiv.spade.domain.*;
import cz.zcu.kiv.spade.domain.Status;
import cz.zcu.kiv.spade.domain.enums.SeverityClass;
import cz.zcu.kiv.spade.domain.enums.StatusClass;
import cz.zcu.kiv.spade.domain.enums.StatusSuperClass;
import cz.zcu.kiv.spade.domain.enums.Tool;
import cz.zcu.kiv.spade.pumps.issuetracking.IssueMiner;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

class JiraIssueMiner extends IssueMiner<Issue> {

    private static final String ISSUE_KEY_FORMAT = "%s-%d";
    private static final String QUERY_FORMAT = "project = %s";
    private static final String ISSUE_URL_FORMAT = "https://%s/" + Tool.JIRA.name().toLowerCase() + "/browse/%s";
    private static final String TAGS_FIELD_NAME = "Tags";
    private static final String SEVERITY_FIELD_NAME = "Severity";
    private static final String TRANSITION_CHANGE_DESC = "transition";
    private static final String TRANSITION_FORMAT = TRANSITION_CHANGE_DESC + ": %s";
    private static final String SPACE = " ";

    private final JiraChangelogMiner changelogMiner;
    private final JiraCommentMiner commentMiner;
    private final JiraWorklogMiner worklogMiner;
    private final JiraAttachmentMiner attachmentMiner;

    JiraIssueMiner(JiraPump pump) {
        super(pump);
        changelogMiner = new JiraChangelogMiner(pump);
        commentMiner = new JiraCommentMiner(pump);
        worklogMiner = new JiraWorklogMiner(pump);
        attachmentMiner = new JiraAttachmentMiner(pump);
    }

    @Override
    public void mineItems() {

        int index = 0;
        String query = String.format(QUERY_FORMAT, pump.getPi().getName());
        Iterable<BasicIssue> issues = null;
        try {
            issues = ((JiraRestClient) pump.getRootObject()).getSearchClient().searchJql(query, 1, 0).get().getIssues();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        if (issues == null) return;

        for (BasicIssue latestIssue : issues) {
            index = getNumberAfterLastDash(latestIssue.getKey());
            if (index > 0) break;
        }

        for (; index > 0; index--) {
            Issue issue = null;
            try {
                issue = ((JiraRestClient) pump.getRootObject()).getIssueClient().getIssue(String.format(ISSUE_KEY_FORMAT, pump.getPi().getName(), index)).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                // deleted issue
                generateDeletedIssue(index);
                continue;
            }
            mineItem(issue);
        }
    }

    @Override
    protected void mineItem(Issue issue) {
        WorkUnit unit = new WorkUnit();
        unit.setNumber(getNumberAfterLastDash(issue.getKey()));
        App.printLogMsg(Integer.toString(unit.getNumber()), false);
        unit.setExternalId(issue.getKey());
        unit.setUrl(String.format(ISSUE_URL_FORMAT, pump.getServer(), issue.getKey()));
        unit.setName(issue.getSummary());
        unit.setDescription(issue.getDescription());
        unit.setAuthor(addPerson(((JiraPeopleMiner) pump.getPeopleMiner()).generateIdentity(issue.getReporter())));
        unit.setCreated(issue.getCreationDate().toDate());
        unit.setStartDate(issue.getCreationDate().toDate());
        if (issue.getDueDate() != null) {
            unit.setDueDate(issue.getDueDate().toDate());
        }
        unit.setAssignee(addPerson(((JiraPeopleMiner) pump.getPeopleMiner()).generateIdentity(issue.getAssignee())));
        unit.setStatus(resolveStatus(issue.getStatus()));
        unit.setType(resolveType(issue.getIssueType().getName()));
        if (issue.getPriority() != null) {
            unit.setPriority(resolvePriority(issue.getPriority().getName()));
        }
        if (issue.getResolution() != null) {
            unit.setResolution(resolveResolution(issue.getResolution().getName()));
        }
        resolveCategories(unit, issue);
        unit.setSeverity(resolveSeverity(issue));

        pump.getPi().getProject().addUnit(unit);

        mineTimeTracking(unit, issue);
        if (issue.getAttachments() != null) {
            attachmentMiner.mineAttachments(unit, issue.getAttachments());
        }
        mineHistory(unit, issue);

        if (issue.getFixVersions() != null) {
            for (Version version : issue.getFixVersions()) {
                Iteration iteration = new Iteration();
                if (version.getId() != null) {
                    iteration.setExternalId(version.getId().toString());
                }
                if (version.getReleaseDate() != null) {
                    iteration.setEndDate(version.getReleaseDate().toDate());
                }
                if ((unit.getIteration() == null || unit.getIteration().getEndDate() == null) ||
                        (version.getReleaseDate() != null && unit.getIteration().getEndDate().after(version.getReleaseDate().toDate()))) {
                    unit.setIteration(iteration);
                }
            }
        }
    }

    @Override
    protected void resolveCategories(WorkUnit unit, Issue issue) {
        for (BasicComponent component : issue.getComponents()) {
            boolean found = false;
            for (Category category : pump.getPi().getCategories()) {
                if (category.getName().equals(component.getName())) {
                    unit.getCategories().add(category);
                    found = true;
                    break;
                }
            }
            if (!found) {
                Category newCategory = new Category();
                newCategory.setName(component.getName());
                newCategory.setDescription(component.getDescription());
                if (component.getId() != null) {
                    newCategory.setExternalId(component.getId().toString());
                }
                unit.getCategories().add(newCategory);
                pump.getPi().getCategories().add(newCategory);
            }
        }

        for (String label : issue.getLabels()) {
            Category fromLabel = resolveCategory(label);
            if (fromLabel != null) {
                unit.getCategories().add(fromLabel);
            }
        }

        Field tags = issue.getFieldByName(TAGS_FIELD_NAME);
        if (tags == null) {
            tags = issue.getFieldByName(TAGS_FIELD_NAME.toLowerCase());
        }
        if (tags != null) {
            for (String tag : tags.getValue().toString().split(SPACE)) {
                Category fromTag = resolveCategory(tag);
                App.printLogMsg(tag, false);
                if (fromTag != null) {
                    unit.getCategories().add(fromTag);
                }
            }
        }
    }

    @Override
    protected Severity resolveSeverity(Issue issue) {
        Field field = issue.getFieldByName(SEVERITY_FIELD_NAME);
        if (field == null) {
            field = issue.getFieldByName(SEVERITY_FIELD_NAME.toLowerCase());
        }
        if (field != null && field.getValue() != null) {
            String value = field.getValue().toString().trim();
            if (!value.isEmpty()) {
                for (Severity severity : pump.getPi().getSeverities()) {
                    if (toLetterOnlyLowerCase(value).equals(toLetterOnlyLowerCase(severity.getName()))) {
                        return severity;
                    }
                }
                Severity newSeverity = new Severity(value, severityDao.findByClass(SeverityClass.UNASSIGNED));
                pump.getPi().getSeverities().add(newSeverity);
                return newSeverity;
            }
        }
        return null;
    }

    @Override
    protected void mineWorklogs(WorkUnit unit, Issue issue) {
        double spentTime = 0;
        for (Worklog worklog : issue.getWorklogs()) {
            worklogMiner.generateLogTimeConfiguration(unit, spentTime, worklog);
            spentTime += minutesToHours(worklog.getMinutesSpent());
        }
    }

    @Override
    protected void mineComments(WorkUnit unit, Issue issue) {
        for (Comment comment : issue.getComments()) {
            commentMiner.generateUnitCommentConfig(unit, comment);
        }
    }

    private Status resolveStatus(BasicStatus basicStatus) {

        String description = "";
        try {
            description = ((JiraRestClient) pump.getRootObject()).getMetadataClient().getStatus(basicStatus.getSelf()).get().getDescription();
        } catch (InterruptedException | ExecutionException e) {
            // lost only status description
        }

        for (Status status : pump.getPi().getStatuses()) {
            if (toLetterOnlyLowerCase(status.getName()).equals(toLetterOnlyLowerCase(basicStatus.getName()))) {
                if (status.getExternalId() == null) {
                    status.setName(basicStatus.getName());
                    status.setExternalId(basicStatus.getSelf().toString());
                    status.setDescription(description);
                }
                return status;
            }
        }

        Status newStatus = new Status(basicStatus.getName(), statusDao.findByClass(StatusClass.UNASSIGNED));
        newStatus.setExternalId(basicStatus.getSelf().toString());
        newStatus.setDescription(description);
        pump.getPi().getStatuses().add(newStatus);
        return newStatus;
    }

    private void mineTimeTracking(WorkUnit unit, Issue issue) {
        if (issue.getTimeTracking() != null) {
            if (issue.getTimeTracking().getOriginalEstimateMinutes() != null) {
                unit.setEstimatedTime(minutesToHours(issue.getTimeTracking().getOriginalEstimateMinutes()));
            }
            if (issue.getTimeTracking().getTimeSpentMinutes() != null) {
                unit.setSpentTime(minutesToHours(issue.getTimeTracking().getTimeSpentMinutes()));
            }
            if (unit.getEstimatedTime() > 0) {
                int percentage = (int) (unit.getEstimatedTime() / unit.getSpentTime()) * PERCENTAGE_MAX;
                unit.setProgress(Math.min(percentage, PERCENTAGE_MAX));
            }
        }
    }

    private Category resolveCategory(String label) {
        if (!label.trim().isEmpty()) {
            for (Category category : pump.getPi().getCategories()) {
                if (toLetterOnlyLowerCase(category.getName()).equals(toLetterOnlyLowerCase(label))) {
                    return category;
                }
            }
            Category newCategory = new Category();
            newCategory.setName(label);
            pump.getPi().getCategories().add(newCategory);
            return newCategory;
        }
        return null;
    }

    @Override
    protected void mineHistory(WorkUnit unit, Issue issue) {
        super.mineHistory(unit, issue);

        if (issue.getChangelog() != null) {
            for (ChangelogGroup changelog : issue.getChangelog()) {
                changelogMiner.generateModification(unit, changelog);
            }
        }

        mineTransitions(unit, issue);

        if (unit.getStatus().getClassification().getSuperClass() == StatusSuperClass.CLOSED
                && issue.getUpdateDate() != null) {
            generateClosureConfig(unit, issue.getUpdateDate().toDate());
        }
    }

    private void mineTransitions(WorkUnit unit, Issue issue) {
        Iterable<Transition> transitions = new ArrayList<>();
        try {
            transitions = ((JiraRestClient) pump.getRootObject()).getIssueClient().getTransitions(issue).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        for (Transition transition : transitions) {
            generateTransitionConfig(unit, transition);
        }
    }

    private void generateTransitionConfig(WorkUnit unit, Transition transition) {
        WorkItemChange change = new WorkItemChange();
        change.setType(WorkItemChange.Type.MODIFY);
        change.setChangedItem(unit);

        App.printLogMsg(String.format(TRANSITION_FORMAT, transition.toString()), false);
        //change.getFieldChanges().add(new FieldChange("status", , ));

        Configuration configuration = new Configuration();
        //configuration.setCreated();
        configuration.getChanges().add(change);

        pump.getPi().getProject().getConfigurations().add(configuration);
    }
}
