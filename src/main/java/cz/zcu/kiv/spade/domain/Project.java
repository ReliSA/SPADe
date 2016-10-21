package cz.zcu.kiv.spade.domain;

import cz.zcu.kiv.spade.domain.abstracts.ProjectSegment;

import javax.persistence.*;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashSet;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class Project extends ProjectSegment {

    protected Collection<Person> personnel;

    public Project() {
        this.personnel = new LinkedHashSet<>();
    }

    public Project(long id, String externalId, String name, String description, DevelopmentProgram program, Date startDate, Date endDate,
                   Collection<Person> personnel) {
        super(id, externalId, name, description, program, startDate, endDate);
        this.personnel = personnel;
    }

    @ManyToMany
    @JoinTable(name = "Project_Person", joinColumns = @JoinColumn(name = "project_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "member_id", referencedColumnName = "id"))
    public Collection<Person> getPersonnel() {
        return personnel;
    }

    public void setPersonnel(Collection<Person> personnel) {
        this.personnel = personnel;
    }

}
