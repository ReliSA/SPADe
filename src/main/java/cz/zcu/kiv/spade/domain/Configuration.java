package cz.zcu.kiv.spade.domain;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "configuration")
@DiscriminatorValue("CONFIGURATION")
public class Configuration extends WorkItem {

    protected List<WorkItemChange> changes;

    public Configuration() {
        super();
        this.changes = new ArrayList<>();
    }

    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "configuration_change", joinColumns = @JoinColumn(name = "configurationId", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "changeId", referencedColumnName = "id"))
    public List<WorkItemChange> getChanges() {
        return changes;
    }

    public void setChanges(List<WorkItemChange> changes) {
        this.changes = changes;
    }

}
