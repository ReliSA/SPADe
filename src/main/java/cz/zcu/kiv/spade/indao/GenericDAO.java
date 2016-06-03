package cz.zcu.kiv.spade.indao;

import cz.zcu.kiv.spade.domain.abstracts.BaseEntity;

public interface GenericDAO<E extends BaseEntity> {

    E save(E entity);

    E findByID(long id);

    void deleteByID(long id);

}
