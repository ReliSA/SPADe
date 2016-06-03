package cz.zcu.kiv.spade.indao;

import cz.zcu.kiv.spade.domain.ToolInstance;

public interface ToolInstanceDAO extends GenericDAO<ToolInstance> {

    ToolInstance save(ToolInstance ti);

}
