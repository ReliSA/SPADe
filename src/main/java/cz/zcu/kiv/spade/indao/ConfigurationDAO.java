package cz.zcu.kiv.spade.indao;

import cz.zcu.kiv.spade.domain.Configuration;

public interface ConfigurationDAO extends GenericDAO<Configuration> {

    Configuration save(Configuration cfg);

}
