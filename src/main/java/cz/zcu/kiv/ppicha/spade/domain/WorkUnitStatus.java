package cz.zcu.kiv.ppicha.spade.domain;

import cz.zcu.kiv.ppicha.spade.domain.abstracts.DescribedEntity;
import cz.zcu.kiv.ppicha.spade.domain.enums.WorkUnitStatusClass;
import cz.zcu.kiv.ppicha.spade.domain.enums.WorkUnitStatusSuperclass;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

public class WorkUnitStatus extends DescribedEntity {

    private WorkUnitStatusClass statusClass;
    private WorkUnitStatusSuperclass statusSuperclass;

    public WorkUnitStatus() {
    }

    public WorkUnitStatus(long id, String externalId, String name, String description, WorkUnitStatusClass statusClass, WorkUnitStatusSuperclass statusSuperclass) {
        super(id, externalId, name, description);
        this.statusClass = statusClass;
        this.statusSuperclass = statusSuperclass;
    }

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    public WorkUnitStatusClass getStatusClass() {
        return statusClass;
    }

    public void setStatusClass(WorkUnitStatusClass statusClass) {
        this.statusClass = statusClass;
    }

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    public WorkUnitStatusSuperclass getStatusSuperclass() {
        return statusSuperclass;
    }

    public void setStatusSuperclass(WorkUnitStatusSuperclass statusSuperclass) {
        this.statusSuperclass = statusSuperclass;
    }
}
