package cz.zcu.kiv.spade.indao.jpa;

import cz.zcu.kiv.spade.domain.ResolutionClassification;
import cz.zcu.kiv.spade.indao.ResolutionClassificationDAO;

import javax.persistence.EntityManager;

public class ResolutionClassificationDAO_JPA extends GenericDAO_JPA<ResolutionClassification> implements ResolutionClassificationDAO {

    public ResolutionClassificationDAO_JPA(EntityManager em) {
        super(em, ResolutionClassification.class);
    }

    public ResolutionClassification save(ResolutionClassification classification) {
        entityManager.getTransaction().begin();

        ResolutionClassification ret;
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
