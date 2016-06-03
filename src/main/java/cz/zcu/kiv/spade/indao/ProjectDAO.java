package cz.zcu.kiv.spade.indao;

import cz.zcu.kiv.spade.domain.Project;

public interface ProjectDAO extends GenericDAO<Project> {

    Project save(Project project);

}
