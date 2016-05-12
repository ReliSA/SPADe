package cz.zcu.kiv.ppicha.spade.indao.jpa;

import cz.zcu.kiv.ppicha.spade.domain.Activity;
import cz.zcu.kiv.ppicha.spade.indao.ActivityDAO;

import javax.persistence.EntityManager;

public class ActivityDAO_JPA extends GenericDAO_JPA<Activity> implements ActivityDAO {

    public ActivityDAO_JPA(EntityManager em){
        super(em, Activity.class);
    }

    public Activity save(Activity activity) {
        entityManager.getTransaction().begin();

        Activity ret;
        if(activity.getId() == 0) {
            entityManager.persist(activity);
            ret = activity;
        } else {
            ret =  entityManager.merge(activity);
        }

        entityManager.getTransaction().commit();

        return ret;
    }
}
