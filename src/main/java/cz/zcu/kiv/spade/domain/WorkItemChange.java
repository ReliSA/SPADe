package cz.zcu.kiv.spade.domain;

import cz.zcu.kiv.spade.domain.abstracts.DescribedEntity;

import javax.persistence.*;
import java.util.Collection;
import java.util.LinkedHashSet;

@Entity
@Table(name = "work_item_change")
public class WorkItemChange extends DescribedEntity {

    public enum Type {
        COMMENT,
        MODIFY,
        LOGTIME,
        ADD,
        RENAME,
        COPY,
        MOVE,
        DELETE
    }

    private WorkItem changedItem;
    private Collection<FieldChange> fieldChanges;
    private Type type;

    public WorkItemChange() {
        super();
        fieldChanges = new LinkedHashSet<>();
        type = Type.MODIFY;
    }

    @Transient
    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.name = type.name();
        this.type = type;
    }

    @Transient
    @Override
    public String getName() {
        return this.type.name();
    }

    @Override
    public void setName(String name) {
        this.type = Type.valueOf(name);
        this.name = name;
    }

    @JoinColumn(name = "workItemId")
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    public WorkItem getChangedItem() {
        return changedItem;
    }

    public void setChangedItem(WorkItem changedItem) {
        this.changedItem = changedItem;
    }

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "workItemChangeId")
    public Collection<FieldChange> getFieldChanges() {
        return fieldChanges;
    }

    public void setFieldChanges(Collection<FieldChange> fieldChanges) {
        this.fieldChanges = fieldChanges;
    }
}
