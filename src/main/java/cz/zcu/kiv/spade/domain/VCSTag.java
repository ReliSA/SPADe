package cz.zcu.kiv.spade.domain;

import cz.zcu.kiv.spade.domain.abstracts.NamedEntity;

import javax.persistence.Entity;

@Entity
public class VCSTag extends NamedEntity {

    public VCSTag() {
    }

    public VCSTag(long id, String externalId, String name) {
        super(id, externalId, name);
    }
}
