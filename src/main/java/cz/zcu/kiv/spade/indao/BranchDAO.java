package cz.zcu.kiv.spade.indao;

import cz.zcu.kiv.spade.domain.Branch;

public interface BranchDAO extends GenericDAO<Branch> {

    Branch save(Branch branch);

}
