package cz.zcu.kiv.ppicha.spade.domain;

import cz.zcu.kiv.ppicha.spade.domain.abstracts.NamedEntity;

import javax.persistence.*;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
public class Person extends NamedEntity {

    private Set<Identity> identities;
    private Set<Competency> competencies;

    public Person() {
        this.identities = new LinkedHashSet<>();
        this.competencies = new LinkedHashSet<>();
    }

    public Person(long id, long externalId, String name, Set<Identity> identities, Set<Competency> competencies) {
        super(id, externalId, name);
        this.identities = identities;
        this.competencies = competencies;
    }

    @OneToMany
    public Set<Identity> getIdentities() {
        return identities;
    }

    public void setIdentities(Set<Identity> identities) {
        this.identities = identities;
    }

    @ManyToMany
    @JoinTable(name = "Person_Competency", joinColumns = @JoinColumn(name = "person", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "competency", referencedColumnName = "id"))
    public Set<Competency> getCompetencies() {
        return competencies;
    }

    public void setCompetencies(Set<Competency> competencies) {
        this.competencies = competencies;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Person person = (Person) o;

        if (identities != null ? !identities.equals(person.identities) : person.identities != null) return false;
        return !(competencies != null ? !competencies.equals(person.competencies) : person.competencies != null);

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (identities != null ? identities.hashCode() : 0);
        result = 31 * result + (competencies != null ? competencies.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Person{" +
                "identities=" + identities +
                ", competencies=" + competencies +
                '}';
    }
}
