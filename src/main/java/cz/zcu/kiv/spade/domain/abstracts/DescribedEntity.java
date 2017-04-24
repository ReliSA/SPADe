package cz.zcu.kiv.spade.domain.abstracts;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class DescribedEntity extends NamedEntity {

    protected String description = "";

    public DescribedEntity() {
        super();
    }

    @Column(columnDefinition = "longtext")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
