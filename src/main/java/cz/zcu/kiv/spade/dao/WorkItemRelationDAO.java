package cz.zcu.kiv.spade.dao;

import cz.zcu.kiv.spade.domain.WorkItemRelation;

public interface WorkItemRelationDAO extends GenericDAO<WorkItemRelation> {

    WorkItemRelation save(WorkItemRelation relation);

}