package cz.zcu.kiv.ppicha.spade.domain;

import cz.zcu.kiv.ppicha.spade.domain.abstracts.NamedEntity;

import javax.persistence.*;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
public class Identity extends NamedEntity {

    private Set<Role> roles;
    private String email;
    private ToolProjectInstance toolProjectInstance;

    public Identity() {
        this.roles = new LinkedHashSet<>();
    }

    public Identity(long id, long externalId, String name, Set<Role> roles, ToolProjectInstance toolProjectInstance) {
        super(id, externalId, name);
        this.roles = roles;
        this.toolProjectInstance = toolProjectInstance;
    }

    @ManyToMany
    @JoinTable(name = "Identity_Role", joinColumns = @JoinColumn(name = "identity", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role", referencedColumnName = "id"))
    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    public ToolProjectInstance getToolProjectInstance() {
        return toolProjectInstance;
    }

    public void setToolProjectInstance(ToolProjectInstance toolProjectInstance) {
        this.toolProjectInstance = toolProjectInstance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Identity identity = (Identity) o;

        if (roles != null ? !roles.equals(identity.roles) : identity.roles != null) return false;
        if (email != null ? !email.equals(identity.email) : identity.email != null) return false;
        return toolProjectInstance != null ? toolProjectInstance.equals(identity.toolProjectInstance) : identity.toolProjectInstance == null;

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (roles != null ? roles.hashCode() : 0);
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (toolProjectInstance != null ? toolProjectInstance.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Identity{" +
                "roles=" + roles +
                ", email='" + email + '\'' +
                ", toolProjectInstance=" + toolProjectInstance +
                '}';
    }
}
