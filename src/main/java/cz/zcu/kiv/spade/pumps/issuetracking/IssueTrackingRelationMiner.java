package cz.zcu.kiv.spade.pumps.issuetracking;

import cz.zcu.kiv.spade.pumps.RelationMiner;

public abstract class IssueTrackingRelationMiner extends RelationMiner {

    protected IssueTrackingRelationMiner(IssueTrackingPump pump) {
        super(pump);
    }

    public void mineAllRelations() {
        mineRelations();
        super.mineAllRelations();
    }

    /**
     * mines relation between issues
     */
    protected abstract void mineRelations();
}
