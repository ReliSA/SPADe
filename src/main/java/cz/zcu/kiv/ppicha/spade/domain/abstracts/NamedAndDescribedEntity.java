package cz.zcu.kiv.ppicha.spade.domain.abstracts;

import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class NamedAndDescribedEntity extends NamedEntity {

    protected String description;

    public NamedAndDescribedEntity() {
    }

    public NamedAndDescribedEntity(long id, long externalId, String name, String description) {
        super(id, externalId, name);
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        NamedAndDescribedEntity that = (NamedAndDescribedEntity) o;

        return !(description != null ? !description.equals(that.description) : that.description != null);

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (description != null ? description.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Competency{" +
                "description='" + description + '\'' +
                '}';
    }
}
