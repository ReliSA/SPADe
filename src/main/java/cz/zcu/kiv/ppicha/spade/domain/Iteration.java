package cz.zcu.kiv.ppicha.spade.domain;

import cz.zcu.kiv.ppicha.spade.domain.abstracts.TemporalNamedAndDescribedEntity;

import javax.persistence.*;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
public class Iteration extends TemporalNamedAndDescribedEntity {

    private Set<WorkUnit> workUnits;
    private Set<Activity> activities;
    private Iteration predecessor;
    private Configuration configuration;

    public Iteration() {
        this.workUnits = new LinkedHashSet<>();
        this.activities = new LinkedHashSet<>();
    }

    public Iteration(long id, long externalId, String name, String description, Date startDate, Date endDate,
                     Set<WorkUnit> workUnits, Set<Activity> activities, Iteration predecessor,
                     Configuration configuration) {
        super(id, externalId, name, description, startDate, endDate);
        this.workUnits = workUnits;
        this.activities = activities;
        this.predecessor = predecessor;
        this.configuration = configuration;
    }

    @ManyToMany
    @JoinTable(name = "Iteration_WorkUnit", joinColumns = @JoinColumn(name = "iteration", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "work_unit", referencedColumnName = "id"))
    public Set<WorkUnit> getWorkUnits() {
        return this.workUnits;
    }

    public void setWorkUnits(Set<WorkUnit> workUnits) {
        this.workUnits = workUnits;
    }

    @OneToMany
    public Set<Activity> getActivities() {
        return activities;
    }

    public void setActivities(Set<Activity> activities) {
        this.activities = activities;
    }

    @OneToOne(fetch = FetchType.LAZY)
    public Iteration getPredecessor() {
        return predecessor;
    }

    public void setPredecessor(Iteration predecessor) {
        this.predecessor = predecessor;
    }

    @OneToOne(fetch = FetchType.LAZY)
    public Configuration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Iteration iteration = (Iteration) o;

        if (workUnits != null ? !workUnits.equals(iteration.workUnits) : iteration.workUnits != null) return false;
        if (activities != null ? !activities.equals(iteration.activities) : iteration.activities != null) return false;
        if (predecessor != null ? !predecessor.equals(iteration.predecessor) : iteration.predecessor != null)
            return false;
        return !(configuration != null ? !configuration.equals(iteration.configuration) : iteration.configuration != null);

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (workUnits != null ? workUnits.hashCode() : 0);
        result = 31 * result + (activities != null ? activities.hashCode() : 0);
        result = 31 * result + (predecessor != null ? predecessor.hashCode() : 0);
        result = 31 * result + (configuration != null ? configuration.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Iteration{" +
                "workUnits=" + workUnits +
                ", activities=" + activities +
                ", predecessor=" + predecessor +
                ", configuration=" + configuration +
                '}';
    }
}
