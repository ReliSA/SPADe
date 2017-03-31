package cz.zcu.kiv.spade.indao;

import cz.zcu.kiv.spade.domain.PriorityClassification;

public interface PriorityClassificationDAO extends GenericDAO<PriorityClassification> {

    PriorityClassification save(PriorityClassification classification);

}
