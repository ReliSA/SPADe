package cz.zcu.kiv.spade.domain;

import cz.zcu.kiv.spade.domain.abstracts.DescribedEntity;
import cz.zcu.kiv.spade.domain.enums.WorkUnitRelationClass;
import cz.zcu.kiv.spade.domain.enums.WorkUnitRelationSuperClass;

import javax.persistence.*;

@Entity
@Table(name = "wu_relation")
public class WorkUnitRelation extends DescribedEntity {

    private WorkUnitRelationClassification classification;
    private WorkUnit leftUnit;
    private WorkUnit rightUnit;

    public WorkUnitRelation() {
        super();
        this.classification = new WorkUnitRelationClassification();
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "classId")
    public WorkUnitRelationClassification getClassification() {
        return classification;
    }

    public void setClassification(WorkUnitRelationClassification classification) {
        this.classification = classification;
    }

    @Transient
    public WorkUnitRelationClass getAClass() {
        return classification.getaClass();
    }

    @Transient
    public WorkUnitRelationSuperClass getSuperClass() {
        return classification.getSuperClass();
    }

    public void setAClass(WorkUnitRelationClass newClass) {
        this.classification.setaClass(newClass);
    }

    @JoinColumn(name = "leftUnitId")
    @ManyToOne(fetch = FetchType.LAZY)
    public WorkUnit getLeftUnit() {
        return leftUnit;
    }

    public void setLeftUnit(WorkUnit leftUnit) {
        this.leftUnit = leftUnit;
    }

    @JoinColumn(name = "rightUnitId")
    @ManyToOne(fetch = FetchType.LAZY)
    public WorkUnit getRightUnit() {
        return rightUnit;
    }

    public void setRightUnit(WorkUnit rightUnit) {
        this.rightUnit = rightUnit;
    }
}
