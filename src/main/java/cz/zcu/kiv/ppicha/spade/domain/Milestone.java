package cz.zcu.kiv.ppicha.spade.domain;

import cz.zcu.kiv.ppicha.spade.domain.abstracts.DescribedEntity;

import javax.persistence.*;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
public class Milestone extends DescribedEntity {

    private Set<Criterion> criteria;

    public Milestone() {
        this.criteria = new LinkedHashSet<>();
    }

    public Milestone(long id, String externalId, String name, String description, Set<Criterion> criteria) {
        super(id, externalId, name, description);
        this.criteria = criteria;
    }

    @ManyToMany
    @JoinTable(name = "Phase_Criterion", joinColumns = @JoinColumn(name = "phase_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "criterion_id", referencedColumnName = "id"))
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

}
