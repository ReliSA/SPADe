package cz.zcu.kiv.spade.indao;

import cz.zcu.kiv.spade.domain.IdentityGroup;

public interface IdentityGroupDAO extends GenericDAO<IdentityGroup> {

    IdentityGroup save(IdentityGroup identityGroup);

}
