package cz.zcu.kiv.spade.domain;

import cz.zcu.kiv.spade.domain.abstracts.DescribedEntity;
import cz.zcu.kiv.spade.domain.enums.StatusClass;
import cz.zcu.kiv.spade.domain.enums.StatusSuperClass;

import javax.persistence.*;

@Entity
@Table(name = "status")
public class Status extends DescribedEntity {

    private StatusClassification classification;

    public Status() {
        super();
        this.classification = new StatusClassification();
    }

    public Status(String name, StatusClassification classification) {
        super();
        this.classification = classification;
        this.name = name;
    }

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
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

    public void setAClass(StatusClass newClass) {
        this.classification.setaClass(newClass);
    }

    @Transient
    public StatusSuperClass getSuperClass() {
        return classification.getSuperClass();
    }
}