package cz.zcu.kiv.spade.dao;

import cz.zcu.kiv.spade.domain.PriorityClassification;
import cz.zcu.kiv.spade.domain.enums.PriorityClass;

public interface PriorityClassificationDAO extends GenericDAO<PriorityClassification> {

    PriorityClassification save(PriorityClassification classification);

    PriorityClassification findByClass(PriorityClass aClass);
}
