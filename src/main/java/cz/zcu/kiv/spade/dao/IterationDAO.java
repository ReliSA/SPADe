package cz.zcu.kiv.spade.dao;

import cz.zcu.kiv.spade.domain.Iteration;

public interface IterationDAO extends GenericDAO<Iteration> {

    Iteration save(Iteration iteration);

}
