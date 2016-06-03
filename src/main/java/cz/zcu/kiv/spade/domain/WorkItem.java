package cz.zcu.kiv.spade.domain;

import cz.zcu.kiv.spade.domain.abstracts.AuthoredEntity;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import java.util.Date;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class WorkItem extends AuthoredEntity {

    protected String url;

    public WorkItem() {
    }

    public WorkItem(long id, String externalId, String name, String description, Date created, Identity author, String url) {
        super(id, externalId, name, description, created, author);
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}
