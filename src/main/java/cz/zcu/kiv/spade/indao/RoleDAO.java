package cz.zcu.kiv.spade.indao;

import cz.zcu.kiv.spade.domain.Role;

public interface RoleDAO extends GenericDAO<Role> {

    Role save(Role role);

}
