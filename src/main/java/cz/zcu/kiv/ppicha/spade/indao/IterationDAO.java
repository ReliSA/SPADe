package cz.zcu.kiv.ppicha.spade.indao;

import cz.zcu.kiv.ppicha.spade.domain.Iteration;

public interface IterationDAO extends GenericDAO<Iteration> {

    Iteration save(Iteration iteration);

}
