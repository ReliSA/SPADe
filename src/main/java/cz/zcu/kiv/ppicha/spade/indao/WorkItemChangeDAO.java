package cz.zcu.kiv.ppicha.spade.indao;

import cz.zcu.kiv.ppicha.spade.domain.WorkItemChange;

public interface WorkItemChangeDAO extends GenericDAO<WorkItemChange>{

    WorkItemChange save(WorkItemChange wic);

}
