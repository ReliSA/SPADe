package cz.zcu.kiv.spade.indao;

import cz.zcu.kiv.spade.domain.WorkItemChange;

public interface WorkItemChangeDAO extends GenericDAO<WorkItemChange> {

    WorkItemChange save(WorkItemChange wic);

}
