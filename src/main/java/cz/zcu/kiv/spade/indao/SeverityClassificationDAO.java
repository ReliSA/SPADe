package cz.zcu.kiv.spade.indao;

import cz.zcu.kiv.spade.domain.SeverityClassification;

public interface SeverityClassificationDAO extends GenericDAO<SeverityClassification> {

    SeverityClassification save(SeverityClassification classification);

}
