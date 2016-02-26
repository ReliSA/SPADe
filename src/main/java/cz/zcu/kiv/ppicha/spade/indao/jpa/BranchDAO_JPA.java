package cz.zcu.kiv.ppicha.spade.indao.jpa;

import cz.zcu.kiv.ppicha.spade.domain.Branch;
import cz.zcu.kiv.ppicha.spade.indao.BranchDAO;

import javax.persistence.EntityManager;

/**
 * Created by Petr on 21.1.2016.
 */
public class BranchDAO_JPA extends GenericDAO_JPA<Branch> implements BranchDAO {

    public BranchDAO_JPA(EntityManager em) {
        super(em);
    }

    public Branch save(Branch branch) {
        entityManager.getTransaction().begin();

        Branch ret;
        if (branch.getId() == 0) {
            entityManager.persist(branch);
            ret = branch;
        } else {
            ret = entityManager.merge(branch);
        }

        entityManager.getTransaction().commit();

        return ret;
    }
}
