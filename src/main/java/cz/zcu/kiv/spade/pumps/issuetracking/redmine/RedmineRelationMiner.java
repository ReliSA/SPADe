package cz.zcu.kiv.spade.pumps.issuetracking.redmine;

import com.taskadapter.redmineapi.Include;
import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.bean.Changeset;
import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.bean.IssueRelation;
import cz.zcu.kiv.spade.domain.*;
import cz.zcu.kiv.spade.domain.enums.ArtifactClass;
import cz.zcu.kiv.spade.pumps.issuetracking.IssueTrackingRelationMiner;

class RedmineRelationMiner extends IssueTrackingRelationMiner {

    RedmineRelationMiner(RedminePump pump) {
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
                    // from wiki page texts
                    if (change.getChangedItem() instanceof Artifact &&
                            ((Artifact) change.getChangedItem()).getArtifactClass().equals(ArtifactClass.WIKIPAGE)) {
                        mineAllMentionedItems(change.getChangedItem());
                    }
                    // from work log and changelog comments
                    if (change.getChangedItem() instanceof WorkUnit &&
                            (change.getType().equals(WorkItemChange.Type.MODIFY) ||
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
            Issue issue = null;
            try {
                issue = ((RedmineManager) pump.getRootObject()).getIssueManager().getIssueById(unit.getNumber(), Include.relations);
            } catch (RedmineException e) {
                e.printStackTrace();
            }
            if (issue != null) {
                if (issue.getParentId() != null) {
                    WorkUnit parent = pump.getPi().getProject().getUnit(issue.getParentId());
                    unit.getRelatedItems().add(new WorkItemRelation(parent, resolveRelation(CHILD_OF)));
                    parent.getRelatedItems().add(new WorkItemRelation(unit, resolveRelation(PARENT_OF)));
                }
                for (IssueRelation relation : issue.getRelations()) {
                    WorkUnit related = pump.getPi().getProject().getUnit(relation.getIssueToId());
                    unit.getRelatedItems().add(new WorkItemRelation(related, resolveRelation(relation.getType())));
                }
                mineRevisions(unit, issue);
            }
        }
    }

    /**
     * mines revisions linked to the issue
     *
     * @param unit  work unit to store data in
     * @param issue issue to mine revisions from
     */
    private void mineRevisions(WorkUnit unit, Issue issue) {
        for (Changeset changeset : issue.getChangesets()) {
            if (pump.getPi().getProject().containsCommit(changeset.getRevision())) {
                Commit commit = pump.getPi().getProject().getCommit(changeset.getRevision());
                generateMentionRelation(commit, unit);
            }
        }
    }
}
