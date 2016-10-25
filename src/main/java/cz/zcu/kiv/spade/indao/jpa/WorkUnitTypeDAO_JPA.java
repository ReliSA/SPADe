package cz.zcu.kiv.spade.indao.jpa;

import cz.zcu.kiv.spade.domain.WorkUnitType;
import cz.zcu.kiv.spade.indao.WorkUnitTypeDAO;

import javax.persistence.EntityManager;

public class WorkUnitTypeDAO_JPA extends GenericDAO_JPA<WorkUnitType> implements WorkUnitTypeDAO {

    public WorkUnitTypeDAO_JPA(EntityManager em) {
        super(em, WorkUnitType.class);
    }

    public WorkUnitType save(WorkUnitType type) {
        entityManager.getTransaction().begin();

        WorkUnitType ret;
        if (type.getId() == 0) {
            entityManager.persist(type);
            ret = type;
        } else {
            ret = entityManager.merge(type);
        }

        entityManager.getTransaction().commit();

        return ret;
    }
}