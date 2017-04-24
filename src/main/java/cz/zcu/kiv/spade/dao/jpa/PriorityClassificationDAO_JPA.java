package cz.zcu.kiv.spade.dao.jpa;

import cz.zcu.kiv.spade.dao.PriorityClassificationDAO;
import cz.zcu.kiv.spade.domain.PriorityClassification;
import cz.zcu.kiv.spade.domain.enums.PriorityClass;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

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

    @Override
    public PriorityClassification findByClass(PriorityClass aClass) {
        TypedQuery<PriorityClassification> q = entityManager.createQuery(
                "SELECT c FROM PriorityClassification c WHERE c.aClass = :class", PriorityClassification.class);
        q.setParameter("class", aClass);
        try {
            return q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

}
