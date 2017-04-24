package cz.zcu.kiv.spade.domain;

import cz.zcu.kiv.spade.domain.abstracts.ProjectSegment;

import javax.persistence.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

@Entity
@Table(name = "project")
@Inheritance(strategy = InheritanceType.JOINED)
public class Project extends ProjectSegment {

    //protected Collection<Person> watchers;
    private Collection<Person> people;
    private Collection<Configuration> configurations;
    private Map<Integer, WorkUnit> units;
    @Transient
    private Map<String, Commit> commits;

    public Project() {
        super();
        //this.watchers = new LinkedHashSet<>();
        this.people = new LinkedHashSet<>();
        this.configurations = new LinkedHashSet<>();
        this.units = new HashMap<>();
        this.commits = new HashMap<>();
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

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "projectId")
    public Collection<Person> getPeople() {
        return people;
    }

    public void setPeople(Collection<Person> people) {
        this.people = people;
    }

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "projectId")
    public Collection<Configuration> getConfigurations() {
        return configurations;
    }

    public void setConfigurations(Collection<Configuration> configurations) {
        this.configurations = configurations;
    }

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "projectId")
    public Collection<WorkUnit> getUnits() {
        return units.values();
    }

    public void setUnits(Collection<WorkUnit> units) {
        this.units.clear();
        for (WorkUnit unit : units) {
            this.units.put(unit.getNumber(), unit);
        }
    }

    @Transient
    private boolean containsUnit(int number) {
        return units.containsKey(number);
    }

    @Transient
    public WorkUnit addUnit(WorkUnit unit) {
        if (containsUnit(unit.getNumber()) && (getUnit(unit.getNumber()).getCreated() != null)) {
            getUnit(unit.getNumber()).getRelatedItems().addAll(unit.getRelatedItems());
            return getUnit(unit.getNumber());
        } else {
            WorkUnit oldUnit = units.put(unit.getNumber(), unit);
            if (oldUnit != null)
                unit.getRelatedItems().addAll(oldUnit.getRelatedItems());
            return unit;
        }
    }

    @Transient
    private WorkUnit getUnit(int number) {
        return units.get(number);
    }

    @Transient
    public boolean containsCommit(String identifier) {
        return commits.containsKey(identifier);
    }

    @Transient
    public Commit addCommit(Commit commit) {
        if (containsCommit(commit.getIdentifier()) && (getCommit(commit.getIdentifier()).getCommitted() != null)) {
            getCommit(commit.getIdentifier()).getRelatedItems().addAll(commit.getRelatedItems());
            return getCommit(commit.getIdentifier());
        } else {
            Commit oldCommit = commits.put(commit.getIdentifier(), commit);
            if (oldCommit != null) {
                commit.getRelatedItems().addAll(oldCommit.getRelatedItems());
                configurations.remove(oldCommit);
            }
            configurations.add(commit);
            return commit;
        }
    }

    @Transient
    public Commit getCommit(String identifier) {
        return commits.get(identifier);
    }
}
