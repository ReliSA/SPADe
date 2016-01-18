package cz.zcu.kiv.ppicha.spade.domain;

import cz.zcu.kiv.ppicha.spade.domain.abstracts.NamedAndDescribedEntity;

import javax.persistence.Entity;

@Entity
public class Competency extends NamedAndDescribedEntity {

    public Competency() {
    }

    public Competency(long id, long externalId, String name, String description) {
        super(id, externalId, name, description);
    }
}
