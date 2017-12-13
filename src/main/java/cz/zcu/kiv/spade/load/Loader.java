package cz.zcu.kiv.spade.load;

import cz.zcu.kiv.spade.dao.ProjectInstanceDAO;
import cz.zcu.kiv.spade.dao.jpa.ProjectInstanceDAO_JPA;
import cz.zcu.kiv.spade.domain.ProjectInstance;

import javax.persistence.EntityManager;

public class Loader {

    private final EntityManager em;

    public Loader(EntityManager em) {
        this.em = em;
    }

    public void loadProjectInstance(ProjectInstance pi) {
        ProjectInstanceDAO piDao = new ProjectInstanceDAO_JPA(em);

        piDao.save(pi);

    }
}
