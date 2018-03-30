package cz.zcu.kiv.spade.pumps.issuetracking.redmine;

import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.bean.*;
import com.taskadapter.redmineapi.bean.Project;
import cz.zcu.kiv.spade.App;
import cz.zcu.kiv.spade.domain.*;
import cz.zcu.kiv.spade.pumps.DataPump;
import cz.zcu.kiv.spade.pumps.issuetracking.IssueMiner;

import java.util.ArrayList;
import java.util.List;

class RedmineIssueMiner extends IssueMiner<Issue> {

    private static final String LOGTIME_PERMISSION_ERR_FORMAT = "Insufficient permissions for log time for issue: %d";
    private static final String QUERIES_PERMISSION_ERR_MSG = "Insufficient permissions for queries";
    private static final String ISSUES_PERMISSION_ERR_MSG = "Insufficient permissions for issues";
    private static final String TAGS_FIELD_NAME = "tags";
    private static final String LABELS_FIELD_NAME = "labels";
    private static final String ISSUE_URL_FORMAT = "https://%s/issues/%d";
    private static final String QUERY_NAME = "allissues";
    private static final int QUERY_ID = 73;
    static final String ISSUES_COUNT_FORMAT = "%s issues to mine";

    private final RedmineChangelogMiner changelogMiner;
    private final RedmineWorklogMiner worklogMiner;
    //private final RedmineAttachmentMiner attachmentMiner;

    RedmineIssueMiner(RedminePump pump) {
        super(pump);
        changelogMiner = new RedmineChangelogMiner(pump);
        worklogMiner = new RedmineWorklogMiner(pump);
        //attachmentMiner = new RedmineAttachmentMiner(pump);
    }

    @Override
    public void mineItems() {
        Project redmineProject = (Project) pump.getSecondaryObject();
        List<Issue> issues = new ArrayList<>();
        int queryId = QUERY_ID;
        try {
            for (SavedQuery query : ((RedmineManager) pump.getRootObject()).getIssueManager().getSavedQueries()) {
                if (toLetterOnlyLowerCase(query.getName()).equals(QUERY_NAME)) {
                    queryId = query.getId();
                }
            }
        } catch (RedmineException e) {
            App.printLogMsg(this, QUERIES_PERMISSION_ERR_MSG);
        }

        try {
            issues = ((RedmineManager) pump.getRootObject()).getIssueManager().getIssues(redmineProject.getIdentifier(), queryId);
        } catch (RedmineException e) {
            App.printLogMsg(this, ISSUES_PERMISSION_ERR_MSG);
        }

        int i = 0;
        App.printLogMsg(this, String.format(ISSUES_COUNT_FORMAT, issues.size()));
        for (Issue issue : issues) {
            mineItem(issue);
            i++;
            if (i % ISSUES_BATCH_SIZE == 0 || i == issues.size()) {
                App.printLogMsg(this, String.format(ISSUES_MINED_FORMAT, (i + DataPump.SLASH + issues.size())));
            }
        }
    }

    @Override
    protected void mineItem(Issue issue) {
        WorkUnit unit = new WorkUnit();
        unit.setNumber(issue.getId());
        unit.setExternalId(issue.getId().toString());
        unit.setUrl(String.format(ISSUE_URL_FORMAT, pump.getServer(), issue.getId()));
        unit.setName(issue.getSubject());
        unit.setDescription(issue.getDescription());
        unit.setAuthor(addPerson(((RedminePeopleMiner) pump.getPeopleMiner()).generateIdentity(issue.getAuthorId(), issue.getAuthorName())));
        unit.setCreated(issue.getCreatedOn());
        unit.setStartDate((issue.getStartDate() == null) ? issue.getCreatedOn() : issue.getStartDate());
        unit.setDueDate(issue.getDueDate());
        unit.setAssignee(addPerson(((RedminePeopleMiner) pump.getPeopleMiner()).generateIdentity(issue.getAssigneeId(), issue.getAssigneeName())));
        unit.setStatus(resolveStatus(issue.getStatusName()));
        unit.setType(resolveType(issue.getTracker().getName()));
        unit.setPriority(resolvePriority(issue.getPriorityText()));
        unit.setEstimatedTime((issue.getEstimatedHours() == null) ? 0 : issue.getEstimatedHours());
        unit.setProgress(issue.getDoneRatio());
        resolveCategories(unit, issue);
        unit.setSeverity(resolveSeverity(issue));

        pump.getPi().getProject().addUnit(unit);

        //attachmentMiner.mineAttachments(unit, issue.getAttachments());
        mineHistory(unit, issue);

        if (issue.getTargetVersion() != null) {
            Iteration iteration = new Iteration();
            iteration.setExternalId(issue.getTargetVersion().getId().toString());
            unit.setIteration(iteration);
        }
    }

    @Override
    protected void resolveCategories(WorkUnit unit, Issue issue) {
        if (issue.getCategory() != null) {
            for (Category category : pump.getPi().getCategories()) {
                if (category.getName().equals(issue.getCategory().getName())) {
                    unit.getCategories().add(category);
                    break;
                }
            }
        }

        for (CustomField field : issue.getCustomFields()) {
            if (field.getName().toLowerCase().equals(TAGS_FIELD_NAME) || field.getName().toLowerCase().equals(LABELS_FIELD_NAME)) {
                String value = issue.getCustomFieldByName(field.getName()).getValue();
                if (value != null && !value.trim().isEmpty()) {
                    boolean found = false;
                    for (Category category : pump.getPi().getCategories()) {
                        if (category.getName().equals(value)) {
                            unit.getCategories().add(category);
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        Category newCategory = new Category();
                        newCategory.setName(value);
                        unit.getCategories().add(newCategory);
                        pump.getPi().getCategories().add(newCategory);
                    }
                }
            }
        }
    }

    @Override
    protected Severity resolveSeverity(Issue issue) {
        for (CustomField field : issue.getCustomFields()) {
            if (field.getName().toLowerCase().equals(SEVERITY_FIELD_NAME)) {
                if (field.getValue() == null || field.getValue().isEmpty()) continue;
                return resolveSeverity(field.getValue());
            }
        }
        return null;
    }

    @Override
    protected void mineHistory(WorkUnit unit, Issue issue) {
        super.mineHistory(unit, issue);

        changelogMiner.parseHistory(unit);

        /*for (Journal journal : issue.getJournals()) {
            changelogMiner.generateModification(unit, journal);
        }

        if (issue.getClosedOn() != null) {
            generateClosureConfig(unit, issue.getClosedOn());
        }*/
    }

    @Override
    protected void mineWorklogs(WorkUnit unit, Issue issue) {
        double spentTime = 0;
        List<TimeEntry> entries = new ArrayList<>();
        try {
            entries = ((RedmineManager) pump.getRootObject()).getTimeEntryManager().getTimeEntriesForIssue(issue.getId());
        } catch (RedmineException e) {
            App.printLogMsg(this, String.format(LOGTIME_PERMISSION_ERR_FORMAT, issue.getId()));
        }
        for (TimeEntry entry : entries) {
            worklogMiner.generateLogTimeConfiguration(unit, spentTime, entry);
            spentTime += entry.getHours();
        }
        unit.setSpentTime(spentTime);
    }

    @Override
    protected void mineComments(WorkUnit unit, Issue issue) {
        // handled in mineWorklogs (getComment) and generateModification (getNotes)
    }
}
