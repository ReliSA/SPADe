package cz.zcu.kiv.spade.domain;

import cz.zcu.kiv.spade.domain.abstracts.DescribedEntity;
import cz.zcu.kiv.spade.domain.enums.PriorityClass;
import cz.zcu.kiv.spade.domain.enums.PrioritySuperClass;

import javax.persistence.*;

@Entity
@Table(name = "priority")
public class Priority extends DescribedEntity{

    private PriorityClassification classification;

    public Priority() {
        super();
        this.classification = new PriorityClassification();
    }

    @ManyToOne(fetch = FetchType.LAZY)
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
