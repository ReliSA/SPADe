package cz.zcu.kiv.spade.indao.jpa;

import cz.zcu.kiv.spade.domain.PriorityClassification;
import cz.zcu.kiv.spade.indao.PriorityClassificationDAO;

import javax.persistence.EntityManager;

public class PriorityClassificationDAO_JPA extends GenericDAO_JPA<PriorityClassification> implements PriorityClassificationDAO {

    public PriorityClassificationDAO_JPA(EntityManager em) {
        super(em, PriorityClassification.class);
    }

    public PriorityClassification save(PriorityClassification classification) {
        entityManager.getTransaction().begin();

        PriorityClassification ret;
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
