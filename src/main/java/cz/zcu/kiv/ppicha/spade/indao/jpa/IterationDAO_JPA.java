package cz.zcu.kiv.ppicha.spade.indao.jpa;

import cz.zcu.kiv.ppicha.spade.domain.Iteration;
import cz.zcu.kiv.ppicha.spade.indao.IterationDAO;

import javax.persistence.EntityManager;

/**
 * Created by Petr on 21.1.2016.
 */
public class IterationDAO_JPA extends GenericDAO_JPA<Iteration> implements IterationDAO {

    public IterationDAO_JPA(EntityManager em){
        super(em);
    }

    public Iteration save(Iteration iteration) {
        entityManager.getTransaction().begin();

        Iteration ret;
        if(iteration.getId() == 0) {
            entityManager.persist(iteration);
            ret = iteration;
        } else {
            ret =  entityManager.merge(iteration);
        }

        entityManager.getTransaction().commit();

        return ret;
    }
}