package cz.zcu.kiv.spade.indao;

import cz.zcu.kiv.spade.domain.Identity;

public interface IdentityDAO extends GenericDAO<Identity> {

    Identity save(Identity identity);

}
