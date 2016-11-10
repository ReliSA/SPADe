package cz.zcu.kiv.spade.domain;

import cz.zcu.kiv.spade.domain.abstracts.AuthoredEntity;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

@Entity
@Table(name = "work_item")
@Inheritance(strategy = InheritanceType.JOINED)
public class WorkItem extends AuthoredEntity {

    protected String url;

    public WorkItem() {
        super();
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}
