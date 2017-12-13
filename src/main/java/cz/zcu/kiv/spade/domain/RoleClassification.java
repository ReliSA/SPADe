package cz.zcu.kiv.spade.domain;

import cz.zcu.kiv.spade.domain.abstracts.BaseEntity;
import cz.zcu.kiv.spade.domain.enums.RoleClass;
import cz.zcu.kiv.spade.domain.enums.RoleSuperclass;

import javax.persistence.*;

@Entity
@Table(name = "role_classification")
public class RoleClassification extends BaseEntity {

    private RoleClass aClass;
    private RoleSuperclass superClass;

    public RoleClassification() {
        super();
    }

    public RoleClassification(RoleClass aClass) {
        super();
        this.setAClass(aClass);
    }

    @Column(name = "class")
    @Enumerated(EnumType.STRING)
    public RoleClass getAClass() {
        return aClass;
    }

    public void setAClass(RoleClass aClass) {
        this.aClass = aClass;
        if (aClass == RoleClass.UNASSIGNED)
            this.superClass = RoleSuperclass.UNASSIGNED;
        if (aClass == RoleClass.TEAMMEMBER || aClass == RoleClass.ANALYST
                || aClass == RoleClass.DESIGNER || aClass == RoleClass.DEVELOPER
                || aClass == RoleClass.TESTER || aClass == RoleClass.DOCUMENTER)
            this.superClass = RoleSuperclass.TEAMMEMBER;
        if (aClass == RoleClass.PROJECTMANAGER)
            this.superClass = RoleSuperclass.MANAGEMENT;
        if (aClass == RoleClass.STAKEHOLDER || aClass == RoleClass.MENTOR)
            this.superClass = RoleSuperclass.STAKEHOLDER;
        if (aClass == RoleClass.NONMEMBER)
            this.superClass = RoleSuperclass.NONMEMBER;
    }

    @Enumerated(EnumType.STRING)
    public RoleSuperclass getSuperClass() {
        return superClass;
    }

    public void setSuperClass(RoleSuperclass superClass) {
        this.superClass = superClass;
    }
}
