package cz.zcu.kiv.ppicha.spade.indao;

import cz.zcu.kiv.ppicha.spade.domain.Competency;

public interface CompetencyDAO extends GenericDAO<Competency>{

    Competency save(Competency competency);
}
