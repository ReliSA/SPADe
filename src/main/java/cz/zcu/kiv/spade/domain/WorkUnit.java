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
    private Collection<Artifact> attachments;
    private Iteration iteration;
    private Phase phase;
    private Activity activity;
    private Project project;
    private Collection<Person> watchers;

    public WorkUnit() {
        super();
        this.attachments = new LinkedHashSet<>();
        this.watchers = new LinkedHashSet<>();
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
    @JoinTable(name = "WorkUnit_Attachment", joinColumns = @JoinColumn(name = "work_unit_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "artifact_id", referencedColumnName = "id"))
    public Collection<Artifact> getAttachments() {
        return attachments;
    }

    public void setAttachments(Collection<Artifact> attachments) {
        this.attachments = attachments;
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

    @ManyToOne(fetch = FetchType.LAZY)
    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    @ManyToMany
    @JoinTable(name = "WorkUnit_Watcher", joinColumns = @JoinColumn(name = "work_unit_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "person_id", referencedColumnName = "id"))
    public Collection<Person> getWatchers() {
        return watchers;
    }

    public void setWatchers(Collection<Person> watchers) {
        this.watchers = watchers;
    }
}