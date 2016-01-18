package cz.zcu.kiv.ppicha.spade.domain.abstracts;

import javax.persistence.*;

@MappedSuperclass
public abstract class BaseEntity {

    protected long id;
    protected long externalId;

    public BaseEntity() {
    }

    public BaseEntity(long id, long externalId) {
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
    public long getExternalId() {
        return externalId;
    }

    public void setExternalId(long externalId) {
        this.externalId = externalId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BaseEntity that = (BaseEntity) o;

        if (id != that.id) return false;
        return externalId == that.externalId;

    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (int) (externalId ^ (externalId >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "BaseEntity{" +
                "id=" + id +
                ", externalId=" + externalId +
                '}';
    }
}
