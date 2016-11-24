package cz.zcu.kiv.spade.domain;

import cz.zcu.kiv.spade.domain.abstracts.ProjectSegment;

import javax.persistence.*;
import java.util.Collection;
import java.util.LinkedHashSet;

@Entity
@Table(name = "project")
@Inheritance(strategy = InheritanceType.JOINED)
public class Project extends ProjectSegment {

    //protected Collection<Person> watchers;
    protected Collection<Configuration> configurations;

    public Project() {
        super();
        //this.watchers = new LinkedHashSet<>();
        this.configurations = new LinkedHashSet<>();
    }

    /*@ManyToMany
    @JoinTable(name = "project_watcher", joinColumns = @JoinColumn(name = "projectId", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "watcherId", referencedColumnName = "id"))
    public Collection<Person> getWatchers() {
        return watchers;
    }

    public void setWatchers(Collection<Person> watchers) {
        this.watchers = watchers;
    }*/

    @OneToMany
    @JoinColumn(name = "projectId")
    public Collection<Configuration> getConfigurations() {
        return configurations;
    }

    public void setConfigurations(Collection<Configuration> configurations) {
        this.configurations = configurations;
    }
}
