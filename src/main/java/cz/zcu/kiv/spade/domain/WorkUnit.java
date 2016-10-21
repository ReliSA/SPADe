package cz.zcu.kiv.spade.domain;

import javax.persistence.*;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashSet;

@Entity
public class WorkUnit extends WorkItem {

    private int number;
    private WorkUnitPriority priority;
    private WorkUnitSeverity severity;
    private WorkUnitType type;
    private WorkUnitStatus status;
    private WorkUnitResolution resolution;
    private WorkUnitCategory category;
    private double estimatedTime;
    private double spentTime;
    private Date startDate;
    private Date dueDate;
    private int progress;
    private Person assignee;
    private Collection<WorkItem> prerequisites;
    private Iteration iteration;
    private Phase phase;
    private Activity activity;

    public WorkUnit() {
        this.prerequisites = new LinkedHashSet<>();
    }

    public WorkUnit(long id, String externalId, String name, String description, Date created, Identity author, String url,
                    int number, WorkUnitType type, WorkUnitPriority priority, WorkUnitSeverity severity,
                    double estimatedTime, double spentTime, Date startDate, Date dueDate, WorkUnitStatus status,
                    WorkUnitResolution resolution, int progress, Person assignee, Collection<WorkItem> prerequisites,
                    WorkUnitCategory category, Iteration iteration, Phase phase, Activity activity) {
        super(id, externalId, name, description, created, author, url);
        this.number = number;
        this.priority = priority;
        this.severity = severity;
        this.type = type;
        this.status = status;
        this.resolution = resolution;
        this.category = category;
        this.estimatedTime = estimatedTime;
        this.spentTime = spentTime;
        this.startDate = startDate;
        this.dueDate = dueDate;
        this.progress = progress;
        this.assignee = assignee;
        this.prerequisites = prerequisites;
        this.iteration = iteration;
        this.phase = phase;
        this.activity = activity;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    @OneToOne(fetch = FetchType.LAZY)
    public WorkUnitType getType() {
        return type;
    }

    public void setType(WorkUnitType type) {
        this.type = type;
    }

    @OneToOne(fetch = FetchType.LAZY)
    public WorkUnitPriority getPriority() {
        return priority;
    }

    public void setPriority(WorkUnitPriority priority) {
        this.priority = priority;
    }

    @OneToOne(fetch = FetchType.LAZY)
    public WorkUnitSeverity getSeverity() {
        return severity;
    }

    public void setSeverity(WorkUnitSeverity severity) {
        this.severity = severity;
    }

    public double getEstimatedTime() {
        return estimatedTime;
    }

    public void setEstimatedTime(double estimatedTime) {
        this.estimatedTime = estimatedTime;
    }

    public double getSpentTime() {
        return spentTime;
    }

    public void setSpentTime(double spentTime) {
        this.spentTime = spentTime;
    }

    @Temporal(TemporalType.DATE)
    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    @Temporal(TemporalType.DATE)
    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    @OneToOne(fetch = FetchType.LAZY)
    public WorkUnitStatus getStatus() {
        return status;
    }

    public void setStatus(WorkUnitStatus status) {
        this.status = status;
    }

    @OneToOne(fetch = FetchType.LAZY)
    public WorkUnitResolution getResolution() {
        return resolution;
    }

    public void setResolution(WorkUnitResolution resolution) {
        this.resolution = resolution;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    public Person getAssignee() {
        return assignee;
    }

    public void setAssignee(Person assignee) {
        this.assignee = assignee;
    }

    @ManyToMany
    @JoinTable(name = "WorkUnit_Prerequisite", joinColumns = @JoinColumn(name = "work_unit_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "prerequisite_id", referencedColumnName = "id"))
    public Collection<WorkItem> getPrerequisites() {
        return prerequisites;
    }

    public void setPrerequisites(Collection<WorkItem> prerequisites) {
        this.prerequisites = prerequisites;
    }

    @OneToOne(fetch = FetchType.LAZY)
    public WorkUnitCategory getCategory() {
        return category;
    }

    public void setCategory(WorkUnitCategory category) {
        this.category = category;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    public Iteration getIteration() {
        return iteration;
    }

    public void setIteration(Iteration iteration) {
        this.iteration = iteration;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    public Phase getPhase() {
        return phase;
    }

    public void setPhase(Phase phase) {
        this.phase = phase;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }
}