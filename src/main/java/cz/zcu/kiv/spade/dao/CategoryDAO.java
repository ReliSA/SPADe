package cz.zcu.kiv.spade.dao;

import cz.zcu.kiv.spade.domain.Category;

public interface CategoryDAO extends GenericDAO<Category> {

    Category save(Category branch);

}
