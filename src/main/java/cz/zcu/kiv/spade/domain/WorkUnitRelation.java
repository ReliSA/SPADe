package cz.zcu.kiv.spade.domain;

import cz.zcu.kiv.spade.domain.abstracts.DescribedEntity;
import cz.zcu.kiv.spade.domain.enums.WorkUnitRelationClass;
import cz.zcu.kiv.spade.domain.enums.WorkUnitRelationSuperclass;

import javax.persistence.*;

@Entity
public class WorkUnitRelation extends DescribedEntity {

    private WorkUnitRelationClass relationClass;
    private WorkUnitRelationSuperclass relationSuperclass;
    private WorkUnit leftUnit;
    private WorkUnit rightUnit;

    public WorkUnitRelation() {

    }

    public WorkUnitRelation(long id, String externalId, String name, String description, WorkUnitRelationClass relationClass, WorkUnitRelationSuperclass relationSuperclass, WorkUnit leftUnit, WorkUnit rightUnit) {
        super(id, externalId, name, description);
        this.relationClass = relationClass;
        this.relationSuperclass = relationSuperclass;
        this.leftUnit = leftUnit;
        this.rightUnit = rightUnit;
    }

    @Enumerated(value = EnumType.STRING)
    public WorkUnitRelationClass getRelationClass() {
        return relationClass;
    }

    public void setRelationClass(WorkUnitRelationClass relationClass) {
        this.relationClass = relationClass;
    }

    @Enumerated(value = EnumType.STRING)
    public WorkUnitRelationSuperclass getRelationSuperclass() {
        return relationSuperclass;
    }

    public void setRelationSuperclass(WorkUnitRelationSuperclass relationSuperclass) {
        this.relationSuperclass = relationSuperclass;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    public WorkUnit getLeftUnit() {
        return leftUnit;
    }

    public void setLeftUnit(WorkUnit leftUnit) {
        this.leftUnit = leftUnit;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    public WorkUnit getRightUnit() {
        return rightUnit;
    }

    public void setRightUnit(WorkUnit rightUnit) {
        this.rightUnit = rightUnit;
    }
}
