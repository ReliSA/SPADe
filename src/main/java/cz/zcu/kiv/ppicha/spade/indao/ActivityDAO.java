package cz.zcu.kiv.ppicha.spade.indao;

import cz.zcu.kiv.ppicha.spade.domain.Activity;

public interface ActivityDAO extends GenericDAO<Activity>{

    Activity save(Activity activity);

}
