package cz.zcu.kiv.ppicha.spade.indao;

import cz.zcu.kiv.ppicha.spade.domain.WorkItemChange;

/**
 * Created by Petr on 21.1.2016.
 */
public interface WorkItemChangeDAO extends GenericDAO<WorkItemChange>{

    WorkItemChange save(WorkItemChange wic);

}
