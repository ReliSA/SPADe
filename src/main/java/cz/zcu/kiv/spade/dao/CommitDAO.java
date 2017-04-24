package cz.zcu.kiv.spade.dao;

import cz.zcu.kiv.spade.domain.Commit;

public interface CommitDAO extends GenericDAO<Commit> {

    Commit save(Commit commit);

}