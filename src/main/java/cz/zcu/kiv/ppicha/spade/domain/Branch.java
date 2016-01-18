package cz.zcu.kiv.ppicha.spade.domain;

import cz.zcu.kiv.ppicha.spade.domain.abstracts.NamedAndDescribedEntity;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class Branch extends NamedAndDescribedEntity {

    private boolean isTrunk;

    public Branch() {
    }

    public Branch(long id, long externalId, String name, String description, boolean isTrunk) {
        super(id, externalId, name, description);
        this.isTrunk = isTrunk;
    }

    @Column(nullable = false, updatable = false)
    public boolean getIsTrunk() {
        return isTrunk;
    }

    public void setIsTrunk(boolean isTrunk) {
        this.isTrunk = isTrunk;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Branch branch = (Branch) o;

        return isTrunk == branch.isTrunk;

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (isTrunk ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Branch{" +
                "isTrunk=" + isTrunk +
                '}';
    }
}
