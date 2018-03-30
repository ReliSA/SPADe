package cz.zcu.kiv.spade.domain;

import cz.zcu.kiv.spade.domain.abstracts.BaseEntity;
import cz.zcu.kiv.spade.domain.enums.PriorityClass;
import cz.zcu.kiv.spade.domain.enums.PrioritySuperClass;

import javax.persistence.*;

@Entity
@Table(name = "priority_classification")
public class PriorityClassification extends BaseEntity {

    private PriorityClass aClass;
    private PrioritySuperClass superClass;

    public PriorityClassification() {
        super();
    }

    public PriorityClassification(PriorityClass aClass) {
        super();
        this.setaClass(aClass);
    }

    @Column(name = "class")
    @Enumerated(EnumType.STRING)
    public PriorityClass getaClass() {
        return aClass;
    }

    public void setaClass(PriorityClass aClass) {
        this.aClass = aClass;
        if (aClass == PriorityClass.UNASSIGNED)
            this.superClass = PrioritySuperClass.UNASSIGNED;
        if (aClass == PriorityClass.LOWEST || aClass == PriorityClass.LOW)
            this.superClass = PrioritySuperClass.LOW;
        if (aClass == PriorityClass.NORMAL)
            this.superClass = PrioritySuperClass.NORMAL;
        if (aClass == PriorityClass.HIGH || aClass == PriorityClass.HIGHEST)
            this.superClass = PrioritySuperClass.HIGH;
    }

    @Enumerated(EnumType.STRING)
    public PrioritySuperClass getSuperClass() {
        return superClass;
    }

    public void setSuperClass(PrioritySuperClass superClass) {
        this.superClass = superClass;
    }
}
