package cz.zcu.kiv.spade.indao;

import cz.zcu.kiv.spade.domain.Group;

public interface GroupDAO extends GenericDAO<Group> {

    Group save(Group identityGroup);

}
