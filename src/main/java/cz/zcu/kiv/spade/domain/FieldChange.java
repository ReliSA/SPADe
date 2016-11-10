package cz.zcu.kiv.spade.domain;

import cz.zcu.kiv.spade.domain.abstracts.NamedEntity;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "field_change")
public class FieldChange extends NamedEntity {

    private String oldValue;
    private String newValue;

    public FieldChange() {
        super();
    }

    public String getOldValue() {
        return oldValue;
    }

    public void setOldValue(String oldValue) {
        this.oldValue = oldValue;
    }

    public String getNewValue() {
        return newValue;
    }

    public void setNewValue(String newValue) {
        this.newValue = newValue;
    }
}
