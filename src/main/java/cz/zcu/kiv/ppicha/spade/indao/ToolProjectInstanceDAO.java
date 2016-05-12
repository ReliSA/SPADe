package cz.zcu.kiv.ppicha.spade.indao;

import cz.zcu.kiv.ppicha.spade.domain.ToolProjectInstance;

public interface ToolProjectInstanceDAO extends GenericDAO<ToolProjectInstance> {

    ToolProjectInstance save(ToolProjectInstance tpi);

}
