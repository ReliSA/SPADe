package cz.zcu.kiv.spade.indao;

import cz.zcu.kiv.spade.domain.WorkUnitStatus;

public interface WorkUnitStatusDAO extends GenericDAO<WorkUnitStatus> {

    WorkUnitStatus save(WorkUnitStatus status);

}
