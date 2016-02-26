package cz.zcu.kiv.ppicha.spade.indao;

import cz.zcu.kiv.ppicha.spade.domain.ToolInstance;

/**
 * Created by Petr on 21.1.2016.
 */
public interface ToolInstanceDAO extends GenericDAO<ToolInstance>{

    ToolInstance save(ToolInstance ti);

}
