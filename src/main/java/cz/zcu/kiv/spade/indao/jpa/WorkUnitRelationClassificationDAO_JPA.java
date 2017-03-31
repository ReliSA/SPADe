package cz.zcu.kiv.spade.indao.jpa;

import cz.zcu.kiv.spade.domain.WorkUnitRelationClassification;
import cz.zcu.kiv.spade.indao.WorkUnitRelationClassificationDAO;

import javax.persistence.EntityManager;

public class WorkUnitRelationClassificationDAO_JPA extends GenericDAO_JPA<WorkUnitRelationClassification> implements WorkUnitRelationClassificationDAO {

    public WorkUnitRelationClassificationDAO_JPA(EntityManager em) {
        super(em, WorkUnitRelationClassification.class);
    }

    public WorkUnitRelationClassification save(WorkUnitRelationClassification classification) {
        entityManager.getTransaction().begin();

        WorkUnitRelationClassification ret;
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
