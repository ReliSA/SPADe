package cz.zcu.kiv.ppicha.spade.domain;

import cz.zcu.kiv.ppicha.spade.domain.abstracts.DescribedEntity;
import cz.zcu.kiv.ppicha.spade.domain.enums.WorkUnitPriorityClass;
import cz.zcu.kiv.ppicha.spade.domain.enums.WorkUnitPrioritySuperclass;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

public class WorkUnitPriority extends DescribedEntity {

    private WorkUnitPriorityClass priorityClass;
    private WorkUnitPrioritySuperclass prioritySuperclass;

    public WorkUnitPriority() {
    }

    public WorkUnitPriority(long id, String externalId, String name, String description, WorkUnitPriorityClass priorityClass, WorkUnitPrioritySuperclass prioritySuperclass) {
        super(id, externalId, name, description);
        this.priorityClass = priorityClass;
        this.prioritySuperclass = prioritySuperclass;
    }

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    public WorkUnitPriorityClass getPriorityClass() {
        return priorityClass;
    }

    public void setPriorityClass(WorkUnitPriorityClass priorityClass) {
        this.priorityClass = priorityClass;
    }

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    public WorkUnitPrioritySuperclass getPrioritySuperclass() {
        return prioritySuperclass;
    }

    public void setPrioritySuperclass(WorkUnitPrioritySuperclass prioritySuperclass) {
        this.prioritySuperclass = prioritySuperclass;
    }
}
