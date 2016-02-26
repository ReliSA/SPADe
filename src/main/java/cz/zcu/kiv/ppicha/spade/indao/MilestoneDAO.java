package cz.zcu.kiv.ppicha.spade.indao;

import cz.zcu.kiv.ppicha.spade.domain.Milestone;

/**
 * Created by Petr on 21.1.2016.
 */
public interface MilestoneDAO extends GenericDAO<Milestone> {

    Milestone save(Milestone milestone);

}
