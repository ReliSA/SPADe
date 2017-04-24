package cz.zcu.kiv.spade.dao.jpa;

import cz.zcu.kiv.spade.dao.RelatedPairDAO;
import cz.zcu.kiv.spade.domain.WorkItemRelation;

import javax.persistence.EntityManager;

public class RelatedPairDAO_JPA extends GenericDAO_JPA<WorkItemRelation> implements RelatedPairDAO {

    public RelatedPairDAO_JPA(EntityManager em) {
        super(em, WorkItemRelation.class);
    }

    public WorkItemRelation save(WorkItemRelation phase) {
        entityManager.getTransaction().begin();

        WorkItemRelation ret;
        if (phase.getId() == 0) {
            entityManager.persist(phase);
            ret = phase;
        } else {
            ret = entityManager.merge(phase);
        }

        entityManager.getTransaction().commit();

        return ret;
    }
}