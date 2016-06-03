package cz.zcu.kiv.spade.indao;

import cz.zcu.kiv.spade.domain.ToolProjectInstance;

public interface ToolProjectInstanceDAO extends GenericDAO<ToolProjectInstance> {

    ToolProjectInstance save(ToolProjectInstance tpi);

}
