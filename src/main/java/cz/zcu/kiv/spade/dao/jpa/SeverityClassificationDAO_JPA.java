package cz.zcu.kiv.spade.dao.jpa;

import cz.zcu.kiv.spade.dao.SeverityClassificationDAO;
import cz.zcu.kiv.spade.domain.SeverityClassification;
import cz.zcu.kiv.spade.domain.enums.SeverityClass;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

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

    @Override
    public SeverityClassification findByClass(SeverityClass aClass) {
        TypedQuery<SeverityClassification> q = entityManager.createQuery(
                "SELECT c FROM SeverityClassification c WHERE c.aClass = :class", SeverityClassification.class);
        q.setParameter("class", aClass);
        try {
            return q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
}
