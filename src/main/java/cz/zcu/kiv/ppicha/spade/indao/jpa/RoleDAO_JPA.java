package cz.zcu.kiv.ppicha.spade.indao.jpa;

import cz.zcu.kiv.ppicha.spade.domain.Role;
import cz.zcu.kiv.ppicha.spade.indao.RoleDAO;

import javax.persistence.EntityManager;

public class RoleDAO_JPA extends GenericDAO_JPA<Role> implements RoleDAO {

    public RoleDAO_JPA(EntityManager em){
        super(em, Role.class);
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
