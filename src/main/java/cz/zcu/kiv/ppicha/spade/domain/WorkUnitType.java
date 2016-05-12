package cz.zcu.kiv.ppicha.spade.domain;

import cz.zcu.kiv.ppicha.spade.domain.abstracts.DescribedEntity;
import cz.zcu.kiv.ppicha.spade.domain.enums.WorkUnitTypeSuperclass;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

public class WorkUnitType extends DescribedEntity {

    private WorkUnitTypeSuperclass typeSuperclass;

    public WorkUnitType() {
    }

    public WorkUnitType(long id, String externalId, String name, String description, String typeClass, WorkUnitTypeSuperclass typeSuperclass) {
        super(id, externalId, name, description);
        this.typeSuperclass = typeSuperclass;
    }

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    public WorkUnitTypeSuperclass getTypeSuperclass() {
        return typeSuperclass;
    }

    public void setTypeSuperclass(WorkUnitTypeSuperclass typeSuperclass) {
        this.typeSuperclass = typeSuperclass;
    }

}
