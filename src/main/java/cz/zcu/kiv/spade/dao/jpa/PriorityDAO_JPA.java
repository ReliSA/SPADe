package cz.zcu.kiv.spade.dao.jpa;

import cz.zcu.kiv.spade.dao.PriorityDAO;
import cz.zcu.kiv.spade.domain.Priority;

import javax.persistence.EntityManager;

public class PriorityDAO_JPA extends GenericDAO_JPA<Priority> implements PriorityDAO {

    public PriorityDAO_JPA(EntityManager em) {
        super(em, Priority.class);
    }

    public Priority save(Priority priority) {
        entityManager.getTransaction().begin();

        Priority ret;
        if (priority.getId() == 0) {
            entityManager.persist(priority);
            ret = priority;
        } else {
            ret = entityManager.merge(priority);
        }

        entityManager.getTransaction().commit();

        return ret;
    }
}
