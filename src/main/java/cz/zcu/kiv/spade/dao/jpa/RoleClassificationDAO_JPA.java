package cz.zcu.kiv.spade.dao.jpa;

import cz.zcu.kiv.spade.dao.RoleClassificationDAO;
import cz.zcu.kiv.spade.domain.RoleClassification;
import cz.zcu.kiv.spade.domain.enums.RoleClass;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

public class RoleClassificationDAO_JPA extends GenericDAO_JPA<RoleClassification> implements RoleClassificationDAO {

    public RoleClassificationDAO_JPA(EntityManager em) {
        super(em, RoleClassification.class);
    }

    public RoleClassification save(RoleClassification classification) {
        entityManager.getTransaction().begin();

        RoleClassification ret;
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
    public RoleClassification findByClass(RoleClass aClass) {
        TypedQuery<RoleClassification> q = entityManager.createQuery(
                "SELECT c FROM RoleClassification c WHERE c.aClass = :class", RoleClassification.class);
        q.setParameter("class", aClass);
        try {
            return q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
}
