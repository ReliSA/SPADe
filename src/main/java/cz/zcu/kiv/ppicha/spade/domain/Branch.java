package cz.zcu.kiv.ppicha.spade.domain;

import cz.zcu.kiv.ppicha.spade.domain.abstracts.DescribedEntity;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class Branch extends DescribedEntity {

    private boolean isMain;

    public Branch() {
    }

    public Branch(long id, String externalId, String name, String description, boolean isMain) {
        super(id, externalId, name, description);
        this.isMain = isMain;
    }

    @Column(nullable = false, updatable = false)
    public boolean getIsMain() {
        return isMain;
    }

    public void setIsMain(boolean isMain) {
        this.isMain = isMain;
    }

}
