package cz.zcu.kiv.spade.domain;

import cz.zcu.kiv.spade.domain.abstracts.DescribedEntity;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "category")
public class WorkUnitCategory extends DescribedEntity {

    public WorkUnitCategory() {
        super();
    }
}
