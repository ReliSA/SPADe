package cz.zcu.kiv.spade.indao;

import cz.zcu.kiv.spade.domain.ConfigPersonRelation;

public interface ConfigPersonRelationDAO extends GenericDAO<ConfigPersonRelation> {

    ConfigPersonRelation save(ConfigPersonRelation cpr);

}
