package cz.zcu.kiv.ppicha.spade.domain.abstracts;

import javax.persistence.*;
import java.util.Date;

@MappedSuperclass
public abstract class ProjectSegment extends DescribedEntity {

    protected Date startDate;
    protected Date endDate;

    public ProjectSegment() {
    }

    public ProjectSegment(long id, String externalId, String name, String description,
                          Date startDate, Date endDate) {
        super(id, externalId, name, description);
        this.startDate = startDate;
        this.endDate = endDate;
    }

    @Column(nullable = false)
    @Temporal(TemporalType.DATE)
    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    @Column(nullable = false)
    @Temporal(TemporalType.DATE)
    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

}
