package cz.zcu.kiv.spade.indao;

import cz.zcu.kiv.spade.domain.WorkUnitCategory;

public interface WorkUnitCategoryDAO extends GenericDAO<WorkUnitCategory> {

    WorkUnitCategory save(WorkUnitCategory category);

}
