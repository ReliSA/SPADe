package cz.zcu.kiv.ppicha.spade.domain;

import cz.zcu.kiv.ppicha.spade.domain.abstracts.AuthoredEntity;

import javax.persistence.*;
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

    @Column(nullable = false)
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}
