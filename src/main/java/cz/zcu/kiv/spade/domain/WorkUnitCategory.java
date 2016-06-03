package cz.zcu.kiv.spade.domain;

import cz.zcu.kiv.spade.domain.abstracts.DescribedEntity;

import javax.persistence.Entity;

@Entity
public class WorkUnitCategory extends DescribedEntity {

    public WorkUnitCategory() {
    }

    public WorkUnitCategory(long id, String externalId, String name, String description) {
        super(id, externalId, name, description);
    }
}
