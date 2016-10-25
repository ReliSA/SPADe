package cz.zcu.kiv.spade.indao;

import cz.zcu.kiv.spade.domain.WorkUnitRelation;

public interface WorkUnitRelationDAO extends GenericDAO<WorkUnitRelation> {

    WorkUnitRelation save(WorkUnitRelation relation);

}
