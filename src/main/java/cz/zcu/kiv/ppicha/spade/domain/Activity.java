package cz.zcu.kiv.ppicha.spade.domain;

import cz.zcu.kiv.ppicha.spade.domain.abstracts.NamedAndDescribedEntity;

import javax.persistence.*;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
public class Activity extends NamedAndDescribedEntity {

    protected Set<WorkUnit> workUnits;
    private Set<Activity> predecessors;

    public Activity() {
        this.workUnits = new LinkedHashSet<>();
        this.predecessors = new LinkedHashSet<>();
    }

    public Activity(long id, long externalId, String name, String description, Set<WorkUnit> workUnits,
                    Set<Activity> predecessors) {
        super(id, externalId, name, description);
        this.workUnits = workUnits;
        this.predecessors = predecessors;
    }

    @OneToMany
    public Set<WorkUnit> getWorkUnits() {
        return this.workUnits;
    }

    //@Override
    public void setWorkUnits(Set<WorkUnit> workUnits) {
        this.workUnits = workUnits;
    }

    @ManyToMany
    @JoinTable(name = "Activity_Predecessor", joinColumns = @JoinColumn(name = "activity", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "predecessor", referencedColumnName = "id"))
    public Set<Activity> getPredecessors() {
        return predecessors;
    }

    public void setPredecessors(Set<Activity> predecessors) {
        this.predecessors = predecessors;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Activity activity = (Activity) o;

        return !(predecessors != null ? !predecessors.equals(activity.predecessors) : activity.predecessors != null);

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (predecessors != null ? predecessors.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Activity{" +
                "predecessors=" + predecessors +
                '}';
    }
}
