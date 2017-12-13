package cz.zcu.kiv.spade.domain;

import cz.zcu.kiv.spade.domain.abstracts.DescribedEntity;

import javax.persistence.*;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashSet;

@Entity
@Table(name = "work_item")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "workItemType")
public class WorkItem extends DescribedEntity {

    private Date created;
    protected Person author;
    protected String url = "";
    private Collection<WorkItemRelation> relatedItems;

    public WorkItem() {
        super();
        relatedItems = new LinkedHashSet<>();
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "leftItemId")
    public Collection<WorkItemRelation> getRelatedItems() {
        return relatedItems;
    }

    public void setRelatedItems(Collection<WorkItemRelation> relatedItems) {
        this.relatedItems = relatedItems;
    }
}
