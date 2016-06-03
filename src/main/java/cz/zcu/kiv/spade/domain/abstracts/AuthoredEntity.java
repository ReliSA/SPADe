package cz.zcu.kiv.spade.domain.abstracts;

import cz.zcu.kiv.spade.domain.Identity;

import javax.persistence.*;
import java.util.Date;

@MappedSuperclass
public abstract class AuthoredEntity extends DescribedEntity {

    protected Date created;
    protected Identity author;

    public AuthoredEntity() {
    }

    public AuthoredEntity(long id, String externalId, String name, String description, Date created, Identity author) {
        super(id, externalId, name, description);
        this.created = created;
        this.author = author;
    }

    @Temporal(TemporalType.TIMESTAMP)
    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    public Identity getAuthor() {
        return author;
    }

    public void setAuthor(Identity author) {
        this.author = author;
    }

}
