package cz.zcu.kiv.spade.indao;

import cz.zcu.kiv.spade.domain.WorkUnitTypeClassification;

public interface WorkUnitTypeClassificationDAO extends GenericDAO<WorkUnitTypeClassification> {

    WorkUnitTypeClassification save(WorkUnitTypeClassification classification);

}
