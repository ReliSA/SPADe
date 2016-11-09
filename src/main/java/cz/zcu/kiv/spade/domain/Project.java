package cz.zcu.kiv.spade.domain;

import cz.zcu.kiv.spade.domain.abstracts.ProjectSegment;

import javax.persistence.*;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashSet;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class Project extends ProjectSegment {

    protected Collection<Person> watchers;
    protected Collection<Configuration> configurations;

    public Project() {
        this.watchers = new LinkedHashSet<>();
        this.configurations = new LinkedHashSet<>();
    }

    public Project(long id, String externalId, String name, String description, DevelopmentProgram program, Date startDate, Date endDate,
                   Collection<Person> watchers, Collection<Configuration> configurations) {
        super(id, externalId, name, description, program, startDate, endDate);
        this.watchers = watchers;
        this.configurations = configurations;
    }

    @ManyToMany
    @JoinTable(name = "Project_Watcher", joinColumns = @JoinColumn(name = "project_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "member_id", referencedColumnName = "id"))
    public Collection<Person> getWatchers() {
        return watchers;
    }

    public void setWatchers(Collection<Person> watchers) {
        this.watchers = watchers;
    }

    @OneToMany
    @JoinColumn(name = "project_id")
    public Collection<Configuration> getConfigurations() {
        return configurations;
    }

    public void setConfigurations(Collection<Configuration> configurations) {
        this.configurations = configurations;
    }
}
