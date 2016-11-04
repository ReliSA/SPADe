package cz.zcu.kiv.spade.domain;

import cz.zcu.kiv.spade.domain.abstracts.BaseEntity;
import cz.zcu.kiv.spade.domain.abstracts.NamedEntity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "Configuration_Person")
public class ConfigPersonRelation extends NamedEntity {

    private Person person;

    public ConfigPersonRelation() {
    }

    public ConfigPersonRelation(long id, String externalId, String name, Person person) {
        super(id, externalId, name);
        this.person = person;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }
}
