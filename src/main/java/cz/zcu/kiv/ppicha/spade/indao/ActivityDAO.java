package cz.zcu.kiv.ppicha.spade.indao;

import cz.zcu.kiv.ppicha.spade.domain.Activity;

/**
 * Created by Petr on 21.1.2016.
 */
public interface ActivityDAO extends GenericDAO<Activity>{

    Activity save(Activity activity);

}
