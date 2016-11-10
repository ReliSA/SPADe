package cz.zcu.kiv.spade.domain;

import cz.zcu.kiv.spade.domain.abstracts.DescribedEntity;

import javax.persistence.*;
import java.util.Collection;
import java.util.LinkedHashSet;

@Entity
@Table(name = "milestone")
public class Milestone extends DescribedEntity {

    private Collection<Criterion> criteria;

    public Milestone() {
        super();
        this.criteria = new LinkedHashSet<>();
    }

    @ManyToMany
    @JoinTable(name = "milestone_criterion", joinColumns = @JoinColumn(name = "milestoneId", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "criterionId", referencedColumnName = "id"))
    public Collection<Criterion> getCriteria() {
        return criteria;
    }

    public void setCriteria(Collection<Criterion> criteria) {
        this.criteria = criteria;
    }

}
