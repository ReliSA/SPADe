package cz.zcu.kiv.spade.indao.jpa;

import cz.zcu.kiv.spade.domain.IdentityGroup;
import cz.zcu.kiv.spade.indao.GroupDAO;

import javax.persistence.EntityManager;

public class GroupDAO_JPA extends GenericDAO_JPA<IdentityGroup> implements GroupDAO {

    public GroupDAO_JPA(EntityManager em) {
        super(em, IdentityGroup.class);
    }

    public IdentityGroup save(IdentityGroup identityGroup) {
        entityManager.getTransaction().begin();

        IdentityGroup ret;
        if (identityGroup.getId() == 0) {
            entityManager.persist(identityGroup);
            ret = identityGroup;
        } else {
            ret = entityManager.merge(identityGroup);
        }

        entityManager.getTransaction().commit();

        return ret;
    }

}
