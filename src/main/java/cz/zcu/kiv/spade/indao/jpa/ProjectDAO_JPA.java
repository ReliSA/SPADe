package cz.zcu.kiv.spade.indao.jpa;

import cz.zcu.kiv.spade.domain.Project;
import cz.zcu.kiv.spade.indao.ProjectDAO;

import javax.persistence.EntityManager;

public class ProjectDAO_JPA extends GenericDAO_JPA<Project> implements ProjectDAO {

    public ProjectDAO_JPA(EntityManager em) {
        super(em, Project.class);
    }

    public Project save(Project project) {
        entityManager.getTransaction().begin();

        Project ret;
        if (project.getId() == 0) {
            entityManager.persist(project);
            ret = project;
        } else {
            ret = entityManager.merge(project);
        }

        entityManager.getTransaction().commit();

        return ret;
    }
}
