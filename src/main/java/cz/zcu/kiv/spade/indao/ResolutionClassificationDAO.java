package cz.zcu.kiv.spade.indao;

import cz.zcu.kiv.spade.domain.ResolutionClassification;

public interface ResolutionClassificationDAO extends GenericDAO<ResolutionClassification> {

    ResolutionClassification save(ResolutionClassification classification);

}
