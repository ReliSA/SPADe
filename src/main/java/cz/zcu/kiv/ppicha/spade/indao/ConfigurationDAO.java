package cz.zcu.kiv.ppicha.spade.indao;

import cz.zcu.kiv.ppicha.spade.domain.Configuration;

/**
 * Created by Petr on 21.1.2016.
 */
public interface ConfigurationDAO extends GenericDAO<Configuration> {

    Configuration save(Configuration cfg);

}
