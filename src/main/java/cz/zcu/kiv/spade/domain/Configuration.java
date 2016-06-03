package cz.zcu.kiv.spade.domain;

import cz.zcu.kiv.spade.domain.abstracts.AuthoredEntity;

import javax.persistence.*;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashSet;

@Entity
public class Configuration extends AuthoredEntity {

    protected int number;
    protected Collection<WorkItemChange> changes;
    protected boolean isRevision;
    protected Collection<Artifact> artifacts;
    protected Collection<WorkUnit> workUnits;
    protected Branch branch;
    protected Collection<String> tags;

    public Configuration() {
        this.changes = new LinkedHashSet<>();
        this.artifacts = new LinkedHashSet<>();
        this.workUnits = new LinkedHashSet<>();
        this.tags = new LinkedHashSet<>();
    }

    public Configuration(long id, String externalId, String name, String description, int number, Date created, Identity author, Collection<WorkItemChange> changes, WorkItemChange lastWorkItemChange,
                         boolean isRevision, Collection<Artifact> artifacts, Collection<WorkUnit> workUnits, Branch branch,
                         Collection<String> tags) {
        super(id, externalId, name, description, created, author);
        this.number = number;
        this.changes = changes;
        this.isRevision = isRevision;
        this.artifacts = artifacts;
        this.workUnits = workUnits;
        this.branch = branch;
        this.tags = tags;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public Collection<WorkItemChange> getChanges() {
        return changes;
    }

    @OneToMany
    @JoinTable(name = "Configuration_Change", joinColumns = @JoinColumn(name = "configuration_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "changet_id", referencedColumnName = "id"))
    public void setChanges(Collection<WorkItemChange> changes) {
        this.changes = changes;
    }

    public boolean getIsRevision() {
        return isRevision;
    }

    public void setIsRevision(boolean isRevision) {
        this.isRevision = isRevision;
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

    @OneToOne(fetch = FetchType.LAZY)
    public Branch getBranch() {
        return branch;
    }

    public void setBranch(Branch branch) {
        this.branch = branch;
    }

    public Collection<String> getTags() {
        return tags;
    }

    public void setTags(Collection<String> tags) {
        this.tags = tags;
    }
}
