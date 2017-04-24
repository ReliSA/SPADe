package cz.zcu.kiv.spade.domain;

import cz.zcu.kiv.spade.domain.abstracts.DescribedEntity;
import cz.zcu.kiv.spade.domain.enums.ResolutionClass;
import cz.zcu.kiv.spade.domain.enums.ResolutionSuperClass;

import javax.persistence.*;

@Entity
@Table(name = "resolution")
public class Resolution extends DescribedEntity {

    private ResolutionClassification classification;

    public Resolution() {
        super();
        this.classification = new ResolutionClassification();
    }

    public Resolution(String name, ResolutionClassification classification) {
        super();
        this.classification = classification;
        this.name = name;
    }

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "classId")
    public ResolutionClassification getClassification() {
        return classification;
    }

    public void setClassification(ResolutionClassification classification) {
        this.classification = classification;
    }

    @Transient
    public ResolutionClass getAClass() {
        return classification.getaClass();
    }

    public void setAClass(ResolutionClass newClass) {
        this.classification.setaClass(newClass);
    }

    @Transient
    public ResolutionSuperClass getSuperClass() {
        return classification.getSuperClass();
    }
}