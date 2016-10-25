package cz.zcu.kiv.spade.indao.jpa;

import cz.zcu.kiv.spade.domain.WorkUnitPriority;
import cz.zcu.kiv.spade.indao.WorkUnitPriorityDAO;

import javax.persistence.EntityManager;

public class WorkUnitPriorityDAO_JPA extends GenericDAO_JPA<WorkUnitPriority> implements WorkUnitPriorityDAO {

    public WorkUnitPriorityDAO_JPA(EntityManager em) {
        super(em, WorkUnitPriority.class);
    }

    public WorkUnitPriority save(WorkUnitPriority priority) {
        entityManager.getTransaction().begin();

        WorkUnitPriority ret;
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