package cz.zcu.kiv.ppicha.spade.domain;

import cz.zcu.kiv.ppicha.spade.domain.abstracts.NamedAndDescribedEntity;

import javax.persistence.*;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
public class Milestone extends NamedAndDescribedEntity {

    private Set<Criterion> criteria;

    public Milestone() {
        this.criteria = new LinkedHashSet<>();
    }

    public Milestone(long id, long externalId, String name, String description, Set<Criterion> criteria) {
        super(id, externalId, name, description);
        this.criteria = criteria;
    }

    @ManyToMany
    @JoinTable(name = "Phase_Criterion", joinColumns = @JoinColumn(name = "phase", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "criterion", referencedColumnName = "id"))
    public Set<Criterion> getCriteria() {
        return criteria;
    }

    public void setCriteria(Set<Criterion> criteria) {
        this.criteria = criteria;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Milestone milestone = (Milestone) o;

        return !(criteria != null ? !criteria.equals(milestone.criteria) : milestone.criteria != null);

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (criteria != null ? criteria.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Milestone{" +
                "criteria=" + criteria +
                '}';
    }
}
