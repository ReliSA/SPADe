package cz.zcu.kiv.spade.domain;

import cz.zcu.kiv.spade.domain.abstracts.DescribedEntity;
import cz.zcu.kiv.spade.domain.enums.WorkUnitRelationType;

import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

public class WorkUnitRelation extends DescribedEntity {

    private WorkUnitRelationType type;
    private WorkUnit leftUnit;
    private WorkUnit rightUnit;

    public WorkUnitRelation() {

    }

    public WorkUnitRelation(long id, String externalId, String name, String description, WorkUnitRelationType type, WorkUnit leftUnit, WorkUnit rightUnit) {
        super(id, externalId, name, description);
        this.type = type;
        this.leftUnit = leftUnit;
        this.rightUnit = rightUnit;
    }

    public WorkUnitRelationType getType() {
        return type;
    }

    public void setType(WorkUnitRelationType type) {
        this.type = type;
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
