package cz.zcu.kiv.spade.domain;

import cz.zcu.kiv.spade.domain.abstracts.DescribedEntity;
import cz.zcu.kiv.spade.domain.enums.WorkUnitTypeClass;

import javax.persistence.*;

@Entity
@Table(name = "wu_type")
public class WorkUnitType extends DescribedEntity {

    private WorkUnitTypeClassification classification;

    public WorkUnitType() {
        super();
        this.classification = new WorkUnitTypeClassification();
    }

    public WorkUnitType(String name, WorkUnitTypeClassification classification) {
        super();
        this.classification = classification;
        this.name = name;
    }

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "classId")
    public WorkUnitTypeClassification getClassification() {
        return classification;
    }

    public void setClassification(WorkUnitTypeClassification classification) {
        this.classification = classification;
    }

    @Transient
    public WorkUnitTypeClass getaClass() {
        return classification.getaClass();
    }

    public void setaClass(WorkUnitTypeClass newClass) {
        this.classification.setaClass(newClass);
    }
}