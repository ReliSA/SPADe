package cz.zcu.kiv.ppicha.spade.indao;

import cz.zcu.kiv.ppicha.spade.domain.Project;

public interface ProjectDAO extends GenericDAO<Project> {

    Project save(Project project);

}
