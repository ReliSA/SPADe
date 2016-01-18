package cz.zcu.kiv.ppicha.spade.domain;

import cz.zcu.kiv.ppicha.spade.domain.abstracts.NamedEntity;

import javax.persistence.Entity;

@Entity
public class Role extends NamedEntity {

    public Role() {
    }

    public Role(long id, long externalId, String name) {
        super(id, externalId, name);
    }
}
