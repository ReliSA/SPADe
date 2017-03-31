package cz.zcu.kiv.spade.domain.abstracts;

import com.fasterxml.jackson.databind.deser.Deserializers;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class ExternalEntity extends BaseEntity{

    protected String externalId;

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
