package cz.zcu.kiv.spade.domain;

import cz.zcu.kiv.spade.domain.abstracts.NamedEntity;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "tag")
public class VCSTag extends NamedEntity {

    public VCSTag() {
        super();
    }
}
