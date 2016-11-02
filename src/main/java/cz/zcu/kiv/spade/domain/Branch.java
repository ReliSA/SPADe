package cz.zcu.kiv.spade.domain;

import cz.zcu.kiv.spade.domain.abstracts.NamedEntity;

import javax.persistence.Entity;

@Entity
public class Branch extends NamedEntity {

    private boolean isMain;

    public Branch() {
    }

    public Branch(long id, String externalId, String name, boolean isMain) {
        super(id, externalId, name);
        this.isMain = isMain;
    }

    public boolean getIsMain() {
        return isMain;
    }

    public void setIsMain(boolean isMain) {
        this.isMain = isMain;
    }
}
