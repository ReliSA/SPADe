package cz.zcu.kiv.spade.domain;

import cz.zcu.kiv.spade.domain.abstracts.DescribedEntity;
import cz.zcu.kiv.spade.domain.enums.StatusClass;
import cz.zcu.kiv.spade.domain.enums.StatusSuperClass;

import javax.persistence.*;

@Entity
@Table(name = "status")
public class Status extends DescribedEntity{

    private StatusClassification classification;

    public Status() {
        super();
        this.classification = new StatusClassification();
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "classId")
    public StatusClassification getClassification() {
        return classification;
    }

    public void setClassification(StatusClassification classification) {
        this.classification = classification;
    }

    @Transient
    public StatusClass getAClass() {
        return classification.getaClass();
    }

    @Transient
    public StatusSuperClass getSuperClass() {
        return classification.getSuperClass();
    }

    public void setAClass(StatusClass newClass){
        this.classification.setaClass(newClass);
    }
}