package cz.zcu.kiv.spade.indao;

import cz.zcu.kiv.spade.domain.WorkUnit;

public interface WorkUnitDAO extends GenericDAO<WorkUnit> {

    WorkUnit save(WorkUnit wu);

}
