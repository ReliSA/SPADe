package cz.zcu.kiv.spade.dao;

import cz.zcu.kiv.spade.domain.Relation;

public interface RelationDAO extends GenericDAO<Relation> {

    Relation save(Relation relation);

}
