package cz.zcu.kiv.spade.dao.jpa;

import cz.zcu.kiv.spade.dao.CommitDAO;
import cz.zcu.kiv.spade.domain.Commit;

import javax.persistence.EntityManager;

public class CommitDAO_JPA extends GenericDAO_JPA<Commit> implements CommitDAO {

    public CommitDAO_JPA(EntityManager em) {
        super(em, Commit.class);
    }

    public Commit save(Commit commit) {
        entityManager.getTransaction().begin();

        Commit ret;
        if (commit.getId() == 0) {
            entityManager.persist(commit);
            ret = commit;
        } else {
            ret = entityManager.merge(commit);
        }

        entityManager.getTransaction().commit();

        return ret;
    }
}
