package cz.zcu.kiv.spade.domain.abstracts;

import cz.zcu.kiv.spade.domain.Project;

import javax.persistence.*;
import java.util.Date;

@MappedSuperclass
public abstract class ProjectSegment extends DescribedEntity {

    protected Project project;
    protected Date startDate;
    protected Date endDate;

    public ProjectSegment() {
    }

    public ProjectSegment(long id, String externalId, String name, String description,
                          Project project, Date startDate, Date endDate) {
        super(id, externalId, name, description);
        this.project = project;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    @Temporal(TemporalType.DATE)
    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    @Temporal(TemporalType.DATE)
    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    @Override
    public String toString() {
        return super.toString() +
                "Start date: " + startDate + "\n" +
                "End date: " + endDate + "\n" +
                "Project: " + project + "\n";

    }
}
