package cz.zcu.kiv.spade.indao.jpa;

import cz.zcu.kiv.spade.domain.WorkItemChange;
import cz.zcu.kiv.spade.indao.WorkItemChangeDAO;

import javax.persistence.EntityManager;

public class WorkItemChangeDAO_JPA extends GenericDAO_JPA<WorkItemChange> implements WorkItemChangeDAO {

    public WorkItemChangeDAO_JPA(EntityManager em) {
        super(em, WorkItemChange.class);
    }

    public WorkItemChange save(WorkItemChange wic) {
        entityManager.getTransaction().begin();

        WorkItemChange ret;
        if (wic.getId() == 0) {
            entityManager.persist(wic);
            ret = wic;
        } else {
            ret = entityManager.merge(wic);
        }

        entityManager.getTransaction().commit();

        return ret;
    }
}