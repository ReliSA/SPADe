package cz.zcu.kiv.spade.pumps.issuetracking;

import cz.zcu.kiv.spade.domain.*;
import cz.zcu.kiv.spade.domain.enums.StatusClass;
import cz.zcu.kiv.spade.domain.enums.StatusSuperClass;
import cz.zcu.kiv.spade.pumps.ItemMiner;

import java.util.Date;

public abstract class IssueMiner<IssueObject> extends ItemMiner<IssueObject> {

    protected static final String PLUS = "\\+";
    private static final String CLOSURE_DESC = "issue closed";
    private static final String STATUS_FIELD_NAME = "status";
    private static final String OPEN_STATUS_NAME = "open";

    protected static final String ISSUES_MINED_FORMAT = "%s tickets mined";
    protected static final int ISSUES_BATCH_SIZE = 100;

    protected IssueMiner(IssueTrackingPump pump) {
        super(pump);
    }

    /**
     * mines issue categories and tags/labels/components, matches them to the ones used in project or creates new ones in the project
     *
     * @param issue issue
     * @param unit  work unit to store data in
     */
    protected abstract void resolveCategories(WorkUnit unit, IssueObject issue);

    /**
     * matches the issue severity to one of the values used in the project
     *
     * @param issue issue
     * @return corresponding Severity instance or null
     */
    protected abstract Severity resolveSeverity(IssueObject issue);

    /**
     * mines spent time log entries
     *
     * @param unit  work unit to save data in
     * @param issue issue to mine worklogs from
     */
    protected abstract void mineWorklogs(WorkUnit unit, IssueObject issue);

    protected abstract void mineComments(WorkUnit unit, IssueObject issue);

    /**
     * matches the issue status to one of the values used in the project
     *
     * @param name status value
     * @return corresponding Status instance or null
     */
    protected Status resolveStatus(String name) {
        for (Status status : pump.getPi().getStatuses()) {
            if (name.equals(status.getName())) return status;
        }
        return null;
    }

    /**
     * matches the issue type to one of the values used in the project
     *
     * @param name type value
     * @return corresponding WorkUnitType instance or null
     */
    protected WorkUnitType resolveType(String name) {
        for (WorkUnitType type : pump.getPi().getWuTypes()) {
            if (name.equals(type.getName())) return type;
        }
        return null;
    }

    /**
     * matches the issue priority to one of the values used in the project
     *
     * @param name priority value
     * @return corresponding Priority instance or null
     */
    protected Priority resolvePriority(String name) {
        for (Priority priority : pump.getPi().getPriorities()) {
            if (name.equals(priority.getName())) return priority;
        }
        return null;
    }

    /**
     * matches the issue severity to one of the values used in the project
     *
     * @param name severity value
     * @return corresponding Severity instance or null
     */
    protected Severity resolveSeverity(String name) {
        for (Severity severity : pump.getPi().getSeverities()) {
            if (name.equals(severity.getName())) return severity;
        }
        return null;
    }

    /**
     * matches the issue resolution to one of the values used in the project
     *
     * @param name resolution value
     * @return corresponding Resolution instance or null
     */
    protected Resolution resolveResolution(String name) {
        for (Resolution resolution : pump.getPi().getResolutions()) {
            if (name.equals(resolution.getName())) return resolution;
        }
        return null;
    }



    /**
     * mines a configuration/action of closing an issue
     *
     * @param unit     WorkUnit (issue)
     * @param closedOn date of closure
     */
    protected Configuration generateClosureConfig(WorkUnit unit, Date closedOn) {
        WorkItemChange change = new WorkItemChange();
        change.setType(WorkItemChange.Type.MODIFY);
        change.setDescription(CLOSURE_DESC);
        change.setChangedItem(unit);

        String openStatus = "";
        for (Status status : pump.getPi().getStatuses()) {
            if (status.getClassification().getaClass().equals(StatusClass.OPEN)) {
                openStatus = status.getName();
                break;
            }
        }
        if (openStatus.isEmpty()) {
            for (Status status : pump.getPi().getStatuses()) {
                if (status.getClassification().getSuperClass().equals(StatusSuperClass.OPEN)) {
                    openStatus = status.getName();
                    break;
                }
            }
        }
        if (openStatus.isEmpty()) {
            Status newStatus = new Status(OPEN_STATUS_NAME, new StatusClassification(StatusClass.OPEN));
            pump.getPi().getStatuses().add(newStatus);
            openStatus = newStatus.getName();
        }

        change.getFieldChanges().add(new FieldChange(STATUS_FIELD_NAME, openStatus, unit.getStatus().getName()));

        Configuration closure = new Configuration();
        closure.setCreated(closedOn);
        closure.getChanges().add(change);

        pump.getPi().getProject().getConfigurations().add(closure);
        return closure;
    }

    /**
     * mines issue history
     *
     * @param unit  work unit to save data in
     * @param issue issue to mine history from
     */
    protected void mineHistory(WorkUnit unit, IssueObject issue) {
        generateCreationConfig(unit);
        mineWorklogs(unit, issue);
        mineComments(unit, issue);
    }

    /**
     * mines a configuration/action of creating an item (issue/artifact)
     *
     * @param item work item
     */
    private void generateCreationConfig(WorkItem item) {

        WorkItemChange change = new WorkItemChange();
        change.setType(WorkItemChange.Type.ADD);
        change.setChangedItem(item);

        Configuration creation = new Configuration();
        creation.setCreated(item.getCreated());
        creation.setAuthor(item.getAuthor());
        creation.getChanges().add(change);

        pump.getPi().getProject().getConfigurations().add(creation);
    }
}
