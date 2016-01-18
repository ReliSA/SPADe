package cz.zcu.kiv.ppicha.spade.domain;

import cz.zcu.kiv.ppicha.spade.domain.abstracts.NamedEntity;

import javax.persistence.*;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
public class Configuration extends NamedEntity {

    private int number;
    private WorkItemChange lastWorkItemChange;
    private boolean isRevision;
    private Set<Artifact> artifacts;
    private Set<WorkUnit> workUnits;
    private Branch branch;
    private boolean isTag;

    public Configuration() {
        this.artifacts = new LinkedHashSet<>();
        this.workUnits = new LinkedHashSet<>();
    }

    public Configuration(long id, long externalId, String name, int number, WorkItemChange lastWorkItemChange,
                         boolean isRevision, Set<Artifact> artifacts, Set<WorkUnit> workUnits, Branch branch,
                         boolean isTag) {
        super(id, externalId, name);
        this.number = number;
        this.lastWorkItemChange = lastWorkItemChange;
        this.isRevision = isRevision;
        this.artifacts = artifacts;
        this.workUnits = workUnits;
        this.branch = branch;
        this.isTag = isTag;
    }

    @Column(updatable = false)
    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    //@Column(nullable = false, updatable = false)
    @OneToOne(fetch = FetchType.LAZY)
    public WorkItemChange getLastWorkItemChange() {
        return lastWorkItemChange;
    }

    public void setLastWorkItemChange(WorkItemChange lastWorkItemChange) {
        this.lastWorkItemChange = lastWorkItemChange;
    }

    @Column(nullable = false, updatable = false)
    public boolean getIsRevision() {
        return isRevision;
    }

    public void setIsRevision(boolean isRevision) {
        this.isRevision = isRevision;
    }

    @ManyToMany
    @JoinTable(name = "Configuration_Artifact", joinColumns = @JoinColumn(name = "configuration", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "artifact", referencedColumnName = "id"))
    public Set<Artifact> getArtifacts() {
        return artifacts;
    }

    public void setArtifacts(Set<Artifact> artifacts) {
        this.artifacts = artifacts;
    }

    @ManyToMany
    @JoinTable(name = "Configuration_WorkUnit", joinColumns = @JoinColumn(name = "configuration", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "work_unit", referencedColumnName = "id"))
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

    @Column(updatable = false)
    public boolean getIsTag() {
        return isTag;
    }

    public void setIsTag(boolean isTag) {
        this.isTag = isTag;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Configuration that = (Configuration) o;

        if (number != that.number) return false;
        if (isRevision != that.isRevision) return false;
        if (isTag != that.isTag) return false;
        if (lastWorkItemChange != null ? !lastWorkItemChange.equals(that.lastWorkItemChange) : that.lastWorkItemChange != null)
            return false;
        if (artifacts != null ? !artifacts.equals(that.artifacts) : that.artifacts != null) return false;
        if (workUnits != null ? !workUnits.equals(that.workUnits) : that.workUnits != null) return false;
        return !(branch != null ? !branch.equals(that.branch) : that.branch != null);

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + number;
        result = 31 * result + (lastWorkItemChange != null ? lastWorkItemChange.hashCode() : 0);
        result = 31 * result + (isRevision ? 1 : 0);
        result = 31 * result + (artifacts != null ? artifacts.hashCode() : 0);
        result = 31 * result + (workUnits != null ? workUnits.hashCode() : 0);
        result = 31 * result + (branch != null ? branch.hashCode() : 0);
        result = 31 * result + (isTag ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Configuration{" +
                "number=" + number +
                ", lastWorkItemChange=" + lastWorkItemChange +
                ", isRevision=" + isRevision +
                ", artifacts=" + artifacts +
                ", workUnits=" + workUnits +
                ", branch=" + branch +
                ", isTag=" + isTag +
                '}';
    }
}
