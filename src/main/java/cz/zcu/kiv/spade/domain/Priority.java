package cz.zcu.kiv.spade.domain;

import cz.zcu.kiv.spade.domain.abstracts.DescribedEntity;
import cz.zcu.kiv.spade.domain.enums.PriorityClass;
import cz.zcu.kiv.spade.domain.enums.PrioritySuperClass;
import cz.zcu.kiv.spade.domain.enums.SeverityClass;

import javax.persistence.*;

@Entity
@Table(name = "priority")
public class Priority extends DescribedEntity{

    private PriorityClassification classification;

    public Priority() {
        super();
        this.classification = new PriorityClassification();
    }

    public Priority(String name, PriorityClass aClass) {
        super();
        this.classification = new PriorityClassification();
        this.setName(name);
        this.setAClass(aClass);
    }

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "classId")
    public PriorityClassification getClassification() {
        return classification;
    }

    public void setClassification(PriorityClassification classification) {
        this.classification = classification;
    }

    @Transient
    public PriorityClass getAClass() {
        return classification.getaClass();
    }

    @Transient
    public PrioritySuperClass getSuperClass() {
        return classification.getSuperClass();
    }

    public void setAClass(PriorityClass newClass){
        this.classification.setaClass(newClass);
    }
}
