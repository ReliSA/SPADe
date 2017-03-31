package cz.zcu.kiv.spade.indao;

import cz.zcu.kiv.spade.domain.Priority;

public interface PriorityDAO extends GenericDAO<Priority> {

    Priority save(Priority priority);
}
