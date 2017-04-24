package cz.zcu.kiv.spade.dao.jpa;

import cz.zcu.kiv.spade.dao.FieldChangeDAO;
import cz.zcu.kiv.spade.domain.FieldChange;

import javax.persistence.EntityManager;

public class FieldChangeDAO_JPA extends GenericDAO_JPA<FieldChange> implements FieldChangeDAO {

    public FieldChangeDAO_JPA(EntityManager em) {
        super(em, FieldChange.class);
    }

    public FieldChange save(FieldChange fieldChange) {
        entityManager.getTransaction().begin();

        FieldChange ret;
        if (fieldChange.getId() == 0) {
            entityManager.persist(fieldChange);
            ret = fieldChange;
        } else {
            ret = entityManager.merge(fieldChange);
        }

        entityManager.getTransaction().commit();

        return ret;
    }
}
