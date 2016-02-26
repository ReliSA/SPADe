package cz.zcu.kiv.ppicha.spade.indao.jpa;

import cz.zcu.kiv.ppicha.spade.domain.Role;
import cz.zcu.kiv.ppicha.spade.indao.RoleDAO;

import javax.persistence.EntityManager;

/**
 * Created by Petr on 21.1.2016.
 */
public class RoleDAO_JPA extends GenericDAO_JPA<Role> implements RoleDAO {

    public RoleDAO_JPA(EntityManager em){
        super(em);
    }

    public Role save(Role role) {
        entityManager.getTransaction().begin();

        Role ret;
        if(role.getId() == 0) {
            entityManager.persist(role);
            ret = role;
        } else {
            ret =  entityManager.merge(role);
        }

        entityManager.getTransaction().commit();

        return ret;
    }
}
