package cz.zcu.kiv.spade.domain;

import cz.zcu.kiv.spade.domain.abstracts.BaseEntity;

import javax.persistence.*;

@Entity
@Table(name = "work_item_relation")
public class WorkItemRelation extends BaseEntity {

    private WorkItem relatedItem;
    private Relation relation;

    public WorkItemRelation() {
        super();
    }

    public WorkItemRelation(WorkItem relatedItem, Relation relation) {
        this.relatedItem = relatedItem;
        this.relation = relation;
    }

    @JoinColumn(name = "rightItemId")
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    public WorkItem getRelatedItem() {
        return relatedItem;
    }

    public void setRelatedItem(WorkItem relatedItem) {
        this.relatedItem = relatedItem;
    }

    @JoinColumn(name = "relationId")
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    public Relation getRelation() {
        return relation;
    }

    public void setRelation(Relation relation) {
        this.relation = relation;
    }
}
