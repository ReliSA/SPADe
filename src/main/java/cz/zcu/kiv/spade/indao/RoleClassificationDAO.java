package cz.zcu.kiv.spade.indao;

import cz.zcu.kiv.spade.domain.RoleClassification;

public interface RoleClassificationDAO extends GenericDAO<RoleClassification> {

    RoleClassification save(RoleClassification classification);

}
