package cz.zcu.kiv.spade.domain;

import cz.zcu.kiv.spade.domain.abstracts.DescribedEntity;
import cz.zcu.kiv.spade.domain.enums.RoleClass;
import cz.zcu.kiv.spade.domain.enums.RoleSuperclass;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Entity
public class Role extends DescribedEntity {

    private RoleClass roleClass;
    private RoleSuperclass roleSuperclass;

    public Role() {
    }

    public Role(long id, String externalId, String name, String description, RoleClass roleClass, RoleSuperclass roleSuperclass) {
        super(id, externalId, name, description);
        this.roleClass = roleClass;
        this.roleSuperclass = roleSuperclass;
    }

    @Enumerated(value = EnumType.STRING)
    public RoleClass getRoleClass() {
        return roleClass;
    }

    public void setRoleClass(RoleClass roleClass) {
        this.roleClass = roleClass;
    }

    @Enumerated(value = EnumType.STRING)
    public RoleSuperclass getRoleSuperclass() {
        return roleSuperclass;
    }

    public void setRoleSuperclass(RoleSuperclass roleSuperclass) {
        this.roleSuperclass = roleSuperclass;
    }
}
