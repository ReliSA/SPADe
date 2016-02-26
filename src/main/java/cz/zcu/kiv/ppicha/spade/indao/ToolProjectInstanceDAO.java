package cz.zcu.kiv.ppicha.spade.indao;

import cz.zcu.kiv.ppicha.spade.domain.ToolProjectInstance;

/**
 * Created by Petr on 21.1.2016.
 */
public interface ToolProjectInstanceDAO extends GenericDAO<ToolProjectInstance> {

    ToolProjectInstance save(ToolProjectInstance tpi);

}
