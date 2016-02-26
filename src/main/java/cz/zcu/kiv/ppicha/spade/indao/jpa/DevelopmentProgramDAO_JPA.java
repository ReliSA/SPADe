package cz.zcu.kiv.ppicha.spade.indao.jpa;

import cz.zcu.kiv.ppicha.spade.domain.DevelopmentProgram;
import cz.zcu.kiv.ppicha.spade.indao.DevelopmentProgramDAO;

import javax.persistence.EntityManager;

/**
 * Created by Petr on 21.1.2016.
 */
public class DevelopmentProgramDAO_JPA extends GenericDAO_JPA<DevelopmentProgram> implements DevelopmentProgramDAO {

    public DevelopmentProgramDAO_JPA(EntityManager em){
        super(em);
    }

    public DevelopmentProgram save(DevelopmentProgram devProg) {
        entityManager.getTransaction().begin();

        DevelopmentProgram ret;
        if(devProg.getId() == 0) {
            entityManager.persist(devProg);
            ret = devProg;
        } else {
            ret =  entityManager.merge(devProg);
        }

        entityManager.getTransaction().commit();

        return ret;
    }
}
