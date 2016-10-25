package cz.zcu.kiv.spade.indao.jpa;

import cz.zcu.kiv.spade.domain.WorkUnitStatus;
import cz.zcu.kiv.spade.indao.WorkUnitStatusDAO;

import javax.persistence.EntityManager;

public class WorkUnitStatusDAO_JPA extends GenericDAO_JPA<WorkUnitStatus> implements WorkUnitStatusDAO {

    public WorkUnitStatusDAO_JPA(EntityManager em) {
        super(em, WorkUnitStatus.class);
    }

    public WorkUnitStatus save(WorkUnitStatus status) {
        entityManager.getTransaction().begin();

        WorkUnitStatus ret;
        if (status.getId() == 0) {
            entityManager.persist(status);
            ret = status;
        } else {
            ret = entityManager.merge(status);
        }

        entityManager.getTransaction().commit();

        return ret;
    }
}