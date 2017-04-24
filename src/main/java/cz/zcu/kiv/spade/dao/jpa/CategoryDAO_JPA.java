package cz.zcu.kiv.spade.dao.jpa;

import cz.zcu.kiv.spade.dao.CategoryDAO;
import cz.zcu.kiv.spade.domain.Category;

import javax.persistence.EntityManager;

public class CategoryDAO_JPA extends GenericDAO_JPA<Category> implements CategoryDAO {

    public CategoryDAO_JPA(EntityManager em) {
        super(em, Category.class);
    }

    public Category save(Category category) {
        entityManager.getTransaction().begin();

        Category ret;
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