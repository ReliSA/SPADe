package cz.zcu.kiv.ppicha.spade.domain.abstracts;

import javax.persistence.*;

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

    @Column(updatable = false)
    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

}
