package cz.zcu.kiv.spade.dao.jpa;

import cz.zcu.kiv.spade.dao.StatusClassificationDAO;
import cz.zcu.kiv.spade.domain.StatusClassification;
import cz.zcu.kiv.spade.domain.enums.StatusClass;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

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

    @Override
    public StatusClassification findByClass(StatusClass aClass) {
        TypedQuery<StatusClassification> q = entityManager.createQuery(
                "SELECT c FROM StatusClassification c WHERE c.aClass = :class", StatusClassification.class);
        q.setParameter("class", aClass);
        try {
            return q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
}
