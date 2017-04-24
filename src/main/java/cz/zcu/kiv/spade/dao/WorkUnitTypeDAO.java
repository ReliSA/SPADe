package cz.zcu.kiv.spade.dao;

import cz.zcu.kiv.spade.domain.WorkUnitType;

public interface WorkUnitTypeDAO extends GenericDAO<WorkUnitType> {

    WorkUnitType save(WorkUnitType wuType);
}
