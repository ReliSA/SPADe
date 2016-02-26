package cz.zcu.kiv.ppicha.spade.indao;

import cz.zcu.kiv.ppicha.spade.domain.Project;

/**
 * Created by Petr on 21.1.2016.
 */
public interface ProjectDAO extends GenericDAO<Project> {

    Project save(Project project);

}
