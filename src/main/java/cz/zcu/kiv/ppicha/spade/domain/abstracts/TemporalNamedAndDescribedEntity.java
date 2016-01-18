package cz.zcu.kiv.ppicha.spade.domain.abstracts;

import cz.zcu.kiv.ppicha.spade.domain.Activity;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

@MappedSuperclass
public abstract class TemporalNamedAndDescribedEntity extends NamedAndDescribedEntity {

    protected Date startDate;
    protected Date endDate;

    public TemporalNamedAndDescribedEntity() {
    }

    public TemporalNamedAndDescribedEntity(long id, long externalId, String name, String description,
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        TemporalNamedAndDescribedEntity that = (TemporalNamedAndDescribedEntity) o;

        if (startDate != null ? !startDate.equals(that.startDate) : that.startDate != null) return false;
        return !(endDate != null ? !endDate.equals(that.endDate) : that.endDate != null);

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (startDate != null ? startDate.hashCode() : 0);
        result = 31 * result + (endDate != null ? endDate.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "TemporalNamedAndDescribedEntity{" +
                "startDate=" + startDate +
                ", endDate=" + endDate +
                '}';
    }
}
