package cz.zcu.kiv.spade.domain.abstracts;

import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class ExternalEntity extends BaseEntity {

    private String externalId;

    public ExternalEntity() {
        super();
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

}
