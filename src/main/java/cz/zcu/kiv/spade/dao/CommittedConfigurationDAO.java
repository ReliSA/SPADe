package cz.zcu.kiv.spade.dao;

import cz.zcu.kiv.spade.domain.CommittedConfiguration;

public interface CommittedConfigurationDAO extends GenericDAO<CommittedConfiguration> {

    CommittedConfiguration save(CommittedConfiguration commitConf);

}