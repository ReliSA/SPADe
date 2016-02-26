package cz.zcu.kiv.ppicha.spade.indao;

import cz.zcu.kiv.ppicha.spade.domain.Competency;

/**
 * Created by Petr on 21.1.2016.
 */
public interface CompetencyDAO extends GenericDAO<Competency>{

    Competency save(Competency competency);
}
