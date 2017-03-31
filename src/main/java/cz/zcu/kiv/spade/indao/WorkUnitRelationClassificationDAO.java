package cz.zcu.kiv.spade.indao;

import cz.zcu.kiv.spade.domain.WorkUnitRelationClassification;

public interface WorkUnitRelationClassificationDAO extends GenericDAO<WorkUnitRelationClassification> {

    WorkUnitRelationClassification save(WorkUnitRelationClassification classification);

}
