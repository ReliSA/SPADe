package cz.zcu.kiv.spade.dao.jpa;

import cz.zcu.kiv.spade.dao.BranchDAO;
import cz.zcu.kiv.spade.domain.Branch;

import javax.persistence.EntityManager;

public class BranchDAO_JPA extends GenericDAO_JPA<Branch> implements BranchDAO {

    public BranchDAO_JPA(EntityManager em) {
        super(em, Branch.class);
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
