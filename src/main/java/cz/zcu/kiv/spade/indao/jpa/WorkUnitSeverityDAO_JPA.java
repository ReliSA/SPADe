package cz.zcu.kiv.spade.indao.jpa;

import cz.zcu.kiv.spade.domain.WorkUnitSeverity;
import cz.zcu.kiv.spade.indao.WorkUnitSeverityDAO;

import javax.persistence.EntityManager;

public class WorkUnitSeverityDAO_JPA extends GenericDAO_JPA<WorkUnitSeverity> implements WorkUnitSeverityDAO {

    public WorkUnitSeverityDAO_JPA(EntityManager em) {
        super(em, WorkUnitSeverity.class);
    }

    public WorkUnitSeverity save(WorkUnitSeverity severity) {
        entityManager.getTransaction().begin();

        WorkUnitSeverity ret;
        if (severity.getId() == 0) {
            entityManager.persist(severity);
            ret = severity;
        } else {
            ret = entityManager.merge(severity);
        }

        entityManager.getTransaction().commit();

        return ret;
    }
}