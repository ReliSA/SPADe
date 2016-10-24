package cz.zcu.kiv.spade.indao;

import cz.zcu.kiv.spade.domain.ProjectInstance;

public interface ToolProjectInstanceDAO extends GenericDAO<ProjectInstance> {

    ProjectInstance save(ProjectInstance tpi);

}
