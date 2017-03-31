package cz.zcu.kiv.spade.indao;

import cz.zcu.kiv.spade.domain.StatusClassification;

public interface StatusClassificationDAO extends GenericDAO<StatusClassification> {

    StatusClassification save(StatusClassification classification);

}
