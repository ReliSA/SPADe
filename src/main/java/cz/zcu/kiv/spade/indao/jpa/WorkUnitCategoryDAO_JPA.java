package cz.zcu.kiv.spade.indao.jpa;

import cz.zcu.kiv.spade.domain.WorkUnitCategory;
import cz.zcu.kiv.spade.indao.WorkUnitCategoryDAO;

import javax.persistence.EntityManager;

public class WorkUnitCategoryDAO_JPA extends GenericDAO_JPA<WorkUnitCategory> implements WorkUnitCategoryDAO {

    public WorkUnitCategoryDAO_JPA(EntityManager em) {
        super(em, WorkUnitCategory.class);
    }

    public WorkUnitCategory save(WorkUnitCategory category) {
        entityManager.getTransaction().begin();

        WorkUnitCategory ret;
        if (category.getId() == 0) {
            entityManager.persist(category);
            ret = category;
        } else {
            ret = entityManager.merge(category);
        }

        entityManager.getTransaction().commit();

        return ret;
    }
}