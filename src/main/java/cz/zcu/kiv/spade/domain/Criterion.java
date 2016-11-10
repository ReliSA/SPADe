package cz.zcu.kiv.spade.domain;

import cz.zcu.kiv.spade.domain.abstracts.DescribedEntity;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "criterion")
public class Criterion extends DescribedEntity {

    public Criterion() {
        super();
    }

}
