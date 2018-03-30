package cz.zcu.kiv.spade.load;

import cz.zcu.kiv.spade.dao.*;
import cz.zcu.kiv.spade.dao.jpa.*;
import cz.zcu.kiv.spade.domain.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Loader {

    /** a JPA persistence unit for updating the SPADe database */
    private static final String PERSISTENCE_UNIT_UPDATE = "update";
    /** JPA entity manager for updating the SPADe database */
    private final EntityManager updateManager;

    public Loader() {
        EntityManagerFactory factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_UPDATE);
        this.updateManager = factory.createEntityManager();
    }

    public void loadProjectInstance(ProjectInstance pi) {
        ProjectInstanceDAO piDao = new ProjectInstanceDAO_JPA(updateManager);
        //piDao.deleteByUrl(pi.getUrl());

        ToolInstanceDAO toolDao = new ToolInstanceDAO_JPA(updateManager);
        ToolInstance real = toolDao.findByToolInstance(pi.getToolInstance().getExternalId(), pi.getToolInstance().getTool());
        if (real != null) pi.setToolInstance(real);

        PriorityClassificationDAO priorityDao = new PriorityClassificationDAO_JPA(updateManager);
        for (Priority priority : pi.getPriorities()) {
            priority.setClassification(priorityDao.findByClass(priority.getaClass()));
        }

        SeverityClassificationDAO severityDao = new SeverityClassificationDAO_JPA(updateManager);
        for (Severity severity : pi.getSeverities()) {
            severity.setClassification(severityDao.findByClass(severity.getaClass()));
        }

        StatusClassificationDAO statusDao = new StatusClassificationDAO_JPA(updateManager);
        for (Status status : pi.getStatuses()) {
            status.setClassification(statusDao.findByClass(status.getaClass()));
        }

        ResolutionClassificationDAO resolutionDao = new ResolutionClassificationDAO_JPA(updateManager);
        for (Resolution resolution : pi.getResolutions()) {
            resolution.setClassification(resolutionDao.findByClass(resolution.getaClass()));
        }

        WorkUnitTypeClassificationDAO typeDao = new WorkUnitTypeClassificationDAO_JPA(updateManager);
        for (WorkUnitType type : pi.getWuTypes()) {
            type.setClassification(typeDao.findByClass(type.getaClass()));
        }

        RelationClassificationDAO relationDao = new RelationClassificationDAO_JPA(updateManager);
        for (Relation relation : pi.getRelations()) {
            relation.setClassification(relationDao.findByClass(relation.getaClass()));
        }

        RoleClassificationDAO roleDao = new RoleClassificationDAO_JPA(updateManager);
        for (Role role : pi.getRoles()) {
            role.setClassification(roleDao.findByClass(role.getaClass()));
        }

        piDao.save(pi);

        updateManager.close();
    }
}
