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

    public Person(long id, String externalId, String name, Set<Identity> identities, Set<Competency> competencies) {
        super(id, externalId, name);
        this.identities = identities;
        this.competencies = competencies;
    }

    @OneToMany
    @JoinTable(name = "Person_Identity", joinColumns = @JoinColumn(name = "person_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "identity_id", referencedColumnName = "id"))
    public Set<Identity> getIdentities() {
        return identities;
    }

    public void setIdentities(Set<Identity> identities) {
        this.identities = identities;
    }

    @ManyToMany
    @JoinTable(name = "Person_Competency", joinColumns = @JoinColumn(name = "person_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "competency_id", referencedColumnName = "id"))
    public Set<Competency> getCompetencies() {
        return competencies;
    }

    public void setCompetencies(Set<Competency> competencies) {
        this.competencies = competencies;
    }

}
