package cz.zcu.kiv.ppicha.spade.domain;

import cz.zcu.kiv.ppicha.spade.domain.abstracts.TemporalNamedAndDescribedEntity;

import javax.persistence.*;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
public class Project extends TemporalNamedAndDescribedEntity {

    private Set<WorkUnit> workUnits;
    private Set<Activity> activities;
    private Set<Project> subprojects;
    private Set<Project> relatedProjects;
    private Set<Role> roles;
    private Set<Person> personnel;
    private Set<Phase> phases;
    private Set<Iteration> iterations;

    public Project() {
        this.workUnits = new LinkedHashSet<>();
        this.activities = new LinkedHashSet<>();
        this.subprojects = new LinkedHashSet<>();
        this.relatedProjects = new LinkedHashSet<>();
        this.roles = new LinkedHashSet<>();
        this.personnel = new LinkedHashSet<>();
        this.phases = new LinkedHashSet<>();
        this.iterations = new LinkedHashSet<>();
    }

    public Project(long id, long externalId, String name, String description, Date startDate, Date endDate,
                   Set<WorkUnit> workUnits, Set<Activity> activities, Set<Project> subprojects,
                   Set<Project> relatedProjects, Set<Role> roles, Set<Person> personnel, Set<Phase> phases,
                   Set<Iteration> iterations) {
        super(id, externalId, name, description, startDate, endDate);
        this.workUnits = workUnits;
        this.activities = activities;
        this.subprojects = subprojects;
        this.relatedProjects = relatedProjects;
        this.roles = roles;
        this.personnel = personnel;
        this.phases = phases;
        this.iterations = iterations;
    }

    @OneToMany
    public Set<WorkUnit> getWorkUnits() {
        return this.workUnits;
    }

    public void setWorkUnits(Set<WorkUnit> workUnits) {
        this.workUnits = workUnits;
    }

    @OneToMany
    public Set<Activity> getActivities() {
        return activities;
    }

    public void setActivities(Set<Activity> activities) {
        this.activities = activities;
    }

    @OneToMany
    public Set<Project> getSubprojects() {
        return subprojects;
    }

    public void setSubprojects(Set<Project> subprojects) {
        this.subprojects = subprojects;
    }

    @ManyToMany
    @JoinTable(name = "Project_Related", joinColumns = @JoinColumn(name = "project", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "related", referencedColumnName = "id"))
    public Set<Project> getRelatedProjects() {
        return relatedProjects;
    }

    public void setRelatedProjects(Set<Project> relatedProjects) {
        this.relatedProjects = relatedProjects;
    }

    @ManyToMany
    @JoinTable(name = "Project_Role", joinColumns = @JoinColumn(name = "project", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role", referencedColumnName = "id"))
    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    @ManyToMany
    @JoinTable(name = "Project_Person", joinColumns = @JoinColumn(name = "project", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "member", referencedColumnName = "id"))
    public Set<Person> getPersonnel() {
        return personnel;
    }

    public void setPersonnel(Set<Person> personnel) {
        this.personnel = personnel;
    }

    @OneToMany
    public Set<Phase> getPhases() {
        return phases;
    }

    public void setPhases(Set<Phase> phases) {
        this.phases = phases;
    }

    @OneToMany
    public Set<Iteration> getIterations() {
        return iterations;
    }

    public void setIterations(Set<Iteration> iterations) {
        this.iterations = iterations;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Project project = (Project) o;

        if (workUnits != null ? !workUnits.equals(project.workUnits) : project.workUnits != null) return false;
        if (activities != null ? !activities.equals(project.activities) : project.activities != null) return false;
        if (subprojects != null ? !subprojects.equals(project.subprojects) : project.subprojects != null) return false;
        if (relatedProjects != null ? !relatedProjects.equals(project.relatedProjects) : project.relatedProjects != null)
            return false;
        if (roles != null ? !roles.equals(project.roles) : project.roles != null) return false;
        if (personnel != null ? !personnel.equals(project.personnel) : project.personnel != null) return false;
        if (phases != null ? !phases.equals(project.phases) : project.phases != null) return false;
        return !(iterations != null ? !iterations.equals(project.iterations) : project.iterations != null);

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (workUnits != null ? workUnits.hashCode() : 0);
        result = 31 * result + (activities != null ? activities.hashCode() : 0);
        result = 31 * result + (subprojects != null ? subprojects.hashCode() : 0);
        result = 31 * result + (relatedProjects != null ? relatedProjects.hashCode() : 0);
        result = 31 * result + (roles != null ? roles.hashCode() : 0);
        result = 31 * result + (personnel != null ? personnel.hashCode() : 0);
        result = 31 * result + (phases != null ? phases.hashCode() : 0);
        result = 31 * result + (iterations != null ? iterations.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Project{" +
                "workUnits=" + workUnits +
                ", activities=" + activities +
                ", subprojects=" + subprojects +
                ", relatedProjects=" + relatedProjects +
                ", roles=" + roles +
                ", personnel=" + personnel +
                ", phases=" + phases +
                ", iterations=" + iterations +
                '}';
    }
}
