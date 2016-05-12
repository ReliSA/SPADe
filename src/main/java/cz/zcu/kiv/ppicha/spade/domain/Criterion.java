package cz.zcu.kiv.ppicha.spade.domain;

import cz.zcu.kiv.ppicha.spade.domain.abstracts.DescribedEntity;

import javax.persistence.Entity;

@Entity
public class Criterion extends DescribedEntity {

    public Criterion() {
    }

    public Criterion(long id, String externalId, String name, String description) {
        super(id, externalId, name, description);
    }
}
