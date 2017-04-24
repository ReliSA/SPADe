package cz.zcu.kiv.spade.dao;

import cz.zcu.kiv.spade.domain.ToolInstance;
import cz.zcu.kiv.spade.domain.enums.Tool;

public interface ToolInstanceDAO extends GenericDAO<ToolInstance> {

    ToolInstance save(ToolInstance ti);

    ToolInstance findByToolInstance(String externalId, Tool tool);

    Tool findToolByProjectInstanceUrl(String url);
}
