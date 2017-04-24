package cz.zcu.kiv.spade.dao;

import cz.zcu.kiv.spade.domain.Severity;

public interface SeverityDAO extends GenericDAO<Severity> {

    Severity save(Severity severity);
}
