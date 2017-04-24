package cz.zcu.kiv.spade.dao.jpa;

import cz.zcu.kiv.spade.dao.WorkItemRelationDAO;
import cz.zcu.kiv.spade.domain.WorkItemRelation;

import javax.persistence.EntityManager;

public class WorkItemRelationDAO_JPA extends GenericDAO_JPA<WorkItemRelation> implements WorkItemRelationDAO {

    public WorkItemRelationDAO_JPA(EntityManager em) {
        super(em, WorkItemRelation.class);
    }

    public WorkItemRelation save(WorkItemRelation relation) {
        entityManager.getTransaction().begin();

        WorkItemRelation ret;
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
