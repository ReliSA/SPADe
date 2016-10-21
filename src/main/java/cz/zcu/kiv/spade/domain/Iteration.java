package cz.zcu.kiv.spade.domain;

import cz.zcu.kiv.spade.domain.abstracts.DefinedProjectSegment;

import javax.persistence.*;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashSet;

@Entity
public class Iteration extends DefinedProjectSegment {

    public Iteration() {
    }

    public Iteration(long id, String externalId, String name, String description, Project project, Date startDate, Date endDate,
                     Date created, Configuration configuration) {
        super(id, externalId, name, description, project, startDate, endDate, created, configuration);
    }

}
