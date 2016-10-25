package cz.zcu.kiv.spade.indao;

import cz.zcu.kiv.spade.domain.WorkUnitType;

public interface WorkUnitTypeDAO extends GenericDAO<WorkUnitType> {

    WorkUnitType save(WorkUnitType type);

}
