package cz.zcu.kiv.spade.dao;

import cz.zcu.kiv.spade.domain.WorkUnitTypeClassification;
import cz.zcu.kiv.spade.domain.enums.WorkUnitTypeClass;

public interface WorkUnitTypeClassificationDAO extends GenericDAO<WorkUnitTypeClassification> {

    WorkUnitTypeClassification save(WorkUnitTypeClassification classification);

    WorkUnitTypeClassification findByClass(WorkUnitTypeClass aClass);
}
