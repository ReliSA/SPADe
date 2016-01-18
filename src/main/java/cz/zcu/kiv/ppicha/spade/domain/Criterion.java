package cz.zcu.kiv.ppicha.spade.domain;

import cz.zcu.kiv.ppicha.spade.domain.abstracts.NamedAndDescribedEntity;

import javax.persistence.Entity;

@Entity
public class Criterion extends NamedAndDescribedEntity {

    public Criterion() {
    }

    public Criterion(long id, long externalId, String name, String description) {
        super(id, externalId, name, description);
    }
}
