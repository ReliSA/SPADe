package cz.zcu.kiv.ppicha.spade.domain;

import cz.zcu.kiv.ppicha.spade.domain.abstracts.BaseEntity;

import javax.persistence.*;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "WorkItemChange")
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

    @Column(nullable = false, updatable = false)
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
