package cz.zcu.kiv.spade.indao.jpa;

import cz.zcu.kiv.spade.domain.WorkUnitTypeClassification;
import cz.zcu.kiv.spade.indao.WorkUnitTypeClassificationDAO;

import javax.persistence.EntityManager;

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
}
