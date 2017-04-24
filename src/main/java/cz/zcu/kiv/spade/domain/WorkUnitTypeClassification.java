package cz.zcu.kiv.spade.domain;

import cz.zcu.kiv.spade.domain.abstracts.BaseEntity;
import cz.zcu.kiv.spade.domain.enums.WorkUnitTypeClass;

import javax.persistence.*;

@Entity
@Table(name = "wu_type_classification")
public class WorkUnitTypeClassification extends BaseEntity {

    private WorkUnitTypeClass aClass;

    public WorkUnitTypeClassification() {
        super();
    }

    public WorkUnitTypeClassification(WorkUnitTypeClass aClass) {
        super();
        this.setaClass(aClass);
    }

    @Column(name = "class")
    @Enumerated(EnumType.STRING)
    public WorkUnitTypeClass getaClass() {
        return aClass;
    }

    public void setaClass(WorkUnitTypeClass aClass) {
        this.aClass = aClass;
    }
}
