package cz.zcu.kiv.spade.domain;

import cz.zcu.kiv.spade.domain.abstracts.DescribedEntity;
import cz.zcu.kiv.spade.domain.enums.RoleClass;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Entity
public class Role extends DescribedEntity {

    private RoleClass roleClass;

    public Role() {
    }

    public Role(long id, String externalId, String name, String description, RoleClass roleClass) {
        super(id, externalId, name, description);
        this.roleClass = roleClass;
    }

    @Enumerated(value = EnumType.STRING)
    public RoleClass getRoleClass() {
        return roleClass;
    }

    public void setRoleClass(RoleClass roleClass) {
        this.roleClass = roleClass;
    }
}
