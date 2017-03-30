package cz.zcu.kiv.spade.domain;

import cz.zcu.kiv.spade.domain.enums.PriorityClass;
import cz.zcu.kiv.spade.domain.enums.PrioritySuperClass;

import javax.persistence.*;

@Entity
@Table(name = "priority_classification")
public class PriorityClassification {
    private long id;
    private PriorityClass aClass;
    private PrioritySuperClass superClass;

    public PriorityClassification() {
        aClass = PriorityClass.NORMAL;
        superClass = PrioritySuperClass.NORMAL;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public PriorityClass getaClass() {
        return aClass;
    }

    public void setaClass(PriorityClass aClass) {
        this.aClass = aClass;
        if (aClass == PriorityClass.LOWEST || aClass == PriorityClass.LOW)
            this.superClass = PrioritySuperClass.LOW;
        if (aClass == PriorityClass.NORMAL)
            this.superClass = PrioritySuperClass.NORMAL;
        if (aClass == PriorityClass.HIGH || aClass == PriorityClass.HIGHEST)
            this.superClass = PrioritySuperClass.HIGH;
    }

    public PrioritySuperClass getSuperClass() {
        return superClass;
    }

    public void setSuperClass(PrioritySuperClass superClass) {
        this.superClass = superClass;
    }
}
