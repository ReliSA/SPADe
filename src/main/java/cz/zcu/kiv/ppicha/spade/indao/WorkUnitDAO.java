package cz.zcu.kiv.ppicha.spade.indao;

import cz.zcu.kiv.ppicha.spade.domain.WorkUnit;

public interface WorkUnitDAO extends GenericDAO<WorkUnit>{

    WorkUnit save(WorkUnit wu);

}
