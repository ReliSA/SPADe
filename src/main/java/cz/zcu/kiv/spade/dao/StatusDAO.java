package cz.zcu.kiv.spade.dao;

import cz.zcu.kiv.spade.domain.Status;

public interface StatusDAO extends GenericDAO<Status> {

    Status save(Status status);
}
