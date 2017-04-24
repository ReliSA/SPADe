package cz.zcu.kiv.spade.dao.jpa;

import cz.zcu.kiv.spade.dao.WorkUnitTypeDAO;
import cz.zcu.kiv.spade.domain.WorkUnitType;

import javax.persistence.EntityManager;

public class WorkUnitTypeDAO_JPA extends GenericDAO_JPA<WorkUnitType> implements WorkUnitTypeDAO {

    public WorkUnitTypeDAO_JPA(EntityManager em) {
        super(em, WorkUnitType.class);
    }

    public WorkUnitType save(WorkUnitType wuType) {
        entityManager.getTransaction().begin();

        WorkUnitType ret;
        if (wuType.getId() == 0) {
            entityManager.persist(wuType);
            ret = wuType;
        } else {
            ret = entityManager.merge(wuType);
        }

        entityManager.getTransaction().commit();

        return ret;
    }
}
