package cz.zcu.kiv.spade.domain;

import cz.zcu.kiv.spade.domain.abstracts.BaseEntity;

import javax.persistence.*;

@Entity
@Table(name = "project_person_role")
public class ProjectPersonRole extends BaseEntity {

    private Project project;
    private Person person;
    private Role role;

    public ProjectPersonRole() {
        super();
    }

    @JoinColumn(name = "projectId")
    @ManyToOne(fetch = FetchType.LAZY)
    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    @JoinColumn(name = "personId")
    @ManyToOne(fetch = FetchType.LAZY)
    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    @JoinColumn(name = "roleId")
    @ManyToOne(fetch = FetchType.LAZY)
    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
