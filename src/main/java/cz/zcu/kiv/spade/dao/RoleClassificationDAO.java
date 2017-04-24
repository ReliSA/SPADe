package cz.zcu.kiv.spade.dao;

import cz.zcu.kiv.spade.domain.RoleClassification;
import cz.zcu.kiv.spade.domain.enums.RoleClass;

public interface RoleClassificationDAO extends GenericDAO<RoleClassification> {

    RoleClassification save(RoleClassification classification);

    RoleClassification findByClass(RoleClass aClass);
}
