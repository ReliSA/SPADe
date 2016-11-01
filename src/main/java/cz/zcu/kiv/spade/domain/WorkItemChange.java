package cz.zcu.kiv.spade.domain;

import cz.zcu.kiv.spade.domain.abstracts.BaseEntity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

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

    public void setDescription(String description) {
        this.description = description;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    public WorkItem getChangedItem() {
        return changedItem;
    }

    public void setChangedItem(WorkItem changedItem) {
        this.changedItem = changedItem;
    }

    @Override
    public String toString() {
        return "ChangedItem: " + changedItem.getName() + "\n" +
                "Description:\n" +
                "\t" + description + "\n";
    }
}
