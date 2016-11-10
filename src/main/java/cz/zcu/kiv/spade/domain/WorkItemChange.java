package cz.zcu.kiv.spade.domain;

import cz.zcu.kiv.spade.domain.abstracts.DescribedEntity;

import javax.persistence.*;

@Entity
public class WorkItemChange extends DescribedEntity {

    private WorkItem changedItem;

    public WorkItemChange() {
        super();
    }

    @ManyToOne(fetch = FetchType.LAZY)
    public WorkItem getChangedItem() {
        return changedItem;
    }

    public void setChangedItem(WorkItem changedItem) {
        this.changedItem = changedItem;
    }

}
