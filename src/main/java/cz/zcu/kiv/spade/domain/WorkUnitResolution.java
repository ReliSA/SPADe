package cz.zcu.kiv.spade.domain;

import cz.zcu.kiv.spade.domain.abstracts.DescribedEntity;
import cz.zcu.kiv.spade.domain.enums.WorkUnitResolutionClass;
import cz.zcu.kiv.spade.domain.enums.WorkUnitResolutionSuperclass;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

@Entity
@Table(name = "resolution")
public class WorkUnitResolution extends DescribedEntity {

    private WorkUnitResolutionClass resolutionClass;
    private WorkUnitResolutionSuperclass resolutionSuperclass;

    public WorkUnitResolution() {
        super();
    }

    @Enumerated(value = EnumType.STRING)
    public WorkUnitResolutionClass getPriorityClass() {
        return resolutionClass;
    }

    public void setPriorityClass(WorkUnitResolutionClass resolutionClass) {
        this.resolutionClass = resolutionClass;
    }

    @Enumerated(value = EnumType.STRING)
    public WorkUnitResolutionSuperclass getPrioritySuperclass() {
        return resolutionSuperclass;
    }

    public void setPrioritySuperclass(WorkUnitResolutionSuperclass resolutionSuperclass) {
        this.resolutionSuperclass = resolutionSuperclass;
    }
}
