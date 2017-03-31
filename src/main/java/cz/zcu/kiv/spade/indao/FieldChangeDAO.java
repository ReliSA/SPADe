package cz.zcu.kiv.spade.indao;

import cz.zcu.kiv.spade.domain.FieldChange;

public interface FieldChangeDAO extends GenericDAO<FieldChange> {

    FieldChange save(FieldChange fieldChange);

}
