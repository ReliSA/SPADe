package cz.zcu.kiv.spade.domain;

import cz.zcu.kiv.spade.domain.abstracts.ProjectSegment;

import javax.persistence.Entity;
import java.util.Date;

@Entity
public class Activity extends ProjectSegment {

    public Activity() {
    }

    public Activity(long id, String externalId, String name, String description, Project project, Date startDate, Date endDate) {
        super(id, externalId, name, description, project, startDate, endDate);
    }

}
