package cz.zcu.kiv.spade.domain;

import cz.zcu.kiv.spade.domain.abstracts.DescribedEntity;
import cz.zcu.kiv.spade.domain.enums.WorkUnitTypeClass;

import javax.persistence.*;

@Entity
@Table(name = "wu_type")
public class WorkUnitType extends DescribedEntity{

    private WorkUnitTypeClassification classification;

    public WorkUnitType() {
        super();
        this.classification = new WorkUnitTypeClassification();
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "classId")
    public WorkUnitTypeClassification getClassification() {
        return classification;
    }

    public void setClassification(WorkUnitTypeClassification classification) {
        this.classification = classification;
    }

    @Transient
    public WorkUnitTypeClass getAClass() {
        return classification.getaClass();
    }

    public void setAClass(WorkUnitTypeClass newClass){
        this.classification.setaClass(newClass);
    }
}