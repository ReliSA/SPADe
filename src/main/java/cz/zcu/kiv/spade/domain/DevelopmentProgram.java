package cz.zcu.kiv.spade.domain;

import cz.zcu.kiv.spade.domain.enums.ProgramType;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import java.util.Collection;
import java.util.Date;

@Entity
public class DevelopmentProgram extends Project {

    private ProgramType type;
    private Collection<Project> projects;

    public DevelopmentProgram() {
    }

    public DevelopmentProgram(long id, String externalId, String name, String description, Project project, Date startDate, Date endDate,
                              Collection<Person> personnel, ProgramType type, Collection<Project> projects) {
        super(id, externalId, name, description, project, startDate, endDate, personnel);
        this.type = type;
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
    public Collection<Project> getProjects() {
        return projects;
    }

    public void setProjects(Collection<Project> workUnits) {
        this.projects = workUnits;
    }

}
