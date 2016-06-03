package cz.zcu.kiv.spade.domain;

import cz.zcu.kiv.spade.domain.abstracts.DescribedEntity;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import java.util.Collection;
import java.util.LinkedHashSet;

@Entity
public class Milestone extends DescribedEntity {

    private Collection<Criterion> criteria;

    public Milestone() {
        this.criteria = new LinkedHashSet<>();
    }

    public Milestone(long id, String externalId, String name, String description, Collection<Criterion> criteria) {
        super(id, externalId, name, description);
        this.criteria = criteria;
    }

    @ManyToMany
    @JoinTable(name = "Phase_Criterion", joinColumns = @JoinColumn(name = "phase_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "criterion_id", referencedColumnName = "id"))
    public Collection<Criterion> getCriteria() {
        return criteria;
    }

    public void setCriteria(Collection<Criterion> criteria) {
        this.criteria = criteria;
    }

}
