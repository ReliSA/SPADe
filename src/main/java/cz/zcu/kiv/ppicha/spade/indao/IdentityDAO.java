package cz.zcu.kiv.ppicha.spade.indao;

import cz.zcu.kiv.ppicha.spade.domain.Identity;

/**
 * Created by Petr on 21.1.2016.
 */
public interface IdentityDAO extends GenericDAO<Identity> {

    Identity save(Identity identity);

}
