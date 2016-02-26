package cz.zcu.kiv.ppicha.spade.indao.jpa;

import cz.zcu.kiv.ppicha.spade.domain.abstracts.BaseEntity;
import cz.zcu.kiv.ppicha.spade.indao.GenericDAO;

import javax.persistence.EntityManager;

/**
 * Created by Petr on 20.1.2016.
 */
public class GenericDAO_JPA<E extends BaseEntity> implements GenericDAO<E> {

    protected EntityManager entityManager;
    protected Class<E> persistedClass;

    public GenericDAO_JPA(EntityManager em, Class<E> persistedClass) {
        this.entityManager = em;
        this.persistedClass = persistedClass;
    }

    @Override
    public E save(E entity) {
        entityManager.getTransaction().begin();

        E ret;
        if(entity.getId() == 0) {
            entityManager.persist(entity);
            ret = entity;
        } else {
            ret =  entityManager.merge(entity);
        }

        entityManager.getTransaction().commit();

        return ret;
    }

    @Override
    public E findByID(long id) {
        entityManager.getTransaction().begin();

        return entityManager.find(persistedClass, id);

        entityManager.getTransaction().commit();
    }

    @Override
    public void deleteByID(long id) {
        entityManager.getTransaction().begin();

        E entity = entityManager.find(persistedClass, id);
        if(entity != null) {
            entityManager.remove(entity);
        }

        entityManager.getTransaction().commit();
    }
}
