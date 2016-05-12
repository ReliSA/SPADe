package cz.zcu.kiv.ppicha.spade.domain;

import cz.zcu.kiv.ppicha.spade.domain.abstracts.ProjectSegment;

import javax.persistence.*;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
public class Iteration extends ProjectSegment {

    private Set<WorkUnit> workUnits;
    private Set<Activity> activities;
    private Configuration configuration;

    public Iteration() {
        this.workUnits = new LinkedHashSet<>();
        this.activities = new LinkedHashSet<>();
    }

    public Iteration(long id, String externalId, String name, String description, Date startDate, Date endDate,
                     Set<WorkUnit> workUnits, Set<Activity> activities,
                     Configuration configuration) {
        super(id, externalId, name, description, startDate, endDate);
        this.workUnits = workUnits;
        this.activities = activities;
        this.configuration = configuration;
    }

    @OneToMany
    @JoinTable(name = "Iteration_WorkUnit", joinColumns = @JoinColumn(name = "iteration_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "work_unit_id", referencedColumnName = "id"))
    public Set<WorkUnit> getWorkUnits() {
        return this.workUnits;
    }

    public void setWorkUnits(Set<WorkUnit> workUnits) {
        this.workUnits = workUnits;
    }

    @OneToMany
    @JoinTable(name = "Iteration_Activity", joinColumns = @JoinColumn(name = "iteration_id", referencedColumnName = "id"),
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

}
