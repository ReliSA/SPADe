package cz.zcu.kiv.spade.domain;

import cz.zcu.kiv.spade.domain.abstracts.DescribedEntity;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "identity")
public class Identity extends DescribedEntity {

    private String email = "";

    public Identity() {
        super();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
