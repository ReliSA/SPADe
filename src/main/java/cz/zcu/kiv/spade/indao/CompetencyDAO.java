package cz.zcu.kiv.spade.indao;

import cz.zcu.kiv.spade.domain.Competency;

public interface CompetencyDAO extends GenericDAO<Competency> {

    Competency save(Competency competency);
}
