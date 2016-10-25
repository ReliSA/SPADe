package cz.zcu.kiv.spade.indao;

import cz.zcu.kiv.spade.domain.WorkUnitResolution;

public interface WorkUnitResolutionDAO extends GenericDAO<WorkUnitResolution> {

    WorkUnitResolution save(WorkUnitResolution resolution);

}
