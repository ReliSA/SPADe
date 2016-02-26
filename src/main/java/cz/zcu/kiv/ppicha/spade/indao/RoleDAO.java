package cz.zcu.kiv.ppicha.spade.indao;

import cz.zcu.kiv.ppicha.spade.domain.Role;

/**
 * Created by Petr on 21.1.2016.
 */
public interface RoleDAO extends GenericDAO<Role> {

    Role save(Role role);

}
