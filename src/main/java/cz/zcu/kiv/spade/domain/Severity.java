package cz.zcu.kiv.spade.domain;

import cz.zcu.kiv.spade.domain.abstracts.DescribedEntity;
import cz.zcu.kiv.spade.domain.enums.SeverityClass;
import cz.zcu.kiv.spade.domain.enums.SeveritySuperClass;

import javax.persistence.*;

@Entity
@Table(name = "severity")
public class Severity extends DescribedEntity{

    private SeverityClassification classification;

    public Severity() {
        super();
        this.classification = new SeverityClassification();
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "classId")
    public SeverityClassification getClassification() {
        return classification;
    }

    public void setClassification(SeverityClassification classification) {
        this.classification = classification;
    }

    @Transient
    public SeverityClass getAClass() {
        return classification.getaClass();
    }

    @Transient
    public SeveritySuperClass getSuperClass() {
        return classification.getSuperClass();
    }

    public void setAClass(SeverityClass newClass){
        this.classification.setaClass(newClass);
    }
}