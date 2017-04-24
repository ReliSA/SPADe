package cz.zcu.kiv.spade.dao.jpa;

import cz.zcu.kiv.spade.dao.PhaseDAO;
import cz.zcu.kiv.spade.domain.Phase;

import javax.persistence.EntityManager;

public class PhaseDAO_JPA extends GenericDAO_JPA<Phase> implements PhaseDAO {

    public PhaseDAO_JPA(EntityManager em) {
        super(em, Phase.class);
    }

    public Phase save(Phase phase) {
        entityManager.getTransaction().begin();

        Phase ret;
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
