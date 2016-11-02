package cz.zcu.kiv.spade.domain;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "Configuration_Person")
public class ConfigPersonRelation {

    private String description;
    private Person person;

    public ConfigPersonRelation() {
    }

    public ConfigPersonRelation(String description, Person person) {
        this.description = description;
        this.person = person;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }
}
