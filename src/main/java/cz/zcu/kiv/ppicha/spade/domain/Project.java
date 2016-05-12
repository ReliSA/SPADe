package cz.zcu.kiv.ppicha.spade.domain;

import cz.zcu.kiv.ppicha.spade.domain.abstracts.ProjectSegment;

import javax.persistence.*;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
public class Project extends ProjectSegment {

    private Set<ToolProjectInstance> representations;
    private Set<WorkUnit> workUnits;
    private Set<Activity> activities;
    private Set<Project> subprojects;
    private Set<Role> roles;
    private Set<Person> personnel;
    private Set<Phase> phases;
    private Set<Iteration> iterations;

    public Project() {
        this.representations = new LinkedHashSet<>();
        this.workUnits = new LinkedHashSet<>();
        this.activities = new LinkedHashSet<>();
        this.subprojects = new LinkedHashSet<>();
        this.roles = new LinkedHashSet<>();
        this.personnel = new LinkedHashSet<>();
        this.phases = new LinkedHashSet<>();
        this.iterations = new LinkedHashSet<>();
    }

    public Project(long id, String externalId, String name, String description, Date startDate, Date endDate,
                   Set<ToolProjectInstance> representations, Set<WorkUnit> workUnits, Set<Activity> activities, Set<Project> subprojects,
                   Set<Role> roles, Set<Person> personnel, Set<Phase> phases,
                   Set<Iteration> iterations) {
        super(id, externalId, name, description, startDate, endDate);
        this.representations = representations;
        this.workUnits = workUnits;
        this.activities = activities;
        this.subprojects = subprojects;
        this.roles = roles;
        this.personnel = personnel;
        this.phases = phases;
        this.iterations = iterations;
    }

    @OneToMany
    @JoinTable(name = "Project_Representation", joinColumns = @JoinColumn(name = "project_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "instance_id", referencedColumnName = "id"))
    public Set<ToolProjectInstance> getRepresentations() {
        return representations;
    }

    public void setRepresentations(Set<ToolProjectInstance> representations) {
        this.representations = representations;
    }

    @OneToMany
    @JoinTable(name = "Project_WorkUnit", joinColumns = @JoinColumn(name = "project_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "work_unit_id", referencedColumnName = "id"))
    public Set<WorkUnit> getWorkUnits() {
        return this.workUnits;
    }

    public void setWorkUnits(Set<WorkUnit> workUnits) {
        this.workUnits = workUnits;
    }

    @OneToMany
    @JoinTable(name = "Project_Activity", joinColumns = @JoinColumn(name = "project_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "activity_id", referencedColumnName = "id"))
    public Set<Activity> getActivities() {
        return activities;
    }

    public void setActivities(Set<Activity> activities) {
        this.activities = activities;
    }

    @OneToMany
    @JoinTable(name = "Project_Subproject", joinColumns = @JoinColumn(name = "project_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "subproject_id", referencedColumnName = "id"))
    public Set<Project> getSubprojects() {
        return subprojects;
    }

    public void setSubprojects(Set<Project> subprojects) {
        this.subprojects = subprojects;
    }

    @ManyToMany
    @JoinTable(name = "Project_Role", joinColumns = @JoinColumn(name = "project_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    @ManyToMany
    @JoinTable(name = "Project_Person", joinColumns = @JoinColumn(name = "project_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "member_id", referencedColumnName = "id"))
    public Set<Person> getPersonnel() {
        return personnel;
    }

    public void setPersonnel(Set<Person> personnel) {
        this.personnel = personnel;
    }

    @OneToMany
    @JoinTable(name = "Project_Phase", joinColumns = @JoinColumn(name = "project_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "phase_id", referencedColumnName = "id"))
    public Set<Phase> getPhases() {
        return phases;
    }

    public void setPhases(Set<Phase> phases) {
        this.phases = phases;
    }

    @OneToMany
    @JoinTable(name = "Project_Iteration", joinColumns = @JoinColumn(name = "project_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "iteration_id", referencedColumnName = "id"))
    public Set<Iteration> getIterations() {
        return iterations;
    }

    public void setIterations(Set<Iteration> iterations) {
        this.iterations = iterations;
    }

}
