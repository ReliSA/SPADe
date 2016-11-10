package cz.zcu.kiv.spade.domain;

import cz.zcu.kiv.spade.domain.abstracts.DefinedProjectSegment;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "iteration")
public class Iteration extends DefinedProjectSegment {

    public Iteration() {
        super();
    }

}
