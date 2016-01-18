package cz.zcu.kiv.ppicha.spade;

import cz.zcu.kiv.ppicha.spade.domain.Project;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.hibernate.service.Service;
import org.hibernate.service.ServiceRegistry;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.Date;

public class Main {

    private static final String PERSISTENCE_UNIT = "cz.zcu.kiv.ppicha.spade";

    public static void main( String[] args ) {

        EntityManagerFactory factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT);
        EntityManager em = factory.createEntityManager();

        //Project p = new Project(0, 0, "p", null, null, new Date(), new Date(), null, null, null, null, null, null, null);

        //em.getTransaction().begin();
        //em.persist(p);
        //em.getTransaction().commit();

        em.close();
    }
}
