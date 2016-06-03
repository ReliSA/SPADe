package cz.zcu.kiv.spade.indao;

import cz.zcu.kiv.spade.domain.Criterion;

public interface CriterionDAO extends GenericDAO<Criterion> {

    Criterion save(Criterion criterion);

}
