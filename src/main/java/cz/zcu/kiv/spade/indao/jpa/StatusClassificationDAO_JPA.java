package cz.zcu.kiv.spade.indao.jpa;

import cz.zcu.kiv.spade.domain.StatusClassification;
import cz.zcu.kiv.spade.indao.StatusClassificationDAO;

import javax.persistence.EntityManager;

public class StatusClassificationDAO_JPA extends GenericDAO_JPA<StatusClassification> implements StatusClassificationDAO {

    public StatusClassificationDAO_JPA(EntityManager em) {
        super(em, StatusClassification.class);
    }

    public StatusClassification save(StatusClassification classification) {
        entityManager.getTransaction().begin();

        StatusClassification ret;
        if (classification.getId() == 0) {
            entityManager.persist(classification);
            ret = classification;
        } else {
            ret = entityManager.merge(classification);
        }

        entityManager.getTransaction().commit();

        return ret;
    }
}
