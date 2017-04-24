package cz.zcu.kiv.spade.dao.jpa;

import cz.zcu.kiv.spade.dao.CriterionDAO;
import cz.zcu.kiv.spade.domain.Criterion;

import javax.persistence.EntityManager;

public class CriterionDAO_JPA extends GenericDAO_JPA<Criterion> implements CriterionDAO {

    public CriterionDAO_JPA(EntityManager em) {
        super(em, Criterion.class);
    }

    public Criterion save(Criterion criterion) {
        entityManager.getTransaction().begin();

        Criterion ret;
        if (criterion.getId() == 0) {
            entityManager.persist(criterion);
            ret = criterion;
        } else {
            ret = entityManager.merge(criterion);
        }

        entityManager.getTransaction().commit();

        return ret;
    }
}
