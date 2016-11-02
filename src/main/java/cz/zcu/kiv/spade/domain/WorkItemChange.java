package cz.zcu.kiv.spade.domain;

import cz.zcu.kiv.spade.domain.abstracts.DescribedEntity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

@Entity
public class WorkItemChange extends DescribedEntity {

    private WorkItem changedItem;

    public WorkItemChange() {
    }

    public WorkItemChange(long id, String externalId, String name, String description, WorkItem changedItem) {
        super(id, externalId, name, description);
        this.changedItem = changedItem;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    public WorkItem getChangedItem() {
        return changedItem;
    }

    public void setChangedItem(WorkItem changedItem) {
        this.changedItem = changedItem;
    }

}
