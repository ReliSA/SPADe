package cz.zcu.kiv.ppicha.spade.indao;

import cz.zcu.kiv.ppicha.spade.domain.ToolInstance;

public interface ToolInstanceDAO extends GenericDAO<ToolInstance>{

    ToolInstance save(ToolInstance ti);

}
