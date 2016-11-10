package cz.zcu.kiv.spade.domain;

import cz.zcu.kiv.spade.domain.abstracts.DescribedEntity;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "competency")
public class Competency extends DescribedEntity {

    public Competency() {
        super();
    }

}
