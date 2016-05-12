package cz.zcu.kiv.ppicha.spade.domain;

import cz.zcu.kiv.ppicha.spade.domain.abstracts.NamedEntity;
import cz.zcu.kiv.ppicha.spade.domain.enums.WorkUnitRelationType;

import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

/**
 * Created by Petr on 11.5.2016.
 */
public class WorkUnitRelation extends NamedEntity{

    private WorkUnitRelationType type;
    private WorkUnit leftUnit;
    private WorkUnit rightUnit;

    public WorkUnitRelation() {

    }

    public WorkUnitRelation(long id, String externalId, String name, WorkUnitRelationType type, WorkUnit leftUnit, WorkUnit rightUnit) {
        super(id, externalId, name);
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
