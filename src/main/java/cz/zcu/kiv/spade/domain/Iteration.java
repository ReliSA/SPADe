package cz.zcu.kiv.spade.domain;

import cz.zcu.kiv.spade.domain.abstracts.DefinedProjectSegment;

import javax.persistence.*;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashSet;

@Entity
public class Iteration extends DefinedProjectSegment {

    private Collection<Activity> activities;

    public Iteration() {
        this.activities = new LinkedHashSet<>();
    }

    public Iteration(long id, String externalId, String name, String description, Project project, Date startDate, Date endDate,
                     Date created, Configuration configuration, Collection<Activity> activities) {
        super(id, externalId, name, description, project, startDate, endDate, created, configuration);
        this.activities = activities;
    }

    @OneToMany
    @JoinTable(name = "Iteration_Activity", joinColumns = @JoinColumn(name = "iteration_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "activity_id", referencedColumnName = "id"))
    public Collection<Activity> getActivities() {
        return activities;
    }

    public void setActivities(Collection<Activity> activities) {
        this.activities = activities;
    }

}
