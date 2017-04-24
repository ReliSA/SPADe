package cz.zcu.kiv.spade.dao.jpa;

import cz.zcu.kiv.spade.dao.VCSTagDAO;
import cz.zcu.kiv.spade.domain.VCSTag;

import javax.persistence.EntityManager;

public class VCSTagDAO_JPA extends GenericDAO_JPA<VCSTag> implements VCSTagDAO {

    public VCSTagDAO_JPA(EntityManager em) {
        super(em, VCSTag.class);
    }

    public VCSTag save(VCSTag tag) {
        entityManager.getTransaction().begin();

        VCSTag ret;
        if (tag.getId() == 0) {
            entityManager.persist(tag);
            ret = tag;
        } else {
            ret = entityManager.merge(tag);
        }

        entityManager.getTransaction().commit();

        return ret;
    }
}
