package cz.zcu.kiv.spade.domain.abstracts;

import cz.zcu.kiv.spade.domain.Person;

import javax.persistence.*;
import java.util.Date;

@MappedSuperclass
public abstract class AuthoredEntity extends DescribedEntity {

    protected Date created;
    protected Person author;

    public AuthoredEntity() {
        super();
    }

    @Temporal(TemporalType.TIMESTAMP)
    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    @JoinColumn(name = "authorId")
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    public Person getAuthor() {
        return author;
    }

    public void setAuthor(Person author) {
        this.author = author;
    }

}
