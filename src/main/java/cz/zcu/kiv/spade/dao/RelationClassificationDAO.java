package cz.zcu.kiv.spade.dao;

import cz.zcu.kiv.spade.domain.RelationClassification;
import cz.zcu.kiv.spade.domain.enums.RelationClass;

public interface RelationClassificationDAO extends GenericDAO<RelationClassification> {

    RelationClassification save(RelationClassification classification);

    RelationClassification findByClass(RelationClass aClass);
}
