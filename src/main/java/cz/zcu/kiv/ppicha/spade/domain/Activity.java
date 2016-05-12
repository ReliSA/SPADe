package cz.zcu.kiv.ppicha.spade.domain;

import cz.zcu.kiv.ppicha.spade.domain.abstracts.DescribedEntity;

import javax.persistence.*;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
public class Activity extends DescribedEntity {

    private Set<WorkUnit> workUnits;

    public Activity() {
        this.workUnits = new LinkedHashSet<>();
    }

    public Activity(long id, String externalId, String name, String description, Set<WorkUnit> workUnits,
                    Set<Activity> predecessors) {
        super(id, externalId, name, description);
        this.workUnits = workUnits;
    }

    @OneToMany
    @JoinTable(name = "Activity_WorkUnit", joinColumns = @JoinColumn(name = "activity_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "work_unit_id", referencedColumnName = "id"))
    public Set<WorkUnit> getWorkUnits() {
        return this.workUnits;
    }

    public void setWorkUnits(Set<WorkUnit> workUnits) {
        this.workUnits = workUnits;
    }

}
