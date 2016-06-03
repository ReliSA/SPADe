package cz.zcu.kiv.spade.domain;

import cz.zcu.kiv.spade.domain.abstracts.DescribedEntity;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
public class IdentityGroup extends DescribedEntity {

    private Collection<Identity> members;

    public IdentityGroup() {
        this.members = new LinkedHashSet<>();
    }

    public IdentityGroup(long id, String externalId, String name, String description, Set<Identity> members) {
        super(id, externalId, name, description);
        this.members = members;
    }

    @ManyToMany
    @JoinTable(name = "IdentityGroup_Identity", joinColumns = @JoinColumn(name = "group_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "identity_id", referencedColumnName = "id"))
    public Collection<Identity> getMembers() {
        return members;
    }

    public void setMembers(Collection<Identity> members) {
        this.members = members;
    }

}
