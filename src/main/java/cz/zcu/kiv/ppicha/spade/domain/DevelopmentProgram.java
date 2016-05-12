package cz.zcu.kiv.ppicha.spade.domain;

import cz.zcu.kiv.ppicha.spade.domain.abstracts.DescribedEntity;
import cz.zcu.kiv.ppicha.spade.domain.enums.ProgramType;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import java.util.Set;

@Entity
public class DevelopmentProgram extends DescribedEntity {

    private ProgramType type;
    private Set<Project> projects;

    public DevelopmentProgram() {
    }

    public DevelopmentProgram(long id, String externalId, String name, String description, Set<Project> projects) {
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
    @JoinTable(name = "DevelopmentProgram_Project", joinColumns = @JoinColumn(name = "program_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "project_id", referencedColumnName = "id"))
    public Set<Project> getProjects() {
        return projects;
    }

    public void setProjects(Set<Project> workUnits) {
        this.projects = workUnits;
    }

}
