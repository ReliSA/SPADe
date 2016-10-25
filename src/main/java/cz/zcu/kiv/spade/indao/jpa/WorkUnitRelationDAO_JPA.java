package cz.zcu.kiv.spade.indao.jpa;

import cz.zcu.kiv.spade.domain.WorkUnitRelation;
import cz.zcu.kiv.spade.indao.WorkUnitRelationDAO;

import javax.persistence.EntityManager;

public class WorkUnitRelationDAO_JPA extends GenericDAO_JPA<WorkUnitRelation> implements WorkUnitRelationDAO {

    public WorkUnitRelationDAO_JPA(EntityManager em) {
        super(em, WorkUnitRelation.class);
    }

    public WorkUnitRelation save(WorkUnitRelation relation) {
        entityManager.getTransaction().begin();

        WorkUnitRelation ret;
        if (relation.getId() == 0) {
            entityManager.persist(relation);
            ret = relation;
        } else {
            ret = entityManager.merge(relation);
        }

        entityManager.getTransaction().commit();

        return ret;
    }
}