package cz.zcu.kiv.spade.dao.jpa;

import cz.zcu.kiv.spade.dao.ResolutionDAO;
import cz.zcu.kiv.spade.domain.Resolution;

import javax.persistence.EntityManager;

public class ResolutionDAO_JPA extends GenericDAO_JPA<Resolution> implements ResolutionDAO {

    public ResolutionDAO_JPA(EntityManager em) {
        super(em, Resolution.class);
    }

    public Resolution save(Resolution resolution) {
        entityManager.getTransaction().begin();

        Resolution ret;
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
