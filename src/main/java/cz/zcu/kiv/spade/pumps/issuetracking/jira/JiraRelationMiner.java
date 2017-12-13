package cz.zcu.kiv.spade.pumps.issuetracking.jira;

import com.atlassian.jira.rest.client.JiraRestClient;
import com.atlassian.jira.rest.client.domain.Issue;
import com.atlassian.jira.rest.client.domain.IssueLink;
import com.atlassian.jira.rest.client.domain.Subtask;
import cz.zcu.kiv.spade.App;
import cz.zcu.kiv.spade.domain.*;
import cz.zcu.kiv.spade.pumps.issuetracking.IssueTrackingRelationMiner;

import java.util.concurrent.ExecutionException;

class JiraRelationMiner extends IssueTrackingRelationMiner {

    JiraRelationMiner(JiraPump pump) {
        super(pump);
    }

    @Override
    protected void mineMentions() {
        // from work unit descriptions
        for (WorkUnit unit : pump.getPi().getProject().getUnits()) {
            mineAllMentionedItems(unit);
        }

        for (Configuration configuration : pump.getPi().getProject().getConfigurations()) {
            if (!(configuration instanceof Commit)) {
                for (WorkItemChange change : configuration.getChanges()) {
                    // from work unit comments and time logs
                    if (change.getChangedItem() instanceof WorkUnit &&
                            (change.getType().equals(WorkItemChange.Type.COMMENT) ||
                                    change.getType().equals(WorkItemChange.Type.LOGTIME))) {
                        mineAllMentionedItems(change.getChangedItem(), configuration.getDescription());
                    }
                }
            }
        }
    }

    @Override
    protected void mineRelations() {
        for (WorkUnit unit : pump.getPi().getProject().getUnits()) {
            try {
                Issue issue = ((JiraRestClient) pump.getRootObject()).getIssueClient().getIssue(unit.getExternalId()).get();
                if (issue.getIssueLinks() != null) {
                    for (IssueLink link : issue.getIssueLinks()) {
                        WorkUnit related = pump.getPi().getProject().getUnit(getNumberAfterLastDash(link.getTargetIssueKey()));
                        unit.getRelatedItems().add(new WorkItemRelation(related, resolveRelation(link.getIssueLinkType().getName())));
                    }
                }
                if (issue.getSubtasks() != null) {
                    for (Subtask subtask : issue.getSubtasks()) {
                        WorkUnit child = pump.getPi().getProject().getUnit(getNumberAfterLastDash(subtask.getIssueKey()));
                        App.printLogMsg(String.format(PARENT_CHILD_FORMAT, unit.getNumber(), child.getNumber()), false);
                        unit.getRelatedItems().add(new WorkItemRelation(child, resolveRelation(PARENT_OF)));
                        child.getRelatedItems().add(new WorkItemRelation(unit, resolveRelation(CHILD_OF)));
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                // deleted issue, nothing to mine
            }
        }
    }
}
