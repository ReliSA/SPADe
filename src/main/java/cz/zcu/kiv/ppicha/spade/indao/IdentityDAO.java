package cz.zcu.kiv.ppicha.spade.indao;

import cz.zcu.kiv.ppicha.spade.domain.Identity;

public interface IdentityDAO extends GenericDAO<Identity> {

    Identity save(Identity identity);

}
