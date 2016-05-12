package cz.zcu.kiv.ppicha.spade.indao.jpa;

import cz.zcu.kiv.ppicha.spade.domain.Identity;
import cz.zcu.kiv.ppicha.spade.indao.IdentityDAO;

import javax.persistence.EntityManager;

public class IdentityDAO_JPA extends GenericDAO_JPA<Identity> implements IdentityDAO {

    public IdentityDAO_JPA(EntityManager em){
        super(em, Identity.class);
    }

    public Identity save(Identity identity) {
        entityManager.getTransaction().begin();

        Identity ret;
        if(identity.getId() == 0) {
            entityManager.persist(identity);
            ret = identity;
        } else {
            ret =  entityManager.merge(identity);
        }

        entityManager.getTransaction().commit();

        return ret;
    }
}