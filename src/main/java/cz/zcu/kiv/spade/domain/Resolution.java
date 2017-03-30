package cz.zcu.kiv.spade.domain;

import cz.zcu.kiv.spade.domain.abstracts.DescribedEntity;
import cz.zcu.kiv.spade.domain.enums.ResolutionClass;
import cz.zcu.kiv.spade.domain.enums.ResolutionSuperClass;

import javax.persistence.*;

@Entity
@Table(name = "resolution")
public class Resolution extends DescribedEntity{

    private ResolutionClassification classification;

    public Resolution() {
        super();
        this.classification = new ResolutionClassification();
    }

    @ManyToOne(fetch = FetchType.LAZY)
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

    @Transient
    public ResolutionSuperClass getSuperClass() {
        return classification.getSuperClass();
    }

    public void setAClass(ResolutionClass newClass){
        this.classification.setaClass(newClass);
    }
}