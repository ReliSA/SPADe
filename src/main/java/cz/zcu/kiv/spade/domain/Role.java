package cz.zcu.kiv.spade.domain;

import cz.zcu.kiv.spade.domain.abstracts.DescribedEntity;
import cz.zcu.kiv.spade.domain.enums.RoleClass;
import cz.zcu.kiv.spade.domain.enums.RoleSuperclass;
import cz.zcu.kiv.spade.domain.enums.StatusClass;

import javax.persistence.*;

@Entity
@Table(name = "role")
public class Role extends DescribedEntity {

    private RoleClassification classification;

    public Role() {
        super();
        this.classification = new RoleClassification();
    }

    public Role(String name, RoleClass aClass) {
        super();
        this.classification = new RoleClassification();
        this.setName(name);
        this.setAClass(aClass);
    }

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
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
