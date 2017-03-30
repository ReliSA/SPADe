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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "classId")
    public RoleClassification getClassification() {
        return classification;
    }

    public void setClassification(RoleClassification classification) {
        this.classification = classification;
    }

    @Transient
    public RoleClass getAClass() {
        return classification.getaClass();
    }

    @Transient
    public RoleSuperclass getSuperClass() {
        return classification.getSuperClass();
    }

    public void setAClass(RoleClass newClass){
        this.classification.setaClass(newClass);
    }

}
