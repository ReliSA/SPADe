package cz.zcu.kiv.spade.domain;

import cz.zcu.kiv.spade.domain.enums.Category;

import javax.persistence.*;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashSet;

@Entity
@Table(name = "work_unit")
@DiscriminatorValue("WORK_UNIT")
public class WorkUnit extends WorkItem {

    private int number;
    private Priority priority;
    private Severity severity;
    private WorkUnitType type;
    private Status status;
    private Resolution resolution;
    private Collection<Category> categories;
    private double estimatedTime;
    private double spentTime;
    private Date startDate;
    private Date dueDate;
    private int progress;
    private Person assignee;
    private Iteration iteration;
    private Phase phase;
    private Activity activity;
    //private Collection<Person> watchers;

    public WorkUnit() {
        super();
        this.categories = new LinkedHashSet<>();
        //this.watchers = new LinkedHashSet<>();
    }

    public WorkUnit(int number) {
        super();
        this.categories = new LinkedHashSet<>();
        this.number = number;
        //this.watchers = new LinkedHashSet<>();
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "priorityId")
    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "severityId")
    public Severity getSeverity() {
        return severity;
    }

    public void setSeverity(Severity severity) {
        this.severity = severity;
    }

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "wuTypeId")
    public WorkUnitType getType() {
        return type;
    }

    public void setType(WorkUnitType type) {
        this.type = type;
    }

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "statusId")
    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "resolutionId")
    public Resolution getResolution() {
        return resolution;
    }

    public void setResolution(Resolution resolution) {
        this.resolution = resolution;
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

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    @JoinColumn(name = "assigneeId")
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    public Person getAssignee() {
        return assignee;
    }

    public void setAssignee(Person assignee) {
        this.assignee = assignee;
    }

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "work_unit_category", joinColumns = @JoinColumn(name = "workUnitId", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "categoryId", referencedColumnName = "id"))
    public Collection<Category> getCategories() {
        return categories;
    }

    public void setCategories(Collection<Category> categories) {
        this.categories = categories;
    }

    @JoinColumn(name = "iterationId")
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    public Iteration getIteration() {
        return iteration;
    }

    public void setIteration(Iteration iteration) {
        this.iteration = iteration;
    }

    @JoinColumn(name = "phaseId")
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    public Phase getPhase() {
        return phase;
    }

    public void setPhase(Phase phase) {
        this.phase = phase;
    }

    @JoinColumn(name = "activityId")
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    /*@ManyToMany
    @JoinTable(name = "work_unit_watcher", joinColumns = @JoinColumn(name = "workUnitId", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "watcherId", referencedColumnName = "id"))
    public Collection<Person> getWatchers() {
        return watchers;
    }

    public void setWatchers(Collection<Person> watchers) {
        this.watchers = watchers;
    }*/
}