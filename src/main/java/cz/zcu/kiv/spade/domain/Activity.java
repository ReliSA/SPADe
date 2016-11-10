package cz.zcu.kiv.spade.domain;

import cz.zcu.kiv.spade.domain.abstracts.ProjectSegment;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "activity")
public class Activity extends ProjectSegment {

    public Activity() {
        super();
    }

}
