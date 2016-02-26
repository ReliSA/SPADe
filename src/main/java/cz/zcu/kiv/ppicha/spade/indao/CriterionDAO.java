package cz.zcu.kiv.ppicha.spade.indao;

import cz.zcu.kiv.ppicha.spade.domain.Criterion;

/**
 * Created by Petr on 21.1.2016.
 */
public interface CriterionDAO extends GenericDAO<Criterion> {

    Criterion save (Criterion criterion);

}
