package cz.zcu.kiv.spade.dao;

import cz.zcu.kiv.spade.domain.Activity;

public interface ActivityDAO extends GenericDAO<Activity> {

    Activity save(Activity activity);

}
