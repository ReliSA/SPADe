package cz.zcu.kiv.spade.dao;

import cz.zcu.kiv.spade.domain.ResolutionClassification;
import cz.zcu.kiv.spade.domain.enums.ResolutionClass;

public interface ResolutionClassificationDAO extends GenericDAO<ResolutionClassification> {

    ResolutionClassification save(ResolutionClassification classification);

    ResolutionClassification findByClass(ResolutionClass aClass);
}
