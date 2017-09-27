package cz.zcu.kiv.spade.domain;

import cz.zcu.kiv.spade.domain.abstracts.NamedEntity;

import javax.persistence.*;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;

@Entity
@Table(name = "person")
public class Person extends NamedEntity {

    private Collection<Identity> identities;
    private Collection<Competency> competencies;
    private Collection<Role> roles;

    public Person() {
        super();
        this.identities = new LinkedHashSet<>();
        this.competencies = new LinkedHashSet<>();
        this.roles = new LinkedHashSet<>();
    }

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "personId")
    public Collection<Identity> getIdentities() {
        return identities;
    }

    public void setIdentities(Collection<Identity> identities) {
        this.identities = identities;
    }

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "person_competency", joinColumns = @JoinColumn(name = "personId", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "competencyId", referencedColumnName = "id"))
    public Collection<Competency> getCompetencies() {
        return competencies;
    }

    public void setCompetencies(Collection<Competency> competencies) {
        this.competencies = competencies;
    }

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "person_role", joinColumns = @JoinColumn(name = "personId", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "roleId", referencedColumnName = "id"))
    public Collection<Role> getRoles() {
        return roles;
    }

    public void setRoles(Collection<Role> roles) {
        this.roles = roles;
    }

    @Transient
    public Set<String> getEmails() {
        Set<String> emails = new TreeSet<>();
        for (Identity identity : identities) {
            if (identity.getEmail() == null || identity.getEmail().isEmpty()) {
                if (!identity.getDescription().isEmpty()) emails.add(identity.getDescription());
            }
            else emails.add(identity.getEmail());
        }
        return emails;
    }
}
