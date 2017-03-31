package cz.zcu.kiv.spade.load;

import cz.zcu.kiv.spade.domain.ProjectInstance;
import cz.zcu.kiv.spade.indao.ProjectInstanceDAO;
import cz.zcu.kiv.spade.indao.jpa.ProjectInstanceDAO_JPA;

import javax.persistence.EntityManager;

public class Loader {

    private EntityManager em;

    public Loader(EntityManager em) {
        this.em = em;
    }

    public void loadProjectInstance(ProjectInstance pi) {
        ProjectInstanceDAO piDao = new ProjectInstanceDAO_JPA(em);

        piDao.save(pi);

    }
}
