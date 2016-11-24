package cz.zcu.kiv.spade.domain;

import cz.zcu.kiv.spade.domain.abstracts.DescribedEntity;

import javax.persistence.*;
import java.util.Collection;
import java.util.LinkedHashSet;

@Entity
@Table(name = "people_group")
public class Group extends DescribedEntity {

    private Collection<Person> members;

    public Group() {
        super();
        this.members = new LinkedHashSet<>();
    }

    @ManyToMany
    @JoinTable(name = "group_member", joinColumns = @JoinColumn(name = "groupId", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "memberId", referencedColumnName = "id"))
    public Collection<Person> getMembers() {
        return members;
    }

    public void setMembers(Collection<Person> members) {
        this.members = members;
    }

}
