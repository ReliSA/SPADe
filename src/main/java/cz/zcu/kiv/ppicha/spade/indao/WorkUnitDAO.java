package cz.zcu.kiv.ppicha.spade.indao;

import cz.zcu.kiv.ppicha.spade.domain.WorkUnit;

/**
 * Created by Petr on 21.1.2016.
 */
public interface WorkUnitDAO extends GenericDAO<WorkUnit>{

    WorkUnit save(WorkUnit wu);

}
