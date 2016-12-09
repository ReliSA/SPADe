package cz.zcu.kiv.spade.domain;

import javax.persistence.*;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashSet;

@Entity
@Table(name = "work_unit")
public class WorkUnit extends WorkItem {

    private int number;
    private String priority;
    private String severity;
    private String type;
    private String status;
    private String resolution;
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
        this.priority = "unassigned";
        this.severity = "unassigned";
        this.type = "unassigned";
        this.status = "unassigned";
        this.resolution = "unassigned";
        this.attachments = new LinkedHashSet<>();
        //this.watchers = new LinkedHashSet<>();
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
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