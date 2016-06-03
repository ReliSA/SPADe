package cz.zcu.kiv.spade.indao.jpa;

import cz.zcu.kiv.spade.domain.ToolInstance;
import cz.zcu.kiv.spade.indao.ToolInstanceDAO;

import javax.persistence.EntityManager;

public class ToolInstanceDAO_JPA extends GenericDAO_JPA<ToolInstance> implements ToolInstanceDAO {

    public ToolInstanceDAO_JPA(EntityManager em) {
        super(em, ToolInstance.class);
    }

    public ToolInstance save(ToolInstance ti) {
        entityManager.getTransaction().begin();

        ToolInstance ret;
        if (ti.getId() == 0) {
            entityManager.persist(ti);
            ret = ti;
        } else {
            ret = entityManager.merge(ti);
        }

        entityManager.getTransaction().commit();

        return ret;
    }
}