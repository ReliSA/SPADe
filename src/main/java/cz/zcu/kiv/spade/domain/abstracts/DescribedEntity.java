package cz.zcu.kiv.spade.domain.abstracts;

import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class DescribedEntity extends NamedEntity {

    protected String description;

    public DescribedEntity() {
    }

    public DescribedEntity(long id, String externalId, String name, String description) {
        super(id, externalId, name);
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
