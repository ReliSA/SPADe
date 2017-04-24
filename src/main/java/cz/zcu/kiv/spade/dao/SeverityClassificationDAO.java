package cz.zcu.kiv.spade.dao;

import cz.zcu.kiv.spade.domain.SeverityClassification;
import cz.zcu.kiv.spade.domain.enums.SeverityClass;

public interface SeverityClassificationDAO extends GenericDAO<SeverityClassification> {

    SeverityClassification save(SeverityClassification classification);

    SeverityClassification findByClass(SeverityClass aClass);
}
