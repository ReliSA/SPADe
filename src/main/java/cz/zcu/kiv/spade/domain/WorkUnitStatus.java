package cz.zcu.kiv.spade.domain;

import cz.zcu.kiv.spade.domain.abstracts.DescribedEntity;
import cz.zcu.kiv.spade.domain.enums.WorkUnitStatusClass;
import cz.zcu.kiv.spade.domain.enums.WorkUnitStatusSuperclass;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

@Entity
@Table(name = "status")
public class WorkUnitStatus extends DescribedEntity {

    private WorkUnitStatusClass statusClass;
    private WorkUnitStatusSuperclass statusSuperclass;

    public WorkUnitStatus() {
        super();
    }

    @Enumerated(value = EnumType.STRING)
    public WorkUnitStatusClass getStatusClass() {
        return statusClass;
    }

    public void setStatusClass(WorkUnitStatusClass statusClass) {
        this.statusClass = statusClass;
    }

    @Enumerated(value = EnumType.STRING)
    public WorkUnitStatusSuperclass getStatusSuperclass() {
        return statusSuperclass;
    }

    public void setStatusSuperclass(WorkUnitStatusSuperclass statusSuperclass) {
        this.statusSuperclass = statusSuperclass;
    }
}
