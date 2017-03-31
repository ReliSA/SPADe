package cz.zcu.kiv.spade.indao.jpa;

import cz.zcu.kiv.spade.domain.SeverityClassification;
import cz.zcu.kiv.spade.indao.SeverityClassificationDAO;

import javax.persistence.EntityManager;

public class SeverityClassificationDAO_JPA extends GenericDAO_JPA<SeverityClassification> implements SeverityClassificationDAO {

    public SeverityClassificationDAO_JPA(EntityManager em) {
        super(em, SeverityClassification.class);
    }

    public SeverityClassification save(SeverityClassification classification) {
        entityManager.getTransaction().begin();

        SeverityClassification ret;
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
