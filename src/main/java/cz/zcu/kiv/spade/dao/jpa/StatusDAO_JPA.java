package cz.zcu.kiv.spade.dao.jpa;

import cz.zcu.kiv.spade.dao.StatusDAO;
import cz.zcu.kiv.spade.domain.Status;

import javax.persistence.EntityManager;

public class StatusDAO_JPA extends GenericDAO_JPA<Status> implements StatusDAO {

    public StatusDAO_JPA(EntityManager em) {
        super(em, Status.class);
    }

    public Status save(Status status) {
        entityManager.getTransaction().begin();

        Status ret;
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
