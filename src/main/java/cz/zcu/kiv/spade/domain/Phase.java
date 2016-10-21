package cz.zcu.kiv.spade.domain;

import cz.zcu.kiv.spade.domain.abstracts.DefinedProjectSegment;

import javax.persistence.*;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
public class Phase extends DefinedProjectSegment {

    private Milestone milestone;

    public Phase(){
    }

    public Phase(long id, String externalId, String name, String description, Project project, Date startDate, Date endDate, Date created,
                 Configuration configuration, Milestone milestone) {
        super(id, externalId, name, description, project, startDate, endDate, created, configuration);
        this.milestone = milestone;
    }

    @OneToOne(fetch = FetchType.LAZY)
    public Milestone getMilestone() {
        return milestone;
    }

    public void setMilestone(Milestone milestone) {
        this.milestone = milestone;
    }

}
