package cz.zcu.kiv.spade.dao.jpa;

import cz.zcu.kiv.spade.dao.RoleDAO;
import cz.zcu.kiv.spade.domain.Role;

import javax.persistence.EntityManager;

public class RoleDAO_JPA extends GenericDAO_JPA<Role> implements RoleDAO {

    public RoleDAO_JPA(EntityManager em) {
        super(em, Role.class);
    }

    public Role save(Role role) {
        entityManager.getTransaction().begin();

        Role ret;
        if (role.getId() == 0) {
            entityManager.persist(role);
            ret = role;
        } else {
            ret = entityManager.merge(role);
        }

        entityManager.getTransaction().commit();

        return ret;
    }
}
