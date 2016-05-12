package cz.zcu.kiv.ppicha.spade.indao;

import cz.zcu.kiv.ppicha.spade.domain.Milestone;

public interface MilestoneDAO extends GenericDAO<Milestone> {

    Milestone save(Milestone milestone);

}
