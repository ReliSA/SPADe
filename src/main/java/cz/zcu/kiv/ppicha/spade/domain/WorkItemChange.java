package cz.zcu.kiv.ppicha.spade.domain;

import cz.zcu.kiv.ppicha.spade.domain.abstracts.BaseEntity;
import cz.zcu.kiv.ppicha.spade.domain.enums.ChangeType;

import javax.persistence.*;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "WorkItemChange")
public class WorkItemChange extends BaseEntity {

    private ChangeType type;
    private String comment;
    private Date timestamp;
    private Set<WorkItem> changedItems;
    private Person author;

    public WorkItemChange() {
        this.changedItems = new LinkedHashSet<>();
    }

    public WorkItemChange(long id, long externalId, ChangeType type, String comment, Date timestamp,
                          Set<WorkItem> changedItems, Person author) {
        super(id, externalId);
        this.type = type;
        this.comment = comment;
        this.timestamp = timestamp;
        this.changedItems = changedItems;
        this.author = author;
    }

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false, updatable = false)
    public ChangeType getType() {
        return type;
    }

    public void setType(ChangeType type) {
        this.type = type;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Column(nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    @ManyToMany
    @JoinTable(name = "WorkItemChange_WorkItem", joinColumns = @JoinColumn(name = "work_item_change", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "changed_item", referencedColumnName = "id"))
    public Set<WorkItem> getChangedItems() {
        return changedItems;
    }

    public void setChangedItems(Set<WorkItem> changedItems) {
        this.changedItems = changedItems;
    }

    //@Column(nullable = false, updatable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    public Person getAuthor() {
        return author;
    }

    public void setAuthor(Person author) {
        this.author = author;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        WorkItemChange workItemChange = (WorkItemChange) o;

        if (type != workItemChange.type) return false;
        if (comment != null ? !comment.equals(workItemChange.comment) : workItemChange.comment != null) return false;
        if (timestamp != null ? !timestamp.equals(workItemChange.timestamp) : workItemChange.timestamp != null) return false;
        if (changedItems != null ? !changedItems.equals(workItemChange.changedItems) : workItemChange.changedItems != null)
            return false;
        return !(author != null ? !author.equals(workItemChange.author) : workItemChange.author != null);

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (comment != null ? comment.hashCode() : 0);
        result = 31 * result + (timestamp != null ? timestamp.hashCode() : 0);
        result = 31 * result + (changedItems != null ? changedItems.hashCode() : 0);
        result = 31 * result + (author != null ? author.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "WorkItemChange{" +
                "type=" + type +
                ", comment='" + comment + '\'' +
                ", timestamp=" + timestamp +
                ", changedItems=" + changedItems +
                ", author=" + author +
                '}';
    }
}
