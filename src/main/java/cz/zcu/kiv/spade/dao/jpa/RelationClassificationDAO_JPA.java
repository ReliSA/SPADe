package cz.zcu.kiv.spade.dao.jpa;

import cz.zcu.kiv.spade.dao.RelationClassificationDAO;
import cz.zcu.kiv.spade.domain.RelationClassification;
import cz.zcu.kiv.spade.domain.enums.RelationClass;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

public class RelationClassificationDAO_JPA extends GenericDAO_JPA<RelationClassification> implements RelationClassificationDAO {

    public RelationClassificationDAO_JPA(EntityManager em) {
        super(em, RelationClassification.class);
    }

    public RelationClassification save(RelationClassification classification) {
        entityManager.getTransaction().begin();

        RelationClassification ret;
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
    public RelationClassification findByClass(RelationClass aClass) {
        TypedQuery<RelationClassification> q = entityManager.createQuery(
                "SELECT c FROM RelationClassification c WHERE c.aClass = :class", RelationClassification.class);
        q.setParameter("class", aClass);
        try {
            return q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
}
