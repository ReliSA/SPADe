package cz.zcu.kiv.spade.indao.jpa;

import cz.zcu.kiv.spade.domain.WorkUnitResolution;
import cz.zcu.kiv.spade.indao.WorkUnitResolutionDAO;

import javax.persistence.EntityManager;

public class WorkUnitResolutionDAO_JPA extends GenericDAO_JPA<WorkUnitResolution> implements WorkUnitResolutionDAO {

    public WorkUnitResolutionDAO_JPA(EntityManager em) {
        super(em, WorkUnitResolution.class);
    }

    public WorkUnitResolution save(WorkUnitResolution resolution) {
        entityManager.getTransaction().begin();

        WorkUnitResolution ret;
        if (resolution.getId() == 0) {
            entityManager.persist(resolution);
            ret = resolution;
        } else {
            ret = entityManager.merge(resolution);
        }

        entityManager.getTransaction().commit();

        return ret;
    }
}