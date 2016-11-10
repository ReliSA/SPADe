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
        super();
    }

    @JoinColumn(name = "superProjectId")
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

}
