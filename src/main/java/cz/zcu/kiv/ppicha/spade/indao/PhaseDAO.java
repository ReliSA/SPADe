package cz.zcu.kiv.ppicha.spade.indao;

import cz.zcu.kiv.ppicha.spade.domain.Phase;

public interface PhaseDAO extends GenericDAO<Phase> {

    Phase save(Phase phase);

}
