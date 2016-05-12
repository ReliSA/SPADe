package cz.zcu.kiv.ppicha.spade.indao;

import cz.zcu.kiv.ppicha.spade.domain.abstracts.BaseEntity;

public interface GenericDAO<E extends BaseEntity> {

    E save(E entity);

    E findByID(long id);

    void deleteByID(long id);

}
