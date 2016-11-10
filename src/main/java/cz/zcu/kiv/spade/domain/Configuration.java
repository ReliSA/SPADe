package cz.zcu.kiv.spade.domain;

import cz.zcu.kiv.spade.domain.abstracts.AuthoredEntity;

import javax.persistence.*;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashSet;

@Entity
@Table(name = "configuration")
public class Configuration extends AuthoredEntity {

    private int number;
    private Date committed;
    private Collection<WorkItemChange> changes;
    private boolean isRelease;
    private Collection<Artifact> artifacts;
    private Collection<WorkUnit> workUnits;
    private Collection<Branch> branches;
    private Collection<VCSTag> tags;
    private Collection<ConfigPersonRelation> relations;

    public Configuration() {
        super();
        this.changes = new LinkedHashSet<>();
        this.artifacts = new LinkedHashSet<>();
        this.workUnits = new LinkedHashSet<>();
        this.tags = new LinkedHashSet<>();
        this.branches = new LinkedHashSet<>();
        this.relations = new LinkedHashSet<>();
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    @Temporal(TemporalType.TIMESTAMP)
    public Date getCommitted() {
        return committed;
    }

    public void setCommitted(Date committed) {
        this.committed = committed;
    }

    @OneToMany
    @JoinTable(name = "configuration_change", joinColumns = @JoinColumn(name = "configurationId", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "changeId", referencedColumnName = "id"))
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
    @JoinTable(name = "configuration_artifact", joinColumns = @JoinColumn(name = "configurationId", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "artifactId", referencedColumnName = "id"))
    public Collection<Artifact> getArtifacts() {
        return artifacts;
    }

    public void setArtifacts(Collection<Artifact> artifacts) {
        this.artifacts = artifacts;
    }

    @ManyToMany
    @JoinTable(name = "configuration_workUnit", joinColumns = @JoinColumn(name = "configurationId", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "workUnitId", referencedColumnName = "id"))
    public Collection<WorkUnit> getWorkUnits() {
        return workUnits;
    }

    public void setWorkUnits(Collection<WorkUnit> workUnits) {
        this.workUnits = workUnits;
    }

    @ManyToMany
    @JoinTable(name = "configuration_branch", joinColumns = @JoinColumn(name = "configurationId", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "branchId", referencedColumnName = "id"))
    public Collection<Branch> getBranches() {
        return branches;
    }

    public void setBranches(Collection<Branch> branches) {
        this.branches = branches;
    }

    @OneToMany
    @JoinColumn(name = "configurationId")
    public Collection<VCSTag> getTags() {
        return tags;
    }

    public void setTags(Collection<VCSTag> tags) {
        this.tags = tags;
    }

    @OneToMany
    @JoinColumn(name = "configurationId")
    public Collection<ConfigPersonRelation> getRelations() {
        return relations;
    }

    public void setRelations(Collection<ConfigPersonRelation> relations) {
        this.relations = relations;
    }
}
