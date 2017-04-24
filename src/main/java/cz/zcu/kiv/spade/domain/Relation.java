package cz.zcu.kiv.spade.domain;

import cz.zcu.kiv.spade.domain.abstracts.DescribedEntity;
import cz.zcu.kiv.spade.domain.enums.RelationClass;
import cz.zcu.kiv.spade.domain.enums.RelationSuperClass;

import javax.persistence.*;

@Entity
@Table(name = "wu_relation")
public class Relation extends DescribedEntity {

    private RelationClassification classification;

    public Relation() {
        super();
        this.classification = new RelationClassification();
    }

    public Relation(String name, RelationClassification classification) {
        super();
        this.classification = classification;
        this.name = name;
    }

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "classId")
    public RelationClassification getClassification() {
        return classification;
    }

    public void setClassification(RelationClassification classification) {
        this.classification = classification;
    }

    @Transient
    public RelationClass getAClass() {
        return classification.getaClass();
    }

    public void setAClass(RelationClass newClass) {
        this.classification.setaClass(newClass);
    }

    @Transient
    public RelationSuperClass getSuperClass() {
        return classification.getSuperClass();
    }
}
