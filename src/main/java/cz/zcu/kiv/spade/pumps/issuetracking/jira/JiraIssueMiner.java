package cz.zcu.kiv.spade.pumps.issuetracking.jira;

import com.atlassian.jira.rest.client.IssueRestClient;
import com.atlassian.jira.rest.client.JiraRestClient;
import com.atlassian.jira.rest.client.domain.*;
import cz.zcu.kiv.spade.App;
import cz.zcu.kiv.spade.domain.*;
import cz.zcu.kiv.spade.domain.Status;
import cz.zcu.kiv.spade.domain.enums.SeverityClass;
import cz.zcu.kiv.spade.domain.enums.StatusClass;
import cz.zcu.kiv.spade.domain.enums.StatusSuperClass;
import cz.zcu.kiv.spade.domain.enums.Tool;
import cz.zcu.kiv.spade.pumps.DataPump;
import cz.zcu.kiv.spade.pumps.issuetracking.IssueMiner;
import javafx.util.Pair;

import java.util.*;
import java.util.concurrent.ExecutionException;

class JiraIssueMiner extends IssueMiner<Issue> {

    private static final String ISSUE_KEY_FORMAT = "%s-%d";
    private static final String QUERY_FORMAT = "project = %s";
    private static final String ISSUE_URL_FORMAT = "https://%s/" + Tool.JIRA.name().toLowerCase() + "/browse/%s";
    private static final String TAGS_FIELD_NAME = "Tags";
    private static final String SEVERITY_FIELD_NAME = "Severity";

    private final JiraChangelogMiner changelogMiner;
    private final JiraCommentMiner commentMiner;
    private final JiraWorklogMiner worklogMiner;

    private Map<String, WorkUnit> parents;
    private Map<String, Set<Pair<IssueLink, WorkUnit>>> links;

    JiraIssueMiner(JiraPump pump) {
        super(pump);
        changelogMiner = new JiraChangelogMiner(pump);
        commentMiner = new JiraCommentMiner(pump);
        worklogMiner = new JiraWorklogMiner(pump);
        parents = new HashMap<>();
        links = new HashMap<>();
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

        List<IssueRestClient.Expandos> expandos = new ArrayList<>();
        expandos.add(IssueRestClient.Expandos.CHANGELOG);
        for (; index > 0; index--) {
            if (index % 100 == 0) {
                App.printLogMsg(this, Integer.toString(index));
            }
            String issueKey = String.format(ISSUE_KEY_FORMAT, pump.getPi().getName(), index);
            try {
                Issue issue = ((JiraRestClient) pump.getRootObject()).getIssueClient().getIssue(issueKey, expandos).get();
                mineItem(issue);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                // deleted issue
                generateDeletedIssue(index);
            }
        }
    }

    @Override
    protected void mineItem(Issue issue) {
        WorkUnit unit = new WorkUnit();
        unit.setNumber(getNumberAfterLastDash(issue.getKey()));
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

        mineHistory(unit, issue);

        if (issue.getFixVersions() != null) {
            for (Version version : issue.getFixVersions()) {
                Iteration iteration = new Iteration();
                if (version.getName() != null) {
                    iteration.setExternalId(version.getName());
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
        mineRelations(unit, issue);
    }

    private void mineRelations(WorkUnit unit, Issue issue) {
        if (issue.getIssueLinks() != null) {
            for (IssueLink link : issue.getIssueLinks()) {
                WorkUnit related;
                if ((related = pump.getPi().getProject().getUnit(getNumberAfterLastDash(link.getTargetIssueKey()))) != null) {
                    unit.getRelatedItems().add(new WorkItemRelation(related, resolveRelation(link.getIssueLinkType().getName())));
                } else {
                    saveLink(unit, link);
                }
            }
        }
        if (issue.getSubtasks() != null) {
            for (Subtask subtask : issue.getSubtasks()) {
                WorkUnit child ;
                if ((child = pump.getPi().getProject().getUnit(getNumberAfterLastDash(subtask.getIssueKey()))) != null) {
                    unit.getRelatedItems().add(new WorkItemRelation(child, resolveRelation(PARENT_OF)));
                    child.getRelatedItems().add(new WorkItemRelation(unit, resolveRelation(CHILD_OF)));
                } else {
                    parents.put(subtask.getIssueKey(), unit);
                }
            }
        }
        completePastRelations(unit);
    }

    private void saveLink(WorkUnit unit, IssueLink link) {
        if (links.containsKey(link.getTargetIssueKey())) {
            Pair<IssueLink, WorkUnit> relation = new Pair<>(link, unit);
            links.get(link.getTargetIssueKey()).add(relation);
        } else {
            Set<Pair<IssueLink, WorkUnit>> set = new LinkedHashSet<>();
            set.add(new Pair<>(link, unit));
            links.put(link.getTargetIssueKey(), set);
        }
    }

    private void completePastRelations(WorkUnit unit) {
        String key = unit.getExternalId();
        if (links.containsKey(key)) {
            for (Pair<IssueLink, WorkUnit> relation : links.get(key)) {
                IssueLink link = relation.getKey();
                WorkUnit related = relation.getValue();
                related.getRelatedItems().add(new WorkItemRelation(unit, resolveRelation(link.getIssueLinkType().getName())));
            }
            links.remove(key);
        }
        if (parents.containsKey(key)) {
            WorkUnit parent = parents.get(key);
            parent.getRelatedItems().add(new WorkItemRelation(unit, resolveRelation(PARENT_OF)));
            unit.getRelatedItems().add(new WorkItemRelation(parent, resolveRelation(CHILD_OF)));
            parents.remove(key);
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
            for (String tag : tags.getValue().toString().split(DataPump.SPACE)) {
                Category fromTag = resolveCategory(tag);
                App.printLogMsg(this, tag);
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
                Severity newSeverity = new Severity(value, new SeverityClassification(SeverityClass.UNASSIGNED));
                pump.getPi().getSeverities().add(newSeverity);
                return newSeverity;
            }
        }
        return null;
    }

    @Override
    protected void mineWorklogs(WorkUnit unit, Issue issue) {
        worklogMiner.mineTimeTracking(unit, issue);

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

        Status newStatus = new Status(basicStatus.getName(), new StatusClassification(StatusClass.UNASSIGNED));
        newStatus.setExternalId(basicStatus.getSelf().toString());
        newStatus.setDescription(description);
        pump.getPi().getStatuses().add(newStatus);
        return newStatus;
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

        if (unit.getStatus().getClassification().getSuperClass() == StatusSuperClass.CLOSED
                && issue.getUpdateDate() != null) {
            generateClosureConfig(unit, issue.getUpdateDate().toDate());
        }
    }
}