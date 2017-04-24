package cz.zcu.kiv.spade.dao.jpa;

import cz.zcu.kiv.spade.dao.RelationDAO;
import cz.zcu.kiv.spade.domain.Relation;

import javax.persistence.EntityManager;

public class RelationDAO_JPA extends GenericDAO_JPA<Relation> implements RelationDAO {

    public RelationDAO_JPA(EntityManager em) {
        super(em, Relation.class);
    }

    public Relation save(Relation relation) {
        entityManager.getTransaction().begin();

        Relation ret;
        if (relation.getId() == 0) {
            entityManager.persist(relation);
            ret = relation;
        } else {
            ret = entityManager.merge(relation);
        }

        entityManager.getTransaction().commit();

        return ret;
    }
}