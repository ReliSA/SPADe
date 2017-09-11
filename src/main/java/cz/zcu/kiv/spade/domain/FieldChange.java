package cz.zcu.kiv.spade.domain;

import cz.zcu.kiv.spade.domain.abstracts.NamedEntity;

import javax.persistence.Column;
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

    public FieldChange(String name, String oldValue, String newValue) {
        super();
        this.name = name;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    @Column(columnDefinition = "longtext")
    public String getOldValue() {
        return oldValue;
    }

    public void setOldValue(String oldValue) {
        this.oldValue = oldValue;
    }

    @Column(columnDefinition = "longtext")
    public String getNewValue() {
        return newValue;
    }

    public void setNewValue(String newValue) {
        this.newValue = newValue;
    }
}
