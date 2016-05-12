package cz.zcu.kiv.ppicha.spade.indao;

import cz.zcu.kiv.ppicha.spade.domain.Role;

public interface RoleDAO extends GenericDAO<Role> {

    Role save(Role role);

}
