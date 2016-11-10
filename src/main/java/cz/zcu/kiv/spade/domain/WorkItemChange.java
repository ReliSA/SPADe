package cz.zcu.kiv.spade.domain;

import cz.zcu.kiv.spade.domain.abstracts.DescribedEntity;

import javax.persistence.*;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

@Entity
public class WorkItemChange extends DescribedEntity {

    private WorkItem changedItem;
    private Collection<FieldChange> fieldChanges;

    public WorkItemChange() {
        super();
        fieldChanges = new LinkedHashSet<>();
    }

    @ManyToOne(fetch = FetchType.LAZY)
    public WorkItem getChangedItem() {
        return changedItem;
    }

    public void setChangedItem(WorkItem changedItem) {
        this.changedItem = changedItem;
    }

    @OneToMany
    @JoinColumn(name = "item_change_id")
    public Collection<FieldChange> getFieldChanges() {
        return fieldChanges;
    }

    public void setFieldChanges(Collection<FieldChange> fieldChanges) {
        this.fieldChanges = fieldChanges;
    }
}
