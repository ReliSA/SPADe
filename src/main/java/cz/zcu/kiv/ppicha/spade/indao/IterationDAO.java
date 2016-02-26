package cz.zcu.kiv.ppicha.spade.indao;

import cz.zcu.kiv.ppicha.spade.domain.Iteration;

/**
 * Created by Petr on 21.1.2016.
 */
public interface IterationDAO extends GenericDAO<Iteration> {

    Iteration save(Iteration iteration);

}
