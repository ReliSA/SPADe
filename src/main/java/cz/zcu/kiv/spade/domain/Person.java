package cz.zcu.kiv.spade.domain;

import cz.zcu.kiv.spade.domain.abstracts.NamedEntity;

import javax.persistence.*;
import java.util.Collection;
import java.util.LinkedHashSet;

@Entity
@Table(name = "person")
public class Person extends NamedEntity {

    private Collection<Identity> identities;
    private Collection<Competency> competencies;

    public Person() {
        super();
        this.identities = new LinkedHashSet<>();
        this.competencies = new LinkedHashSet<>();
    }

    @OneToMany
    @JoinColumn(name = "personId")
    public Collection<Identity> getIdentities() {
        return identities;
    }

    public void setIdentities(Collection<Identity> identities) {
        this.identities = identities;
    }

    @ManyToMany
    @JoinTable(name = "person_competency", joinColumns = @JoinColumn(name = "personId", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "competencyId", referencedColumnName = "id"))
    public Collection<Competency> getCompetencies() {
        return competencies;
    }

    public void setCompetencies(Collection<Competency> competencies) {
        this.competencies = competencies;
    }

}
