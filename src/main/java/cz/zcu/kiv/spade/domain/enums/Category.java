package cz.zcu.kiv.spade.domain.enums;

import cz.zcu.kiv.spade.domain.abstracts.DescribedEntity;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "priority")
public class Category extends DescribedEntity{

    public Category() {
        super();
    }
}
