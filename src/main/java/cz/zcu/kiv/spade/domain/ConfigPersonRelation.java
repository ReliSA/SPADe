package cz.zcu.kiv.spade.domain;

import cz.zcu.kiv.spade.domain.abstracts.AuthoredEntity;
import cz.zcu.kiv.spade.domain.abstracts.NamedEntity;

import java.util.Date;

/**
 * Created by Petr on 31.10.2016.
 */
public class ConfigPersonRelation extends AuthoredEntity{
    public ConfigPersonRelation() {
    }

    public ConfigPersonRelation(long id, String externalId, String name, String description, Date created, Person author) {
        super(id, externalId, name, description, created, author);
    }

    @Override
    public String toString() {
        return description + ": " + author.getName();
    }
}
