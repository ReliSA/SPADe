package cz.zcu.kiv.ppicha.spade.indao;

import cz.zcu.kiv.ppicha.spade.domain.Phase;

/**
 * Created by Petr on 21.1.2016.
 */
public interface PhaseDAO extends GenericDAO<Phase> {

    Phase save(Phase phase);

}
