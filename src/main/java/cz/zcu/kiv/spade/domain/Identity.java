package cz.zcu.kiv.spade.domain;

import cz.zcu.kiv.spade.domain.abstracts.DescribedEntity;

import javax.persistence.Entity;

@Entity
public class Identity extends DescribedEntity {

    private String email;

    public Identity() {
    }

    public Identity(long id, String externalId, String name, String description) {
        super(id, externalId, name, description);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
