package cz.zcu.kiv.spade.indao;

import cz.zcu.kiv.spade.domain.Phase;

public interface PhaseDAO extends GenericDAO<Phase> {

    Phase save(Phase phase);

}
