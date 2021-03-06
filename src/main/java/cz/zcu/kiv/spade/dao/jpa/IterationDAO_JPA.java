package cz.zcu.kiv.spade.dao.jpa;

import cz.zcu.kiv.spade.dao.IterationDAO;
import cz.zcu.kiv.spade.domain.Iteration;

import javax.persistence.EntityManager;

public class IterationDAO_JPA extends GenericDAO_JPA<Iteration> implements IterationDAO {

    public IterationDAO_JPA(EntityManager em) {
        super(em, Iteration.class);
    }

    public Iteration save(Iteration iteration) {
        entityManager.getTransaction().begin();

        Iteration ret;
        if (iteration.getId() == 0) {
            entityManager.persist(iteration);
            ret = iteration;
        } else {
            ret = entityManager.merge(iteration);
        }

        entityManager.getTransaction().commit();

        return ret;
    }
}