package cz.zcu.kiv.ppicha.spade.indao.jpa;

import cz.zcu.kiv.ppicha.spade.domain.ToolInstance;
import cz.zcu.kiv.ppicha.spade.indao.ToolInstanceDAO;

import javax.persistence.EntityManager;

/**
 * Created by Petr on 21.1.2016.
 */
public class ToolInstanceDAO_JPA extends GenericDAO_JPA<ToolInstance> implements ToolInstanceDAO {

    public ToolInstanceDAO_JPA(EntityManager em){
        super(em);
    }

    public ToolInstance save(ToolInstance ti) {
        entityManager.getTransaction().begin();

        ToolInstance ret;
        if(ti.getId() == 0) {
            entityManager.persist(ti);
            ret = ti;
        } else {
            ret =  entityManager.merge(ti);
        }

        entityManager.getTransaction().commit();

        return ret;
    }
}