package cz.zcu.kiv.spade.domain;

import cz.zcu.kiv.spade.domain.abstracts.DescribedEntity;
import cz.zcu.kiv.spade.domain.enums.WorkUnitResolutionClass;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Entity
public class WorkUnitResolution extends DescribedEntity {
    private WorkUnitResolutionClass resolutionClass;

    public WorkUnitResolution() {
    }

    public WorkUnitResolution(long id, String externalId, String name, String description, WorkUnitResolutionClass resolutionClass) {
        super(id, externalId, name, description);
        this.resolutionClass = resolutionClass;
    }

    @Enumerated(value = EnumType.STRING)
    public WorkUnitResolutionClass getPriorityClass() {
        return resolutionClass;
    }

    public void setPriorityClass(WorkUnitResolutionClass resolutionClass) {
        this.resolutionClass = resolutionClass;
    }

}
