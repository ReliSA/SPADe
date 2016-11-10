package cz.zcu.kiv.spade.domain;

import cz.zcu.kiv.spade.domain.abstracts.DescribedEntity;
import cz.zcu.kiv.spade.domain.enums.WorkUnitPriorityClass;
import cz.zcu.kiv.spade.domain.enums.WorkUnitPrioritySuperclass;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

@Entity
@Table(name = "priority")
public class WorkUnitPriority extends DescribedEntity {

    private WorkUnitPriorityClass priorityClass;
    private WorkUnitPrioritySuperclass prioritySuperclass;

    public WorkUnitPriority() {
        super();
    }

    @Enumerated(value = EnumType.STRING)
    public WorkUnitPriorityClass getPriorityClass() {
        return priorityClass;
    }

    public void setPriorityClass(WorkUnitPriorityClass priorityClass) {
        this.priorityClass = priorityClass;
    }

    @Enumerated(value = EnumType.STRING)
    public WorkUnitPrioritySuperclass getPrioritySuperclass() {
        return prioritySuperclass;
    }

    public void setPrioritySuperclass(WorkUnitPrioritySuperclass prioritySuperclass) {
        this.prioritySuperclass = prioritySuperclass;
    }
}
