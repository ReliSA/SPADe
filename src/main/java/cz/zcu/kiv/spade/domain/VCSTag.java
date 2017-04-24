package cz.zcu.kiv.spade.domain;

import cz.zcu.kiv.spade.domain.abstracts.DescribedEntity;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "tag")
public class VCSTag extends DescribedEntity {

    public VCSTag() {
        super();
    }
}
