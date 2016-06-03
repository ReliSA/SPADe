package cz.zcu.kiv.spade.indao.jpa;

import cz.zcu.kiv.spade.domain.Competency;
import cz.zcu.kiv.spade.indao.CompetencyDAO;

import javax.persistence.EntityManager;

public class CompetencyDAO_JPA extends GenericDAO_JPA<Competency> implements CompetencyDAO {

    public CompetencyDAO_JPA(EntityManager em) {
        super(em, Competency.class);
    }

    public Competency save(Competency competency) {
        entityManager.getTransaction().begin();

        Competency ret;
        if (competency.getId() == 0) {
            entityManager.persist(competency);
            ret = competency;
        } else {
            ret = entityManager.merge(competency);
        }

        entityManager.getTransaction().commit();

        return ret;
    }
}
