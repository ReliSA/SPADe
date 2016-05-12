package cz.zcu.kiv.ppicha.spade.indao.jpa;

import cz.zcu.kiv.ppicha.spade.domain.WorkUnit;
import cz.zcu.kiv.ppicha.spade.indao.WorkUnitDAO;

import javax.persistence.EntityManager;

public class WorkUnitDAO_JPA extends GenericDAO_JPA<WorkUnit> implements WorkUnitDAO {

    public WorkUnitDAO_JPA(EntityManager em){
        super(em, WorkUnit.class);
    }

    public WorkUnit save(WorkUnit wu) {
        entityManager.getTransaction().begin();

        WorkUnit ret;
        if(wu.getId() == 0) {
            entityManager.persist(wu);
            ret = wu;
        } else {
            ret =  entityManager.merge(wu);
        }

        entityManager.getTransaction().commit();

        return ret;
    }
}