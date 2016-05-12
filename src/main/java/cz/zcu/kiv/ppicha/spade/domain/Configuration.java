package cz.zcu.kiv.ppicha.spade.domain;

import cz.zcu.kiv.ppicha.spade.domain.abstracts.AuthoredEntity;

import javax.persistence.*;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
public class Configuration extends AuthoredEntity {

    protected int number;
    protected Set<WorkItemChange> changes;
    protected boolean isRevision;
    protected Set<Artifact> artifacts;
    protected Set<WorkUnit> workUnits;
    protected Branch branch;
    protected String tags;

    public Configuration() {
        this.changes = new LinkedHashSet<>();
        this.artifacts = new LinkedHashSet<>();
        this.workUnits = new LinkedHashSet<>();
    }

    public Configuration(long id, String externalId, String name, String description, int number, Date created, Identity author, Set<WorkItemChange> changes, WorkItemChange lastWorkItemChange,
                         boolean isRevision, Set<Artifact> artifacts, Set<WorkUnit> workUnits, Branch branch,
                         String tags) {
        super(id, externalId, name, description, created, author);
        this.number = number;
        this.changes = changes;
        this.isRevision = isRevision;
        this.artifacts = artifacts;
        this.workUnits = workUnits;
        this.branch = branch;
        this.tags = tags;
    }

    @Column(updatable = false)
    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public Set<WorkItemChange> getChanges() {
        return changes;
    }

    @OneToMany
    @JoinTable(name = "Configuration_Change", joinColumns = @JoinColumn(name = "configuration_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "changet_id", referencedColumnName = "id"))
    public void setChanges(Set<WorkItemChange> changes) {
        this.changes = changes;
    }

    @Column(nullable = false, updatable = false)
    public boolean getIsRevision() {
        return isRevision;
    }

    public void setIsRevision(boolean isRevision) {
        this.isRevision = isRevision;
    }

    @ManyToMany
    @JoinTable(name = "Configuration_Artifact", joinColumns = @JoinColumn(name = "configuration_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "artifact_id", referencedColumnName = "id"))
    public Set<Artifact> getArtifacts() {
        return artifacts;
    }

    public void setArtifacts(Set<Artifact> artifacts) {
        this.artifacts = artifacts;
    }

    @ManyToMany
    @JoinTable(name = "Configuration_WorkUnit", joinColumns = @JoinColumn(name = "configuration_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "work_unit_id", referencedColumnName = "id"))
    public Set<WorkUnit> getWorkUnits() {
        return workUnits;
    }

    public void setWorkUnits(Set<WorkUnit> workUnits) {
        this.workUnits = workUnits;
    }

    @OneToOne(fetch = FetchType.LAZY)
    public Branch getBranch() {
        return branch;
    }

    public void setBranch(Branch branch) {
        this.branch = branch;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }
}
