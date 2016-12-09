package cz.zcu.kiv.spade.domain;

import cz.zcu.kiv.spade.domain.abstracts.AuthoredEntity;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "configuration")
public class Configuration extends AuthoredEntity {

    private int number;
    private Date committed;
    private List<WorkItemChange> changes;
    private boolean isRelease;
    private Collection<WorkUnit> workUnits;
    private Collection<Branch> branches;
    private Collection<VCSTag> tags;
    private Collection<ConfigPersonRelation> relations;
    /*private Collection<Configuration> parents;
    private boolean mergePoint, branchPoint;
    private int childrenCont;*/

    public Configuration() {
        super();
        this.changes = new ArrayList<>();
        this.workUnits = new LinkedHashSet<>();
        this.tags = new LinkedHashSet<>();
        this.branches = new LinkedHashSet<>();
        this.relations = new LinkedHashSet<>();
        /*this.parents = new LinkedHashSet<>();
        this.mergePoint = this.branchPoint = false;
        childrenCont = 0;*/
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
    public List<WorkItemChange> getChanges() {
        return changes;
    }

    public void setChanges(List<WorkItemChange> changes) {
        this.changes = changes;
    }

    public boolean getIsRelease() {
        return isRelease;
    }

    public void setIsRelease(boolean isRelease) {
        this.isRelease = isRelease;
    }

    @ManyToMany
    @JoinTable(name = "configuration_work_unit", joinColumns = @JoinColumn(name = "configurationId", referencedColumnName = "id"),
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

    /*@Transient
    public Collection<Configuration> getParents() {
        return parents;
    }

    public void setParents(Collection<Configuration> parents) {
        this.parents = parents;
    }

    @Transient
    public boolean isMergePoint() {
        return mergePoint;
    }

    public void setMergePoint(boolean mergePoint) {
        this.mergePoint = mergePoint;
    }

    @Transient
    public boolean isBranchPoint() {
        return branchPoint;
    }

    public void setBranchPoint(boolean branchPoint) {
        this.branchPoint = branchPoint;
    }

    @Transient
    public int getChildrenCont() {
        return childrenCont;
    }

    public void raiseChildrenCont() {
        this.childrenCont++;
    }*/
}
