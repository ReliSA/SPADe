package cz.zcu.kiv.spade.domain;

import javax.persistence.*;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashSet;

@Entity
@Table(name = "work_unit")
public class WorkUnit extends WorkItem {

    private int number;
    private Priority priority;
    private Severity severity;
    private WorkUnitType type;
    private Status status;
    private Resolution resolution;
    private String category;
    private double estimatedTime;
    private double spentTime;
    private Date startDate;
    private Date dueDate;
    private int progress;
    private Person assignee;
    private Collection<Artifact> attachments;
    private Iteration iteration;
    private Phase phase;
    private Activity activity;
    private Project project;
    //private Collection<Person> watchers;

    public WorkUnit() {
        super();
        this.priority = new Priority();
        this.severity = new Severity();
        this.type = new WorkUnitType();
        this.status = new Status();
        this.resolution = new Resolution();
        this.attachments = new LinkedHashSet<>();
        //this.watchers = new LinkedHashSet<>();
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "priorityId")
    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "severityId")
    public Severity getSeverity() {
        return severity;
    }

    public void setSeverity(Severity severity) {
        this.severity = severity;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wuTypeId")
    public WorkUnitType getType() {
        return type;
    }

    public void setType(WorkUnitType type) {
        this.type = type;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "statusId")
    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @ManyToOne(fetch = FetchType.LAZY)
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
    @ManyToOne(fetch = FetchType.LAZY)
    public Person getAssignee() {
        return assignee;
    }

    public void setAssignee(Person assignee) {
        this.assignee = assignee;
    }

    @ManyToMany
    @JoinTable(name = "work_unit_attachment", joinColumns = @JoinColumn(name = "workUnitId", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "attachmentId", referencedColumnName = "id"))
    public Collection<Artifact> getAttachments() {
        return attachments;
    }

    public void setAttachments(Collection<Artifact> attachments) {
        this.attachments = attachments;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @JoinColumn(name = "iterationId")
    @ManyToOne(fetch = FetchType.LAZY)
    public Iteration getIteration() {
        return iteration;
    }

    public void setIteration(Iteration iteration) {
        this.iteration = iteration;
    }

    @JoinColumn(name = "phaseId")
    @ManyToOne(fetch = FetchType.LAZY)
    public Phase getPhase() {
        return phase;
    }

    public void setPhase(Phase phase) {
        this.phase = phase;
    }

    @JoinColumn(name = "activityId")
    @ManyToOne(fetch = FetchType.LAZY)
    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    @JoinColumn(name = "projectId")
    @ManyToOne(fetch = FetchType.LAZY)
    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
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