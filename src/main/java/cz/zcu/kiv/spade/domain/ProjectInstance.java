package cz.zcu.kiv.spade.domain;

import cz.zcu.kiv.spade.domain.abstracts.DescribedEntity;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "project_instance")
public class ProjectInstance extends DescribedEntity {

    private ToolInstance toolInstance;
    private Project project;
    private String url;
    private Collection<Priority> priorities;
    private Collection<Severity> severities;
    private Collection<Status> statuses;
    private Collection<Resolution> resolutions;
    private Collection<WorkUnitType> wuTypes;
    private Collection<Role> roles;

    public ProjectInstance() {
        super();
        this.priorities = Defaults.getDefaultPriorities();
        this.severities = Defaults.getDefaultSeverities();
        this.statuses = Defaults.getDeafultStatuses();
        this.resolutions = Defaults.getDefaultResolutions();
        this.wuTypes = Defaults.getDefaultWUTypes();
        this.roles = Defaults.getDefaultRoles();
    }

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "toolInstanceId")
    public ToolInstance getToolInstance() {
        return toolInstance;
    }

    public void setToolInstance(ToolInstance toolInstance) {
        this.toolInstance = toolInstance;
    }

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "projectId")
    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "projectInstanceId")
    public Collection<Priority> getPriorities() {
        return priorities;
    }

    public void setPriorities(Collection<Priority> priorities) {
        this.priorities = priorities;
    }

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "projectInstanceId")
    public Collection<Severity> getSeverities() {
        return severities;
    }

    public void setSeverities(Collection<Severity> severities) {
        this.severities = severities;
    }

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "projectInstanceId")
    public Collection<Status> getStatuses() {
        return statuses;
    }


    public void setStatuses(Collection<Status> statuses) {
        this.statuses = statuses;
    }

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "projectInstanceId")
    public Collection<Resolution> getResolutions() {
        return resolutions;
    }

    public void setResolutions(Collection<Resolution> resolutions) {
        this.resolutions = resolutions;
    }

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "projectInstanceId")
    public Collection<WorkUnitType> getWuTypes() {
        return wuTypes;
    }

    public void setWuTypes(Collection<WorkUnitType> wuTypes) {
        this.wuTypes = wuTypes;
    }

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "projectInstanceId")
    public Collection<Role> getRoles() {
        return roles;
    }

    public void setRoles(Collection<Role> roles) {
        this.roles = roles;
    }

}
