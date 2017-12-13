package cz.zcu.kiv.spade.dao.jpa;

import cz.zcu.kiv.spade.dao.DevelopmentProgramDAO;
import cz.zcu.kiv.spade.domain.DevelopmentProgram;

import javax.persistence.EntityManager;

public class DevelopmentProgramDAO_JPA extends GenericDAO_JPA<DevelopmentProgram> implements DevelopmentProgramDAO {

    public DevelopmentProgramDAO_JPA(EntityManager em) {
        super(em, DevelopmentProgram.class);
    }

    public DevelopmentProgram save(DevelopmentProgram developmentProgram) {
        entityManager.getTransaction().begin();

        DevelopmentProgram ret;
        if (developmentProgram.getId() == 0) {
            entityManager.persist(developmentProgram);
            ret = developmentProgram;
        } else {
            ret = entityManager.merge(developmentProgram);
        }

        entityManager.getTransaction().commit();

        return ret;
    }
}
