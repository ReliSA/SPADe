package cz.zcu.kiv.ppicha.spade.domain;

import cz.zcu.kiv.ppicha.spade.domain.abstracts.DescribedEntity;

import javax.persistence.*;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
public class Identity extends DescribedEntity {

    private Set<Role> roles;
    private String email;
    private ToolProjectInstance toolProjectInstance;

    public Identity() {
        this.roles = new LinkedHashSet<>();
    }

    public Identity(long id, String externalId, String name, String description, Set<Role> roles, ToolProjectInstance toolProjectInstance) {
        super(id, externalId, name, description);
        this.roles = roles;
        this.toolProjectInstance = toolProjectInstance;
    }

    @ManyToMany
    @JoinTable(name = "Identity_Role", joinColumns = @JoinColumn(name = "identity_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
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

}
