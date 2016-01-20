package cz.zcu.kiv.ppicha.spade.domain;

import cz.zcu.kiv.ppicha.spade.domain.abstracts.NamedAndDescribedEntity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class WorkItem extends NamedAndDescribedEntity {

    protected Date created;
    protected Person author;
    protected String url;

    public WorkItem() {
    }

    public WorkItem(long id, long externalId, String name, String description, Date created, Person author, String url) {
        super(id, externalId, name, description);
        this.created = created;
        this.author = author;
        this.url = url;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false, updatable = false)
    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    public Person getAuthor() {
        return author;
    }

    public void setAuthor(Person author) {
        this.author = author;
    }

    @Column(nullable = false)
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        WorkItem workItem = (WorkItem) o;

        if (created != null ? !created.equals(workItem.created) : workItem.created != null) return false;
        if (author != null ? !author.equals(workItem.author) : workItem.author != null) return false;
        return !(url != null ? !url.equals(workItem.url) : workItem.url != null);

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (created != null ? created.hashCode() : 0);
        result = 31 * result + (author != null ? author.hashCode() : 0);
        result = 31 * result + (url != null ? url.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "WorkItem{" +
                "created=" + created +
                ", author=" + author +
                ", url='" + url + '\'' +
                '}';
    }
}
