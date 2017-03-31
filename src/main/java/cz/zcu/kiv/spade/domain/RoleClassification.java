package cz.zcu.kiv.spade.domain;

import cz.zcu.kiv.spade.domain.enums.RoleClass;
import cz.zcu.kiv.spade.domain.enums.RoleSuperclass;
import cz.zcu.kiv.spade.domain.enums.SeverityClass;
import cz.zcu.kiv.spade.domain.enums.SeveritySuperClass;

import javax.persistence.*;

@Entity
@Table(name = "role_classification")
public class RoleClassification {
    private long id;
    private RoleClass aClass;
    private RoleSuperclass superClass;

    public RoleClassification() {
        aClass = RoleClass.TEAM_MEMBER;
        superClass = RoleSuperclass.TEAM_MEMBER;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Column(name = "class")
    @Enumerated(EnumType.STRING)
    public RoleClass getaClass() {
        return aClass;
    }

    public void setaClass(RoleClass aClass) {
        this.aClass = aClass;
        if (aClass == RoleClass.TEAM_MEMBER || aClass == RoleClass.ANALYST
                || aClass == RoleClass.DESIGNER || aClass == RoleClass.DEVELOPER
                || aClass == RoleClass.TESTER || aClass == RoleClass.DOCUMENTER)
            this.superClass = RoleSuperclass.TEAM_MEMBER;
        if (aClass == RoleClass.PROJECT_MANAGER)
            this.superClass = RoleSuperclass.MANAGEMENT;
        if (aClass == RoleClass.STAKEHOLDER || aClass == RoleClass.MENTOR)
            this.superClass = RoleSuperclass.STAKEHOLDER;
        if (aClass == RoleClass.NON_MEMBER)
            this.superClass = RoleSuperclass.NON_MEMBER;
    }

    @Enumerated(EnumType.STRING)
    public RoleSuperclass getSuperClass() {
        return superClass;
    }

    public void setSuperClass(RoleSuperclass superClass) {
        this.superClass = superClass;
    }
}
