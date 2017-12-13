package cz.zcu.kiv.spade.domain;

import cz.zcu.kiv.spade.domain.abstracts.DescribedEntity;
import cz.zcu.kiv.spade.domain.enums.SeverityClass;
import cz.zcu.kiv.spade.domain.enums.SeveritySuperClass;

import javax.persistence.*;

@Entity
@Table(name = "severity")
public class Severity extends DescribedEntity {

    private SeverityClassification classification;

    public Severity() {
        super();
        this.classification = new SeverityClassification();
    }

    public Severity(String name, SeverityClassification classification) {
        super();
        this.classification = classification;
        this.name = name;
    }

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "classId")
    public SeverityClassification getClassification() {
        return classification;
    }

    public void setClassification(SeverityClassification classification) {
        this.classification = classification;
    }

    @Transient
    public SeverityClass getAClass() {
        return classification.getAClass();
    }

    public void setAClass(SeverityClass newClass) {
        this.classification.setAClass(newClass);
    }

    @Transient
    public SeveritySuperClass getSuperClass() {
        return classification.getSuperClass();
    }
}