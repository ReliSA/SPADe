package cz.zcu.kiv.spade.dao.jpa;

import cz.zcu.kiv.spade.dao.CommittedConfigurationDAO;
import cz.zcu.kiv.spade.domain.CommittedConfiguration;

import javax.persistence.EntityManager;

public class CommittedConfigurationDAO_JPA extends GenericDAO_JPA<CommittedConfiguration> implements CommittedConfigurationDAO {

    public CommittedConfigurationDAO_JPA(EntityManager em) {
        super(em, CommittedConfiguration.class);
    }

    public CommittedConfiguration save(CommittedConfiguration commitConf) {
        entityManager.getTransaction().begin();

        CommittedConfiguration ret;
        if (commitConf.getId() == 0) {
            entityManager.persist(commitConf);
            ret = commitConf;
        } else {
            ret = entityManager.merge(commitConf);
        }

        entityManager.getTransaction().commit();

        return ret;
    }
}
