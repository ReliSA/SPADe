package cz.zcu.kiv.spade.indao;

import cz.zcu.kiv.spade.domain.ProjectInstance;

public interface ProjectInstanceDAO extends GenericDAO<ProjectInstance> {

    ProjectInstance save(ProjectInstance pi);

}
