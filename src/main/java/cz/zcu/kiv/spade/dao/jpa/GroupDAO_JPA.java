package cz.zcu.kiv.spade.dao.jpa;

import cz.zcu.kiv.spade.dao.GroupDAO;
import cz.zcu.kiv.spade.domain.Group;

import javax.persistence.EntityManager;

public class GroupDAO_JPA extends GenericDAO_JPA<Group> implements GroupDAO {

    public GroupDAO_JPA(EntityManager em) {
        super(em, Group.class);
    }

    public Group save(Group group) {
        entityManager.getTransaction().begin();

        Group ret;
        if (group.getId() == 0) {
            entityManager.persist(group);
            ret = group;
        } else {
            ret = entityManager.merge(group);
        }

        entityManager.getTransaction().commit();

        return ret;
    }

}
