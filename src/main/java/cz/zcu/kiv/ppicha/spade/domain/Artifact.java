package cz.zcu.kiv.ppicha.spade.domain;

import cz.zcu.kiv.ppicha.spade.domain.enums.ArtifactType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.util.Date;

@Entity
public class Artifact extends WorkItem {

    private ArtifactType type;

    public Artifact() {
    }

    public Artifact(long id, long externalId, String name, String description, Date created, Person author, String url,
                    ArtifactType type) {
        super(id, externalId, name, description, created, author, url);
        this.type = type;
    }

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false, updatable = false)
    public ArtifactType getType() {
        return type;
    }

    public void setType(ArtifactType type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Artifact artifact = (Artifact) o;

        return type == artifact.type;

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (type != null ? type.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Artifact{" +
                "type=" + type +
                '}';
    }
}
