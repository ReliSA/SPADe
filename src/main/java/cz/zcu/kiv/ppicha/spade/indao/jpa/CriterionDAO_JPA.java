package cz.zcu.kiv.ppicha.spade.indao.jpa;

import cz.zcu.kiv.ppicha.spade.domain.Criterion;
import cz.zcu.kiv.ppicha.spade.indao.CriterionDAO;

import javax.persistence.EntityManager;

public class CriterionDAO_JPA extends GenericDAO_JPA<Criterion> implements CriterionDAO {

    public CriterionDAO_JPA(EntityManager em){
        super(em, Criterion.class);
    }

    public Criterion save(Criterion criterion) {
        entityManager.getTransaction().begin();

        Criterion ret;
        if(criterion.getId() == 0) {
            entityManager.persist(criterion);
            ret = criterion;
        } else {
            ret =  entityManager.merge(criterion);
        }

        entityManager.getTransaction().commit();

        return ret;
    }
}
