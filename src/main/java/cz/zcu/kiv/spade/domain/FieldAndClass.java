package cz.zcu.kiv.spade.domain;

import cz.zcu.kiv.spade.domain.abstracts.BaseEntity;
import cz.zcu.kiv.spade.domain.enums.EnumClass;
import cz.zcu.kiv.spade.domain.enums.EnumField;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

@Entity
@Table(name = "field_and_class")
public class FieldAndClass extends BaseEntity {

    private EnumField enumField;
    private EnumClass enumClass;

    public FieldAndClass() {
    }

    public FieldAndClass(EnumField enumField, EnumClass enumClass) {
        this.enumField = enumField;
        this.enumClass = enumClass;
    }

    @Enumerated(value = EnumType.STRING)
    public EnumField getEnumField() {
        return enumField;
    }

    public void setEnumField(EnumField enumField) {
        this.enumField = enumField;
    }

    @Enumerated(value = EnumType.STRING)
    public EnumClass getEnumClass() {
        return enumClass;
    }

    public void setEnumClass(EnumClass enumClass) {
        this.enumClass = enumClass;
    }
}
