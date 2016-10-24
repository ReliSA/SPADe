package cz.zcu.kiv.spade.domain;

import cz.zcu.kiv.spade.domain.abstracts.DescribedEntity;

import javax.persistence.*;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
public class IdentityGroup extends DescribedEntity {

    private Collection<Person> members;

    public IdentityGroup() {
        this.members = new LinkedHashSet<>();
    }

    public IdentityGroup(long id, String externalId, String name, String description, Set<Person> members) {
        super(id, externalId, name, description);
        this.members = members;
    }

    @ManyToMany
    @JoinTable(name = "Group_Person", joinColumns = @JoinColumn(name = "group_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "person_id", referencedColumnName = "id"))
    public Collection<Person> getMembers() {
        return members;
    }

    public void setMembers(Collection<Person> members) {
        this.members = members;
    }

}
