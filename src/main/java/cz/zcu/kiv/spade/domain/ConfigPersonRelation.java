package cz.zcu.kiv.spade.domain;

import cz.zcu.kiv.spade.domain.abstracts.DescribedEntity;

import javax.persistence.*;

@Entity
@Table(name = "configuration_person_relation")
public class ConfigPersonRelation extends DescribedEntity {

    private Person person;

    public ConfigPersonRelation() {
        super();
    }

    @JoinColumn(name = "personId")
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }
}
