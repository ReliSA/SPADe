package cz.zcu.kiv.spade.domain.abstracts;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class BaseEntity {

    protected long id;
    protected String externalId;

    public BaseEntity() {
        this.id = 0;
    }

    public BaseEntity(long id, String externalId) {
        this.id = id;
        this.externalId = externalId;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    @Override
    public String toString() {
        return "ID: " + id + "\n" +
                "External ID: " + externalId + "\n";
    }
}
