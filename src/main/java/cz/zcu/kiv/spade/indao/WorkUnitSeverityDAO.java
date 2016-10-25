package cz.zcu.kiv.spade.indao;

import cz.zcu.kiv.spade.domain.WorkUnitSeverity;

public interface WorkUnitSeverityDAO extends GenericDAO<WorkUnitSeverity> {

    WorkUnitSeverity save(WorkUnitSeverity severity);

}
