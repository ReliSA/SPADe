package cz.zcu.kiv.spade.indao.jpa;

import cz.zcu.kiv.spade.domain.RoleClassification;
import cz.zcu.kiv.spade.indao.RoleClassificationDAO;

import javax.persistence.EntityManager;

public class RoleClassificationDAO_JPA extends GenericDAO_JPA<RoleClassification> implements RoleClassificationDAO {

    public RoleClassificationDAO_JPA(EntityManager em) {
        super(em, RoleClassification.class);
    }

    public RoleClassification save(RoleClassification classification) {
        entityManager.getTransaction().begin();

        RoleClassification ret;
        if (classification.getId() == 0) {
            entityManager.persist(classification);
            ret = classification;
        } else {
            ret = entityManager.merge(classification);
        }

        entityManager.getTransaction().commit();

        return ret;
    }
}
