package cz.zcu.kiv.spade.domain;

import cz.zcu.kiv.spade.domain.abstracts.DescribedEntity;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import java.util.Collection;
import java.util.LinkedHashSet;

@Entity
public class Identity extends DescribedEntity {

    private Collection<Role> roles;
    private String email;

    public Identity() {
        this.roles = new LinkedHashSet<>();
    }

    public Identity(long id, String externalId, String name, String description, Collection<Role> roles) {
        super(id, externalId, name, description);
        this.roles = roles;
    }

    @ManyToMany
    @JoinTable(name = "Identity_Role", joinColumns = @JoinColumn(name = "identity_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
    public Collection<Role> getRoles() {
        return roles;
    }

    public void setRoles(Collection<Role> roles) {
        this.roles = roles;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
