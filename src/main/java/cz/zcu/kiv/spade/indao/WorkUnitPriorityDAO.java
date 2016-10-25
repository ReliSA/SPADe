package cz.zcu.kiv.spade.indao;

import cz.zcu.kiv.spade.domain.WorkUnitPriority;

public interface WorkUnitPriorityDAO extends GenericDAO<WorkUnitPriority> {

    WorkUnitPriority save(WorkUnitPriority priority);

}
