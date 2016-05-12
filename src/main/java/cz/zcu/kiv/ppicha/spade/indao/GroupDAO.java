package cz.zcu.kiv.ppicha.spade.indao;

import cz.zcu.kiv.ppicha.spade.domain.IdentityGroup;

public interface GroupDAO extends GenericDAO<IdentityGroup> {

    IdentityGroup save(IdentityGroup identityGroup);

}
