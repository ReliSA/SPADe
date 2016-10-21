package cz.zcu.kiv.spade.domain;

import cz.zcu.kiv.spade.domain.abstracts.DescribedEntity;

import javax.persistence.*;
import java.util.Collection;
import java.util.LinkedHashSet;

@Entity
public class ToolProjectInstance extends DescribedEntity {

    private ToolInstance toolInstance;
    private Project project;
    private String url;
    private Collection<WorkUnitPriority> priorities;
    private Collection<WorkUnitSeverity> severities;
    private Collection<WorkUnitType> workUnitTypes;
    private Collection<WorkUnitStatus> statuses;
    private Collection<WorkUnitResolution> resolutions;
    private Collection<WorkUnitCategory> categories;
    private Collection<Identity> identities;
    private Collection<IdentityGroup> groups;
    private Collection<Role> roles;
    private Collection<Configuration> configurations;
    private Collection<Branch> branches;
    private Collection<VCSTag> tags;

    public ToolProjectInstance() {
        this.priorities = new LinkedHashSet<>();
        this.severities = new LinkedHashSet<>();
        this.workUnitTypes = new LinkedHashSet<>();
        this.statuses = new LinkedHashSet<>();
        this.resolutions = new LinkedHashSet<>();
        this.categories = new LinkedHashSet<>();
        this.identities = new LinkedHashSet<>();
        this.groups = new LinkedHashSet<>();
        this.roles = new LinkedHashSet<>();
        this.configurations = new LinkedHashSet<>();
        this.branches = new LinkedHashSet<>();
        this.tags = new LinkedHashSet<>();
    }

    public ToolProjectInstance(long id, String externalId, String name, String description, ToolInstance toolInstance, Project project, String url, Collection<WorkUnitPriority> priorities, Collection<WorkUnitSeverity> severities, Collection<WorkUnitType> workUnitTypes, Collection<WorkUnitStatus> statuses, Collection<WorkUnitResolution> resolutions, Collection<WorkUnitCategory> categories, Collection<Identity> identities, Collection<IdentityGroup> groups, Collection<Role> roles, Collection<Configuration> configurations, Collection<Branch> branches, Collection<VCSTag> tags) {
        super(id, externalId, name, description);
        this.toolInstance = toolInstance;
        this.project = project;
        this.url = url;
        this.priorities = priorities;
        this.severities = severities;
        this.workUnitTypes = workUnitTypes;
        this.statuses = statuses;
        this.resolutions = resolutions;
        this.categories = categories;
        this.identities = identities;
        this.groups = groups;
        this.roles = roles;
        this.configurations = configurations;
        this.branches = branches;
        this.tags = tags;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    public ToolInstance getToolInstance() {
        return toolInstance;
    }

    public void setToolInstance(ToolInstance toolInstance) {
        this.toolInstance = toolInstance;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    @Column(nullable = false)
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @ManyToMany
    @JoinTable(name = "ToolProjectInstance_Priority", joinColumns = @JoinColumn(name = "tpi_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "priority_id", referencedColumnName = "id"))
    public Collection<WorkUnitPriority> getPriorities() {
        return priorities;
    }

    public void setPriorities(Collection<WorkUnitPriority> priorities) {
        this.priorities = priorities;
    }

    @ManyToMany
    @JoinTable(name = "ToolProjectInstance_Severity", joinColumns = @JoinColumn(name = "tpi_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "severity_id", referencedColumnName = "id"))
    public Collection<WorkUnitSeverity> getSeverities() {
        return severities;
    }

    public void setSeverities(Collection<WorkUnitSeverity> severities) {
        this.severities = severities;
    }

    @ManyToMany
    @JoinTable(name = "ToolProjectInstance_WorkUnitType", joinColumns = @JoinColumn(name = "tpi_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "wut_id", referencedColumnName = "id"))
    public Collection<WorkUnitType> getWorkUnitTypes() {
        return workUnitTypes;
    }

    public void setWorkUnitTypes(Collection<WorkUnitType> workUnitTypes) {
        this.workUnitTypes = workUnitTypes;
    }

    @ManyToMany
    @JoinTable(name = "ToolProjectInstance_Category", joinColumns = @JoinColumn(name = "tpi_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "category_id", referencedColumnName = "id"))
    public Collection<WorkUnitCategory> getCategories() {
        return categories;
    }

    public void setCategories(Collection<WorkUnitCategory> categories) {
        this.categories = categories;
    }

    @ManyToMany
    @JoinTable(name = "ToolProjectInstance_Status", joinColumns = @JoinColumn(name = "tpi_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "status_id", referencedColumnName = "id"))
    public Collection<WorkUnitStatus> getStatuses() {
        return statuses;
    }

    public void setStatuses(Collection<WorkUnitStatus> statuses) {
        this.statuses = statuses;
    }

    @ManyToMany
    @JoinTable(name = "ToolProjectInstance_Resolution", joinColumns = @JoinColumn(name = "tpi_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "resolution_id", referencedColumnName = "id"))
    public Collection<WorkUnitResolution> getResolutions() {
        return resolutions;
    }

    public void setResolutions(Collection<WorkUnitResolution> resolutions) {
        this.resolutions = resolutions;
    }

    @ManyToMany
    @JoinTable(name = "ToolProjectInstance_Identity", joinColumns = @JoinColumn(name = "tpi_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "identity_id", referencedColumnName = "id"))
    public Collection<Identity> getIdentities() {
        return identities;
    }

    public void setIdentities(Collection<Identity> identities) {
        this.identities = identities;
    }

    @ManyToMany
    @JoinTable(name = "ToolProjectInstance_Group", joinColumns = @JoinColumn(name = "tpi_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "group_id", referencedColumnName = "id"))
    public Collection<IdentityGroup> getGroups() {
        return groups;
    }

    public void setGroups(Collection<IdentityGroup> groups) {
        this.groups = groups;
    }

    @ManyToMany
    @JoinTable(name = "ToolProjectInstance_Role", joinColumns = @JoinColumn(name = "tpi_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
    public Collection<Role> getRoles() {
        return roles;
    }

    public void setRoles(Collection<Role> roles) {
        this.roles = roles;
    }

    @ManyToMany
    @JoinTable(name = "ToolProjectInstance_Configuration", joinColumns = @JoinColumn(name = "tpi_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "configuration_id", referencedColumnName = "id"))
    public Collection<Configuration> getConfigurations() {
        return configurations;
    }

    public void setConfigurations(Collection<Configuration> configurations) {
        this.configurations = configurations;
    }

    @ManyToMany
    @JoinTable(name = "ToolProjectInstance_Branch", joinColumns = @JoinColumn(name = "tpi_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "branch_id", referencedColumnName = "id"))
    public Collection<Branch> getBranches() {
        return branches;
    }

    public void setBranches(Collection<Branch> branches) {
        this.branches = branches;
    }

    @OneToMany
    @JoinColumn(name = "tpi_id")
    public Collection<VCSTag> getTags() {
        return tags;
    }

    public void setTags(Collection<VCSTag> tags) {
        this.tags = tags;
    }
}
