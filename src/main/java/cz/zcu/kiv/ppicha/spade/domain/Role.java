package cz.zcu.kiv.ppicha.spade.domain;

import cz.zcu.kiv.ppicha.spade.domain.abstracts.DescribedEntity;
import cz.zcu.kiv.ppicha.spade.domain.enums.RoleSuperclass;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Entity
public class Role extends DescribedEntity {

    private RoleSuperclass roleSuperclass;

    public Role() {
    }

    public Role(long id, String externalId, String name, String description, RoleSuperclass roleSuperclass) {
        super(id, externalId, name, description);
        this.roleSuperclass = roleSuperclass;
    }

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    public RoleSuperclass getRoleSuperclass() {
        return roleSuperclass;
    }

    public void setRoleSuperclass(RoleSuperclass roleSuperclass) {
        this.roleSuperclass = roleSuperclass;
    }
}
