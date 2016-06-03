package cz.zcu.kiv.spade.domain.abstracts;

import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class NamedEntity extends BaseEntity {

    protected String name;

    public NamedEntity() {
    }

    public NamedEntity(long id, String externalId, String name) {
        super(id, externalId);
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
