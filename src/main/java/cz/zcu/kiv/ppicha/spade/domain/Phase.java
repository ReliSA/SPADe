package cz.zcu.kiv.ppicha.spade.domain;

import cz.zcu.kiv.ppicha.spade.domain.abstracts.TemporalNamedAndDescribedEntity;

import javax.persistence.*;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
public class Phase extends TemporalNamedAndDescribedEntity {

    private Set<WorkUnit> workUnits;
    private Set<Activity> activities;
    private Phase predecessor;
    private Configuration release;
    private Milestone milestone;

    public Phase() {
        this.workUnits = new LinkedHashSet<>();
        this.activities = new LinkedHashSet<>();
    }

    public Phase(long id, long externalId, String name, String description, Date startDate, Date endDate,
                 Set<WorkUnit> workUnits, Set<Activity> activities, Phase predecessor, Configuration release,
                 Milestone milestone) {
        super(id, externalId, name, description, startDate, endDate);
        this.workUnits = workUnits;
        this.activities = activities;
        this.predecessor = predecessor;
        this.release = release;
        this.milestone = milestone;
    }

    @ManyToMany
    @JoinTable(name = "Phase_WorkUnit", joinColumns = @JoinColumn(name = "phase", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "work_unit", referencedColumnName = "id"))
    public Set<WorkUnit> getWorkUnits() {
        return workUnits;
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
    public Phase getPredecessor() {
        return predecessor;
    }

    @OneToOne(fetch = FetchType.LAZY)
    public void setPredecessor(Phase predecessor) {
        this.predecessor = predecessor;
    }

    @OneToOne(fetch = FetchType.LAZY)
    public Configuration getRelease() {
        return release;
    }

    public void setRelease(Configuration release) {
        this.release = release;
    }

    @OneToOne(fetch = FetchType.LAZY)
    public Milestone getMilestone() {
        return milestone;
    }

    public void setMilestone(Milestone milestone) {
        this.milestone = milestone;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Phase phase = (Phase) o;

        if (workUnits != null ? !workUnits.equals(phase.workUnits) : phase.workUnits != null) return false;
        if (activities != null ? !activities.equals(phase.activities) : phase.activities != null) return false;
        if (predecessor != null ? !predecessor.equals(phase.predecessor) : phase.predecessor != null) return false;
        if (release != null ? !release.equals(phase.release) : phase.release != null) return false;
        return !(milestone != null ? !milestone.equals(phase.milestone) : phase.milestone != null);

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (workUnits != null ? workUnits.hashCode() : 0);
        result = 31 * result + (activities != null ? activities.hashCode() : 0);
        result = 31 * result + (predecessor != null ? predecessor.hashCode() : 0);
        result = 31 * result + (release != null ? release.hashCode() : 0);
        result = 31 * result + (milestone != null ? milestone.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Phase{" +
                "workUnits=" + workUnits +
                ", activities=" + activities +
                ", predecessor=" + predecessor +
                ", release=" + release +
                ", milestone=" + milestone +
                '}';
    }
}
