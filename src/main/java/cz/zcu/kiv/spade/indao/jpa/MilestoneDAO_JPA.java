package cz.zcu.kiv.spade.indao.jpa;

import cz.zcu.kiv.spade.domain.Milestone;
import cz.zcu.kiv.spade.indao.MilestoneDAO;

import javax.persistence.EntityManager;

public class MilestoneDAO_JPA extends GenericDAO_JPA<Milestone> implements MilestoneDAO {

    public MilestoneDAO_JPA(EntityManager em) {
        super(em, Milestone.class);
    }

    public Milestone save(Milestone milestone) {
        entityManager.getTransaction().begin();

        Milestone ret;
        if (milestone.getId() == 0) {
            entityManager.persist(milestone);
            ret = milestone;
        } else {
            ret = entityManager.merge(milestone);
        }

        entityManager.getTransaction().commit();

        return ret;
    }
}
