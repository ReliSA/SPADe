package cz.zcu.kiv.ppicha.spade.indao;

import cz.zcu.kiv.ppicha.spade.domain.Branch;

public interface BranchDAO extends GenericDAO<Branch>{

    Branch save(Branch branch);

}
