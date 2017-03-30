package cz.zcu.kiv.spade.domain;

import cz.zcu.kiv.spade.domain.abstracts.DescribedEntity;
import cz.zcu.kiv.spade.domain.enums.*;

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
        this.priorities = new LinkedHashSet<>();
        this.severities = new LinkedHashSet<>();
        this.statuses = new LinkedHashSet<>();
        this.resolutions = new LinkedHashSet<>();
        this.wuTypes = new LinkedHashSet<>();
        this.roles = new LinkedHashSet<>();
        setDefaultEnumValues();
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "toolInstanceId")
    public ToolInstance getToolInstance() {
        return toolInstance;
    }

    public void setToolInstance(ToolInstance toolInstance) {
        this.toolInstance = toolInstance;
    }

    @ManyToOne(fetch = FetchType.LAZY)
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

    @OneToMany
    @JoinColumn(name = "projectInstanceId")
    public Collection<Priority> getPriorities() {
        return priorities;
    }

    public void setPriorities(Collection<Priority> priorities) {
        this.priorities = priorities;
    }

    @OneToMany
    @JoinColumn(name = "projectInstanceId")
    public Collection<Severity> getSeverities() {
        return severities;
    }

    public void setSeverities(Collection<Severity> severities) {
        this.severities = severities;
    }

    @OneToMany
    @JoinColumn(name = "projectInstanceId")
    public Collection<Status> getStatuses() {
        return statuses;
    }


    public void setStatuses(Collection<Status> statuses) {
        this.statuses = statuses;
    }

    @OneToMany
    @JoinColumn(name = "projectInstanceId")
    public Collection<Resolution> getResolutions() {
        return resolutions;
    }

    public void setResolutions(Collection<Resolution> resolutions) {
        this.resolutions = resolutions;
    }

    @OneToMany
    @JoinColumn(name = "projectInstanceId")
    public Collection<WorkUnitType> getWuTypes() {
        return wuTypes;
    }

    public void setWuTypes(Collection<WorkUnitType> wuTypes) {
        this.wuTypes = wuTypes;
    }

    @OneToMany
    @JoinColumn(name = "projectInstanceId")
    public Collection<Role> getRoles() {
        return roles;
    }

    public void setRoles(Collection<Role> roles) {
        this.roles = roles;
    }

    @Transient
    private void setDefaultEnumValues() {
        setDefaultWUTypes();
        setDefaultResolutions();
        setDefaultSeverities();
        setDefaultPriorities();
        setDeafultStatuses();
        setDefaultRoles();
    }

    @Transient
    private void setDefaultRoles() {
        RoleClass role;

        role = RoleClass.ANALYST;
        addRole(role, "analyst");
        addRole(role, "bussinessanalyst");

        role = RoleClass.DESIGNER;
        addRole(role, "designer");
        addRole(role, "architect");
        addRole(role, "systemarchitect");
        addRole(role, "uxdesigner");

        role = RoleClass.DEVELOPER;
        addRole(role, "developer");

        role = RoleClass.DOCUMENTER;
        addRole(role, "documenter");

        role = RoleClass.MENTOR;
        addRole(role, "mentor");
        addRole(role, "scrummaster");

        role = RoleClass.NON_MEMBER;
        addRole(role, "nonmember");
        addRole(role, "anonymous");

        role = RoleClass.PROJECT_MANAGER;
        addRole(role, "projectmanager");
        addRole(role, "manager");
        addRole(role, "owner");
        addRole(role, "productmanager");
        addRole(role, "projectlead");
        addRole(role, "projectleader");
        addRole(role, "administrator");
        addRole(role, "tempoprojectmanager");
    }

    private void addRole(RoleClass aClass, String keyword) {
        Role role = new Role();
        role.setName(keyword);
        role.setAClass(aClass);
        this.roles.add(role);
    }

    @Transient
    private void setDeafultStatuses() {
        StatusClass status;

        status = StatusClass.NEW;
        addStatus(status, "new");
        addStatus(status, "todo");
        addStatus(status, "unconfirmed");
        addStatus(status, "funnel");
        addStatus(status, "analysis");
        addStatus(status, "open");

        status = StatusClass.ACCEPTED;
        addStatus(status, "accepted");
        addStatus(status, "assigned");
        addStatus(status, "backlog");

        status = StatusClass.IN_PROGRESS;
        addStatus(status, "inprogress");

        status = StatusClass.RESOLVED;
        addStatus(status, "resolved");
        addStatus(status, "test");

        status = StatusClass.VERIFIED;
        addStatus(status, "verified");
        addStatus(status, "feedback");

        status = StatusClass.DONE;
        addStatus(status, "done");
        addStatus(status, "closed");
        addStatus(status, "fixed");
        addStatus(status, "approved");

        status = StatusClass.INVALID;
        addStatus(status, "invalid");
        addStatus(status, "cancelled");
        addStatus(status, "rejected");
    }

    private void addStatus(StatusClass aClass, String keyword) {
        Status status = new Status();
        status.setName(keyword);
        status.setAClass(aClass);
        this.statuses.add(status);
    }

    @Transient
    private void setDefaultPriorities() {
        PriorityClass priority;

        priority = PriorityClass.LOWEST;
        addPriority(priority, "lowest");

        priority = PriorityClass.LOW;
        addPriority(priority, "low");

        priority = PriorityClass.NORMAL;
        addPriority(priority, "normal");
        addPriority(priority, "medium");
        addPriority(priority, "standard");
        addPriority(priority, "moderate");
        addPriority(priority, "common");

        priority = PriorityClass.HIGH;
        addPriority(priority, "high");

        priority = PriorityClass.HIGHEST;
        addPriority(priority, "highest");
        addPriority(priority, "immediate");
        addPriority(priority, "urgent");
    }

    private void addPriority(PriorityClass aClass, String keyword) {
        Priority priority = new Priority();
        priority.setName(keyword);
        priority.setAClass(aClass);
        this.priorities.add(priority);
    }

    @Transient
    private void setDefaultSeverities() {
        SeverityClass severity;

        severity = SeverityClass.TRIVIAL;
        addSeverity(severity, "trivial");

        severity = SeverityClass.MINOR;
        addSeverity(severity, "minor");
        addSeverity(severity, "small");

        severity = SeverityClass.NORMAL;
        addSeverity(severity, "normal");
        addSeverity(severity, "moderate");
        addSeverity(severity, "common");
        addSeverity(severity, "standard");
        addSeverity(severity, "medium");

        severity = SeverityClass.MAJOR;
        addSeverity(severity, "major");
        addSeverity(severity, "big");

        severity = SeverityClass.CRITICAL;
        addSeverity(severity, "critical");
        addSeverity(severity, "blocker");
    }

    private void addSeverity(SeverityClass aClass, String keyword) {
        Severity severity = new Severity();
        severity.setName(keyword);
        severity.setAClass(aClass);
        this.severities.add(severity);
    }

    @Transient
    private void setDefaultResolutions() {
        ResolutionClass resolution;

        resolution = ResolutionClass.INVALID;
        addResolution(resolution, "invalid");

        resolution = ResolutionClass.DUPLICATE;
        addResolution(resolution, "duplicate");

        resolution = ResolutionClass.WONT_FIX;
        addResolution(resolution, "wontfix");
        addResolution(resolution, "wontdo");

        resolution = ResolutionClass.FIXED;
        addResolution(resolution, "fixed");
        addResolution(resolution, "done");
        addResolution(resolution, "fixedupstream");

        resolution = ResolutionClass.WORKS_AS_DESIGNED;
        addResolution(resolution, "worksasdesigned");

        resolution = ResolutionClass.FINISHED;
        addResolution(resolution, "finished");

        resolution = ResolutionClass.WORKS_FOR_ME;
        addResolution(resolution, "worksforme");

        resolution = ResolutionClass.INCOMPLETE;
        addResolution(resolution, "incomplete");
        addResolution(resolution, "cannotreproduce");

        resolution = ResolutionClass.UNFINISHED;
        addResolution(resolution, "unfinished");
    }

    private void addResolution(ResolutionClass aClass, String keyword) {
        Resolution resolution = new Resolution();
        resolution.setName(keyword);
        resolution.setAClass(aClass);
        this.resolutions.add(resolution);
    }

    @Transient
    private void setDefaultWUTypes() {
        WorkUnitTypeClass type;

        type = WorkUnitTypeClass.BUG;
        addWUType(type, "bug");
        addWUType(type, "defect");

        type = WorkUnitTypeClass.TASK;
        addWUType(type, "task");

        type = WorkUnitTypeClass.ENHANCEMENT;
        addWUType(type, "enhancement");
        addWUType(type, "improvement");

        type = WorkUnitTypeClass.ENHANCEMENT;
        addWUType(type, "feature");
        addWUType(type, "newfeature");
    }

    private void addWUType(WorkUnitTypeClass aClass, String keyword) {
        WorkUnitType wuType = new WorkUnitType();
        wuType.setName(keyword);
        wuType.setAClass(aClass);
        this.wuTypes.add(wuType);
    }
}
