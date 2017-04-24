package cz.zcu.kiv.spade.dao.jpa;

import cz.zcu.kiv.spade.dao.DevelopmentProgramDAO;
import cz.zcu.kiv.spade.domain.DevelopmentProgram;

import javax.persistence.EntityManager;

public class DevelopmentProgramDAO_JPA extends GenericDAO_JPA<DevelopmentProgram> implements DevelopmentProgramDAO {

    public DevelopmentProgramDAO_JPA(EntityManager em) {
        super(em, DevelopmentProgram.class);
    }

    public DevelopmentProgram save(DevelopmentProgram devProg) {
        entityManager.getTransaction().begin();

        DevelopmentProgram ret;
        if (devProg.getId() == 0) {
            entityManager.persist(devProg);
            ret = devProg;
        } else {
            ret = entityManager.merge(devProg);
        }

        entityManager.getTransaction().commit();

        return ret;
    }
}
