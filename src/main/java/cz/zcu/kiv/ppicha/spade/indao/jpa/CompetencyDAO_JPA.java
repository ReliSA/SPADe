package cz.zcu.kiv.ppicha.spade.indao.jpa;

import cz.zcu.kiv.ppicha.spade.domain.Competency;
import cz.zcu.kiv.ppicha.spade.indao.CompetencyDAO;

import javax.persistence.EntityManager;

/**
 * Created by Petr on 21.1.2016.
 */
public class CompetencyDAO_JPA extends GenericDAO_JPA<Competency> implements CompetencyDAO {

    public CompetencyDAO_JPA(EntityManager em){
        super(em);
    }

    public Competency save(Competency competency) {
        entityManager.getTransaction().begin();

        Competency ret;
        if(competency.getId() == 0) {
            entityManager.persist(competency);
            ret = competency;
        } else {
            ret =  entityManager.merge(competency);
        }

        entityManager.getTransaction().commit();

        return ret;
    }
}
