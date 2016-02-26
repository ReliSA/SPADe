package cz.zcu.kiv.ppicha.spade.indao;

import cz.zcu.kiv.ppicha.spade.domain.Branch;

/**
 * Created by Petr on 21.1.2016.
 */
public interface BranchDAO extends GenericDAO<Branch>{

    Branch save(Branch branch);

}
