package cz.zcu.kiv.spade.domain;

import cz.zcu.kiv.spade.domain.abstracts.DescribedEntity;
import cz.zcu.kiv.spade.domain.enums.RoleClass;
import cz.zcu.kiv.spade.domain.enums.RoleSuperclass;

import javax.persistence.*;

@Entity
@Table(name = "role")
public class Role extends DescribedEntity {

    private RoleClassification classification;

    public Role() {
        super();
        this.classification = new RoleClassification();
    }

    public Role(String name, RoleClassification classification) {
        super();
        this.classification = classification;
        this.name = name;
    }

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "classId")
    public RoleClassification getClassification() {
        return classification;
    }

    public void setClassification(RoleClassification classification) {
        this.classification = classification;
    }

    @Transient
    public RoleClass getAClass() {
        return classification.getAClass();
    }

    public void setAClass(RoleClass newClass) {
        this.classification.setAClass(newClass);
    }

    @Transient
    public RoleSuperclass getSuperClass() {
        return classification.getSuperClass();
    }

}
