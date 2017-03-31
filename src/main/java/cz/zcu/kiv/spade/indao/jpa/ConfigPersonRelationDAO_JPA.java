package cz.zcu.kiv.spade.indao.jpa;

import cz.zcu.kiv.spade.domain.ConfigPersonRelation;
import cz.zcu.kiv.spade.indao.ConfigPersonRelationDAO;

import javax.persistence.EntityManager;

public class ConfigPersonRelationDAO_JPA extends GenericDAO_JPA<ConfigPersonRelation> implements ConfigPersonRelationDAO {

    public ConfigPersonRelationDAO_JPA(EntityManager em) {
        super(em, ConfigPersonRelation.class);
    }

    public ConfigPersonRelation save(ConfigPersonRelation cpr) {
        entityManager.getTransaction().begin();

        ConfigPersonRelation ret;
        if (cpr.getId() == 0) {
            entityManager.persist(cpr);
            ret = cpr;
        } else {
            ret = entityManager.merge(cpr);
        }

        entityManager.getTransaction().commit();

        return ret;
    }
}
