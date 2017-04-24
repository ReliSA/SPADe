package cz.zcu.kiv.spade.dao;

import cz.zcu.kiv.spade.domain.StatusClassification;
import cz.zcu.kiv.spade.domain.enums.StatusClass;

public interface StatusClassificationDAO extends GenericDAO<StatusClassification> {

    StatusClassification save(StatusClassification classification);

    StatusClassification findByClass(StatusClass aClass);
}
