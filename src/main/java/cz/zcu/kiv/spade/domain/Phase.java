package cz.zcu.kiv.spade.domain;

import cz.zcu.kiv.spade.domain.abstracts.DefinedProjectSegment;

import javax.persistence.*;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
public class Phase extends DefinedProjectSegment {

    private Collection<Activity> activities;
    private Milestone milestone;

    public Phase() {
        this.activities = new LinkedHashSet<>();
    }

    public Phase(long id, String externalId, String name, String description, Project project, Date startDate, Date endDate, Date created,
                 Configuration configuration, Set<Activity> activities, Milestone milestone) {
        super(id, externalId, name, description, project, startDate, endDate, created, configuration);
        this.activities = activities;
        this.milestone = milestone;
    }

    @OneToMany
    @JoinTable(name = "Phase_Activity", joinColumns = @JoinColumn(name = "phase_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "activity_id", referencedColumnName = "id"))
    public Collection<Activity> getActivities() {
        return activities;
    }

    public void setActivities(Collection<Activity> activities) {
        this.activities = activities;
    }

    @OneToOne(fetch = FetchType.LAZY)
    public Milestone getMilestone() {
        return milestone;
    }

    public void setMilestone(Milestone milestone) {
        this.milestone = milestone;
    }

}
