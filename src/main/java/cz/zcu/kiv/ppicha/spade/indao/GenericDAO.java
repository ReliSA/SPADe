package cz.zcu.kiv.ppicha.spade.indao;

import cz.zcu.kiv.ppicha.spade.domain.abstracts.BaseEntity;

/**
 * Created by Petr on 20.1.2016.
 */
public interface GenericDAO<E extends BaseEntity> {

    E save(E entity);

    E findByID(long id);

    void deleteByID(long id);

}
