package cz.zcu.kiv.ppicha.spade.domain;

import cz.zcu.kiv.ppicha.spade.domain.abstracts.NamedAndDescribedEntity;
import cz.zcu.kiv.ppicha.spade.domain.enums.ProgramType;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.Set;

@Entity
public class DevelopmentProgram extends NamedAndDescribedEntity {

    private ProgramType type;
    private Set<Project> projects;

    public DevelopmentProgram() {
    }

    public DevelopmentProgram(long id, long externalId, String name, String description, Set<Project> projects) {
        super(id, externalId, name, description);
        this.projects = projects;
    }

    public ProgramType getType() {
        return type;
    }

    public void setType(ProgramType type) {
        this.type = type;
    }

    @OneToMany
    public Set<Project> getProjects() {
        return projects;
    }

    public void setProjects(Set<Project> workUnits) {
        this.projects = workUnits;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        DevelopmentProgram that = (DevelopmentProgram) o;

        if (type != that.type) return false;
        return projects != null ? projects.equals(that.projects) : that.projects == null;

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (projects != null ? projects.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "DevelopmentProgram{" +
                "type=" + type +
                ", projects=" + projects +
                '}';
    }
}
