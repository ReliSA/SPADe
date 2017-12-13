package cz.zcu.kiv.spade.pumps.issuetracking;

import cz.zcu.kiv.spade.domain.WorkItemChange;
import cz.zcu.kiv.spade.domain.WorkUnit;
import cz.zcu.kiv.spade.pumps.DataMiner;
import cz.zcu.kiv.spade.pumps.DataPump;

public abstract class CommentMiner<CommentObject> extends DataMiner {

    protected CommentMiner(DataPump pump) {
        super(pump);
    }

    /**
     * generates a Configuration representing a comment being added to a Work Unit (issue)
     *
     * @param unit    Work Unit to link the comment Configuration to
     * @param comment an issue comment form GitHub
     */
    protected abstract void generateUnitCommentConfig(WorkUnit unit, CommentObject comment);

    protected WorkItemChange generateCommentChange(WorkUnit unit) {
        WorkItemChange change = new WorkItemChange();
        change.setType(WorkItemChange.Type.COMMENT);
        change.setChangedItem(unit);
        return change;
    }
}
