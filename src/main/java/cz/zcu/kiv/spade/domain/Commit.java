package cz.zcu.kiv.spade.domain;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "commit")
@DiscriminatorValue("COMMIT")
public class Commit extends CommittedConfiguration {

    private String identifier;
    private boolean isRelease;
    private Collection<VCSTag> tags;
    private Collection<Branch> branches;
    private Collection<ConfigPersonRelation> relations;
    /*private Collection<Configuration> parents;
    private boolean mergePoint, branchPoint;
    private int childrenCont;*/

    public Commit() {
        super();
        this.tags = new LinkedHashSet<>();
        this.branches = new LinkedHashSet<>();
        this.relations = new LinkedHashSet<>();
        /*this.parents = new LinkedHashSet<>();
        this.mergePoint = this.branchPoint = false;
        childrenCont = 0;*/
    }

    public Commit(String identifier) {
        super();
        this.tags = new LinkedHashSet<>();
        this.branches = new LinkedHashSet<>();
        this.relations = new LinkedHashSet<>();
        this.identifier = identifier;
        /*this.parents = new LinkedHashSet<>();
        this.mergePoint = this.branchPoint = false;
        childrenCont = 0;*/
    }

    @Column(length = 7)
    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public boolean getIsRelease() {
        return isRelease;
    }

    public void setIsRelease(boolean isRelease) {
        this.isRelease = isRelease;
    }

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "configuration_branch", joinColumns = @JoinColumn(name = "configurationId", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "branchId", referencedColumnName = "id"))
    public Collection<Branch> getBranches() {
        return branches;
    }

    public void setBranches(Collection<Branch> branches) {
        this.branches = branches;
    }

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "configurationId")
    public Collection<VCSTag> getTags() {
        return tags;
    }

    public void setTags(Collection<VCSTag> tags) {
        this.tags = tags;
    }

    @OneToMany(cascade = CascadeType.ALL)
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
