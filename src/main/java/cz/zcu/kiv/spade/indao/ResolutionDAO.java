package cz.zcu.kiv.spade.indao;

import cz.zcu.kiv.spade.domain.Resolution;

public interface ResolutionDAO extends GenericDAO<Resolution> {

    Resolution save(Resolution resolution);
}
