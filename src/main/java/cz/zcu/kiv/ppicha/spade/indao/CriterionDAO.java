package cz.zcu.kiv.ppicha.spade.indao;

import cz.zcu.kiv.ppicha.spade.domain.Criterion;

public interface CriterionDAO extends GenericDAO<Criterion> {

    Criterion save (Criterion criterion);

}
