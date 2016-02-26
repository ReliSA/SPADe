package cz.zcu.kiv.ppicha.spade.indao.jpa;

import cz.zcu.kiv.ppicha.spade.domain.Phase;
import cz.zcu.kiv.ppicha.spade.indao.PhaseDAO;

import javax.persistence.EntityManager;

/**
 * Created by Petr on 21.1.2016.
 */
public class PhaseDAO_JPA extends GenericDAO_JPA<Phase> implements PhaseDAO {

    public PhaseDAO_JPA(EntityManager em){
        super(em);
    }

    public Phase save(Phase phase) {
        entityManager.getTransaction().begin();

        Phase ret;
        if(phase.getId() == 0) {
            entityManager.persist(phase);
            ret = phase;
        } else {
            ret =  entityManager.merge(phase);
        }

        entityManager.getTransaction().commit();

        return ret;
    }
}
