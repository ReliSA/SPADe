package cz.zcu.kiv.spade.indao.jpa;

import cz.zcu.kiv.spade.domain.Severity;
import cz.zcu.kiv.spade.indao.SeverityDAO;

import javax.persistence.EntityManager;

public class SeverityDAO_JPA extends GenericDAO_JPA<Severity> implements SeverityDAO {

    public SeverityDAO_JPA(EntityManager em) {
        super(em, Severity.class);
    }

    public Severity save(Severity severity) {
        entityManager.getTransaction().begin();

        Severity ret;
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
