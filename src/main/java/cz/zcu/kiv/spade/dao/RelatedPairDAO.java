package cz.zcu.kiv.spade.dao;

import cz.zcu.kiv.spade.domain.WorkItemRelation;

public interface RelatedPairDAO extends GenericDAO<WorkItemRelation> {

    WorkItemRelation save(WorkItemRelation project);

}