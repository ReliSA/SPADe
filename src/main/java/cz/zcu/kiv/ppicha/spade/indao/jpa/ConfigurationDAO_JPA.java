package cz.zcu.kiv.ppicha.spade.indao.jpa;

import cz.zcu.kiv.ppicha.spade.domain.Configuration;
import cz.zcu.kiv.ppicha.spade.indao.ConfigurationDAO;

import javax.persistence.EntityManager;

/**
 * Created by Petr on 21.1.2016.
 */
public class ConfigurationDAO_JPA extends GenericDAO_JPA<Configuration> implements ConfigurationDAO {

    public ConfigurationDAO_JPA(EntityManager em){
        super(em);
    }

    public Configuration save(Configuration config) {
        entityManager.getTransaction().begin();

        Configuration ret;
        if(config.getId() == 0) {
            entityManager.persist(config);
            ret = config;
        } else {
            ret =  entityManager.merge(config);
        }

        entityManager.getTransaction().commit();

        return ret;
    }
}
