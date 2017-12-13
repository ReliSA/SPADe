package cz.zcu.kiv.spade.domain;

import cz.zcu.kiv.spade.domain.abstracts.ProjectSegment;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "project")
@Inheritance(strategy = InheritanceType.JOINED)
public class Project extends ProjectSegment {

    //protected Collection<Person> watchers;
    private Collection<Person> people;
    private Collection<Configuration> configurations;
    private Map<Integer, WorkUnit> units;
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
    public void addUnit(WorkUnit unit) {
        units.put(unit.getNumber(), unit);
    }

    @Transient
    public WorkUnit getUnit(String number) {
        return units.get(Integer.parseInt(number));
    }

    @Transient
    public WorkUnit getUnit(int number) {
        return units.get(number);
    }

    @Transient
    public boolean containsCommit(String identifier) {
        return commits.containsKey(identifier);
    }

    @Transient
    public boolean containsUnit(String number) {
        return units.containsKey(Integer.parseInt(number));
    }

    @Transient
    public void addCommit(Commit commit) {
        commits.put(commit.getIdentifier(), commit);
        configurations.add(commit);
    }

    @Transient
    public Commit getCommit(String identifier) {
        return commits.get(identifier);
    }

    @Transient
    public Set<WorkItem> getAllItems() {
        Set<WorkItem> items = new LinkedHashSet<>();
        items.addAll(units.values());
        for (Configuration configuration : getConfigurations()) {
            items.add(configuration);
            for (WorkItemChange change : configuration.getChanges()) {
                items.add(change.getChangedItem());
            }
        }
        return items;
    }

    @Transient
    public Collection<Commit> getCommits() {
        return commits.values();
    }
}
