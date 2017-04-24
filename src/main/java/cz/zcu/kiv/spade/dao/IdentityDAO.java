package cz.zcu.kiv.spade.dao;

import cz.zcu.kiv.spade.domain.Identity;

public interface IdentityDAO extends GenericDAO<Identity> {

    Identity save(Identity identity);

}
