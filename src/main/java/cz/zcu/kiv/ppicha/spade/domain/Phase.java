package cz.zcu.kiv.ppicha.spade.domain;

import cz.zcu.kiv.ppicha.spade.domain.abstracts.ProjectSegment;

import javax.persistence.*;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
public class Phase extends ProjectSegment {

    private Set<WorkUnit> workUnits;
    private Set<Activity> activities;
    private Configuration configuration;
    private Milestone milestone;

    public Phase() {
        this.workUnits = new LinkedHashSet<>();
        this.activities = new LinkedHashSet<>();
    }

    public Phase(long id, String externalId, String name, String description, Date startDate, Date endDate,
                 Set<WorkUnit> workUnits, Set<Activity> activities, Phase predecessor, Configuration configuration,
                 Milestone milestone) {
        super(id, externalId, name, description, startDate, endDate);
        this.workUnits = workUnits;
        this.activities = activities;
        this.configuration = configuration;
        this.milestone = milestone;
    }

    @OneToMany
    @JoinTable(name = "Phase_WorkUnit", joinColumns = @JoinColumn(name = "phase_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "work_unit_id", referencedColumnName = "id"))
    public Set<WorkUnit> getWorkUnits() {
        return workUnits;
    }

    public void setWorkUnits(Set<WorkUnit> workUnits) {
        this.workUnits = workUnits;
    }

    @OneToMany
    @JoinTable(name = "Phase_Activity", joinColumns = @JoinColumn(name = "phase_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "activity_id", referencedColumnName = "id"))
    public Set<Activity> getActivities() {
        return activities;
    }

    public void setActivities(Set<Activity> activities) {
        this.activities = activities;
    }

    @OneToOne(fetch = FetchType.LAZY)
    public Configuration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    @OneToOne(fetch = FetchType.LAZY)
    public Milestone getMilestone() {
        return milestone;
    }

    public void setMilestone(Milestone milestone) {
        this.milestone = milestone;
    }

}
