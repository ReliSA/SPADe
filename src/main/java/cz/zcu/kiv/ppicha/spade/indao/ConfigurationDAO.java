package cz.zcu.kiv.ppicha.spade.indao;

import cz.zcu.kiv.ppicha.spade.domain.Configuration;

public interface ConfigurationDAO extends GenericDAO<Configuration> {

    Configuration save(Configuration cfg);

}
