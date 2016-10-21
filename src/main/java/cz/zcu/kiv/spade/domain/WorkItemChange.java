package cz.zcu.kiv.spade.domain;

import cz.zcu.kiv.spade.domain.abstracts.BaseEntity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
public class WorkItemChange extends BaseEntity {

    private String description;
    private WorkItem changedItem;

    public WorkItemChange() {
    }

    public WorkItemChange(long id, String externalId, String description, WorkItem changedItem) {
        super(id, externalId);
        this.description = description;
        this.changedItem = changedItem;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String type) {
        this.description = description;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    public WorkItem getChangedItem() {
        return changedItem;
    }

    public void setChangedItem(WorkItem changedItem) {
        this.changedItem = changedItem;
    }

}
