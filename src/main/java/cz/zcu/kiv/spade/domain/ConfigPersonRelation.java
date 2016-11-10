package cz.zcu.kiv.spade.domain;

import cz.zcu.kiv.spade.domain.abstracts.NamedEntity;

import javax.persistence.*;

@Entity
@Table(name = "configuration_person_relation")
public class ConfigPersonRelation extends NamedEntity {

    private Person person;

    public ConfigPersonRelation() {
        super();
    }

    @JoinColumn(name = "personId")
    @ManyToOne(fetch = FetchType.LAZY)
    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }
}
