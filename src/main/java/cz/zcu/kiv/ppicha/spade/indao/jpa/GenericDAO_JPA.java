package cz.zcu.kiv.ppicha.spade.indao.jpa;

import cz.zcu.kiv.ppicha.spade.domain.abstracts.BaseEntity;
import cz.zcu.kiv.ppicha.spade.indao.GenericDAO;

import javax.persistence.EntityManager;

/**
 * Created by Petr on 20.1.2016.
 */
public class GenericDAO_JPA implements GenericDAO {

    protected EntityManager entityManager;

    public GenericDAO_JPA(EntityManager em) {
        this.entityManager = em;
    }

    @Override
    public BaseEntity save(BaseEntity entity) {
        entityManager.getTransaction().begin();

        BaseEntity ret = null;
        if(entity.getId() == 0) {
            entityManager.persist(entity);
            ret = entity;
        } else {
            ret =  entityManager.merge(entity);
        }

        entityManager.getTransaction().commit();

        return ret;
    }
}
