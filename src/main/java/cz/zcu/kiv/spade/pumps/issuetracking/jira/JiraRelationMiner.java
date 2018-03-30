package cz.zcu.kiv.spade.pumps.issuetracking.jira;

import cz.zcu.kiv.spade.domain.*;
import cz.zcu.kiv.spade.pumps.issuetracking.IssueTrackingRelationMiner;

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
    }
}
