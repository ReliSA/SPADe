package cz.zcu.kiv.spade.domain;

import cz.zcu.kiv.spade.domain.abstracts.AuthoredEntity;

import javax.persistence.*;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashSet;

@Entity
public class Configuration extends AuthoredEntity {

    private int number;
    private Collection<WorkItemChange> changes;
    private boolean isRelease;
    private Collection<Artifact> artifacts;
    private Collection<WorkUnit> workUnits;
    private Collection<Branch> branches;
    private Collection<VCSTag> tags;
    private Collection<ConfigPersonRelation> relations;

    public Configuration() {
        this.changes = new LinkedHashSet<>();
        this.artifacts = new LinkedHashSet<>();
        this.workUnits = new LinkedHashSet<>();
        this.tags = new LinkedHashSet<>();
        this.branches = new LinkedHashSet<>();
        this.relations = new LinkedHashSet<>();
    }

    public Configuration(long id, String externalId, String name, String description, int number, Date created, Person author, Collection<WorkItemChange> changes,
                         boolean isRelease, Collection<Artifact> artifacts, Collection<WorkUnit> workUnits, Collection<Branch> branches,
                         Collection<VCSTag> tags, Collection<ConfigPersonRelation> relations) {
        super(id, externalId, name, description, created, author);
        this.number = number;
        this.changes = changes;
        this.isRelease = isRelease;
        this.artifacts = artifacts;
        this.workUnits = workUnits;
        this.branches = branches;
        this.tags = tags;
        this.relations = relations;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    @OneToMany
    @JoinTable(name = "Configuration_Change", joinColumns = @JoinColumn(name = "configuration_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "change_id", referencedColumnName = "id"))
    public Collection<WorkItemChange> getChanges() {
        return changes;
    }

    public void setChanges(Collection<WorkItemChange> changes) {
        this.changes = changes;
    }

    public boolean getIsRelease() {
        return isRelease;
    }

    public void setIsRelease(boolean isRelease) {
        this.isRelease = isRelease;
    }

    @ManyToMany
    @JoinTable(name = "Configuration_Artifact", joinColumns = @JoinColumn(name = "configuration_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "artifact_id", referencedColumnName = "id"))
    public Collection<Artifact> getArtifacts() {
        return artifacts;
    }

    public void setArtifacts(Collection<Artifact> artifacts) {
        this.artifacts = artifacts;
    }

    @ManyToMany
    @JoinTable(name = "Configuration_WorkUnit", joinColumns = @JoinColumn(name = "configuration_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "work_unit_id", referencedColumnName = "id"))
    public Collection<WorkUnit> getWorkUnits() {
        return workUnits;
    }

    public void setWorkUnits(Collection<WorkUnit> workUnits) {
        this.workUnits = workUnits;
    }

    @ManyToMany
    @JoinTable(name = "Configuration_Branch", joinColumns = @JoinColumn(name = "configuration_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "branch_id", referencedColumnName = "id"))
    public Collection<Branch> getBranches() {
        return branches;
    }

    public void setBranches(Collection<Branch> branches) {
        this.branches = branches;
    }

    @OneToMany
    @JoinColumn(name = "configuration_id")
    public Collection<VCSTag> getTags() {
        return tags;
    }

    public void setTags(Collection<VCSTag> tags) {
        this.tags = tags;
    }

    @OneToMany
    @JoinColumn(name = "configuration_id")
    public Collection<ConfigPersonRelation> getRelations() {
        return relations;
    }

    public void setRelations(Collection<ConfigPersonRelation> relations) {
        this.relations = relations;
    }
}
