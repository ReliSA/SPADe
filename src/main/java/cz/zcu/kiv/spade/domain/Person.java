package cz.zcu.kiv.spade.domain;

import cz.zcu.kiv.spade.domain.abstracts.NamedEntity;

import javax.persistence.*;
import java.util.Collection;
import java.util.LinkedHashSet;

@Entity
public class Person extends NamedEntity {

    private Collection<Identity> identities;
    private Collection<Competency> competencies;

    public Person() {
        this.identities = new LinkedHashSet<>();
        this.competencies = new LinkedHashSet<>();
    }

    public Person(long id, String externalId, String name, Collection<Identity> identities, Collection<Competency> competencies) {
        super(id, externalId, name);
        this.identities = identities;
        this.competencies = competencies;
    }

    @OneToMany
    @JoinColumn(name = "person_id")
    public Collection<Identity> getIdentities() {
        return identities;
    }

    public void setIdentities(Collection<Identity> identities) {
        this.identities = identities;
    }

    @ManyToMany
    @JoinTable(name = "Person_Competency", joinColumns = @JoinColumn(name = "person_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "competency_id", referencedColumnName = "id"))
    public Collection<Competency> getCompetencies() {
        return competencies;
    }

    public void setCompetencies(Collection<Competency> competencies) {
        this.competencies = competencies;
    }

}
