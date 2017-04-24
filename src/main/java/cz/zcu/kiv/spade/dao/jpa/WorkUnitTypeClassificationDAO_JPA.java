package cz.zcu.kiv.spade.dao.jpa;

import cz.zcu.kiv.spade.dao.WorkUnitTypeClassificationDAO;
import cz.zcu.kiv.spade.domain.WorkUnitTypeClassification;
import cz.zcu.kiv.spade.domain.enums.WorkUnitTypeClass;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

public class WorkUnitTypeClassificationDAO_JPA extends GenericDAO_JPA<WorkUnitTypeClassification> implements WorkUnitTypeClassificationDAO {

    public WorkUnitTypeClassificationDAO_JPA(EntityManager em) {
        super(em, WorkUnitTypeClassification.class);
    }

    public WorkUnitTypeClassification save(WorkUnitTypeClassification classification) {
        entityManager.getTransaction().begin();

        WorkUnitTypeClassification ret;
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
    public WorkUnitTypeClassification findByClass(WorkUnitTypeClass aClass) {
        TypedQuery<WorkUnitTypeClassification> q = entityManager.createQuery(
                "SELECT c FROM WorkUnitTypeClassification c WHERE c.aClass = :class", WorkUnitTypeClassification.class);
        q.setParameter("class", aClass);
        try {
            return q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
}
