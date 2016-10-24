package cz.zcu.kiv.spade.domain;

import cz.zcu.kiv.spade.domain.abstracts.DescribedEntity;
import cz.zcu.kiv.spade.domain.enums.WorkUnitTypeClass;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

@Entity
public class WorkUnitType extends DescribedEntity {

    private WorkUnitTypeClass typeClass;

    public WorkUnitType() {
    }

    public WorkUnitType(long id, String externalId, String name, String description, WorkUnitTypeClass typeClass) {
        super(id, externalId, name, description);
        this.typeClass = typeClass;
    }

    @Enumerated(value = EnumType.STRING)
    public WorkUnitTypeClass getTypeClass() {
        return typeClass;
    }

    public void setTypeClass(WorkUnitTypeClass typeSuperclass) {
        this.typeClass = typeClass;
    }

}
