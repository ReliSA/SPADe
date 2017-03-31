package cz.zcu.kiv.spade.indao.jpa;

import cz.zcu.kiv.spade.domain.ProjectInstance;
import cz.zcu.kiv.spade.indao.ProjectInstanceDAO;

import javax.persistence.EntityManager;

public class ProjectInstanceDAO_JPA extends GenericDAO_JPA<ProjectInstance> implements ProjectInstanceDAO {

    public ProjectInstanceDAO_JPA(EntityManager em) {
        super(em, ProjectInstance.class);
    }

    public ProjectInstance save(ProjectInstance pi) {
        entityManager.getTransaction().begin();

        ProjectInstance ret;
        if (pi.getId() == 0) {
            entityManager.persist(pi);
            ret = pi;
        } else {
            ret = entityManager.merge(pi);
        }

        entityManager.getTransaction().commit();

        return ret;
    }
}
