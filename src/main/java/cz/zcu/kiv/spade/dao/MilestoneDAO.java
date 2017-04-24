package cz.zcu.kiv.spade.dao;

import cz.zcu.kiv.spade.domain.Milestone;

public interface MilestoneDAO extends GenericDAO<Milestone> {

    Milestone save(Milestone milestone);

}
