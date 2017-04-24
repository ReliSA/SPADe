package cz.zcu.kiv.spade.dao.jpa;

import cz.zcu.kiv.spade.dao.ResolutionClassificationDAO;
import cz.zcu.kiv.spade.domain.ResolutionClassification;
import cz.zcu.kiv.spade.domain.enums.ResolutionClass;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

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

    @Override
    public ResolutionClassification findByClass(ResolutionClass aClass) {
        TypedQuery<ResolutionClassification> q = entityManager.createQuery(
                "SELECT c FROM ResolutionClassification c WHERE c.aClass = :class", ResolutionClassification.class);
        q.setParameter("class", aClass);
        try {
            return q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
}
