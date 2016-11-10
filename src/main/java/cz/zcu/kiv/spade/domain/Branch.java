package cz.zcu.kiv.spade.domain;

import cz.zcu.kiv.spade.domain.abstracts.NamedEntity;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "branch")
public class Branch extends NamedEntity {

    private boolean isMain;

    public Branch() {
        super();
    }

    public boolean getIsMain() {
        return isMain;
    }

    public void setIsMain(boolean isMain) {
        this.isMain = isMain;
    }
}
