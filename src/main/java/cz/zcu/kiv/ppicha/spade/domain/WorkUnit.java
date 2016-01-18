package cz.zcu.kiv.ppicha.spade.domain;

import cz.zcu.kiv.ppicha.spade.domain.enums.WorkUnitPriority;
import cz.zcu.kiv.ppicha.spade.domain.enums.WorkUnitSeverity;
import cz.zcu.kiv.ppicha.spade.domain.enums.WorkUnitStatus;
import cz.zcu.kiv.ppicha.spade.domain.enums.WorkUnitType;

import javax.persistence.*;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
public class WorkUnit extends WorkItem {

    private int number;
    private WorkUnitType type;
    private WorkUnitPriority priority;
    private WorkUnitSeverity severity;
    private long estimatedTime;
    private long spentTime;
    private Date dueDate;
    private WorkUnitStatus status;
    private int progress;
    private Set<WorkUnit> subunits;
    private Set<WorkUnit> related;
    private Person assignee;
    private Set<WorkItem> prerequisites;
    private Set<Activity> previousActivities;
    private String category;

    public WorkUnit() {
        this.subunits = new LinkedHashSet<>();
        this.related = new LinkedHashSet<>();
        this.prerequisites = new LinkedHashSet<>();
        this.previousActivities = new LinkedHashSet<>();
    }

    public WorkUnit(long id, long externalId, String name, String description, Date created, Person author, String url,
                    int number, WorkUnitType type, WorkUnitPriority priority, WorkUnitSeverity severity,
                    long estimatedTime, long spentTime, Date dueDate, WorkUnitStatus status, int progress,
                    Set<WorkUnit> subunits, Set<WorkUnit> related, Person assignee, Set<WorkItem> prerequisites,
                    Set<Activity> previousActivities, String category) {
        super(id, externalId, name, description, created, author, url);
        this.number = number;
        this.type = type;
        this.priority = priority;
        this.severity = severity;
        this.estimatedTime = estimatedTime;
        this.spentTime = spentTime;
        this.dueDate = dueDate;
        this.status = status;
        this.progress = progress;
        this.subunits = subunits;
        this.related = related;
        this.assignee = assignee;
        this.prerequisites = prerequisites;
        this.previousActivities = previousActivities;
        this.category = category;
    }

    @Column(nullable = false, updatable = false)
    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    public WorkUnitType getType() {
        return type;
    }

    public void setType(WorkUnitType type) {
        this.type = type;
    }

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    public WorkUnitPriority getPriority() {
        return priority;
    }

    public void setPriority(WorkUnitPriority priority) {
        this.priority = priority;
    }

    @Enumerated(value = EnumType.STRING)
    public WorkUnitSeverity getSeverity() {
        return severity;
    }

    public void setSeverity(WorkUnitSeverity severity) {
        this.severity = severity;
    }

    public long getEstimatedTime() {
        return estimatedTime;
    }

    public void setEstimatedTime(long estimatedTime) {
        this.estimatedTime = estimatedTime;
    }

    public long getSpentTime() {
        return spentTime;
    }

    public void setSpentTime(long spentTime) {
        this.spentTime = spentTime;
    }

    @Temporal(TemporalType.DATE)
    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    @Column(nullable = false)
    public WorkUnitStatus getStatus() {
        return status;
    }

    @Enumerated(value = EnumType.STRING)
    public void setStatus(WorkUnitStatus status) {
        this.status = status;
    }

    @Column(nullable = false)
    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    @OneToMany
    public Set<WorkUnit> getSubunits() {
        return subunits;
    }

    public void setSubunits(Set<WorkUnit> subunits) {
        this.subunits = subunits;
    }

    @ManyToMany
    @JoinTable(name = "WorkUnit_Related", joinColumns = @JoinColumn(name = "WorkUnit", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "Related", referencedColumnName = "id"))
    public Set<WorkUnit> getRelated() {
        return related;
    }

    public void setRelated(Set<WorkUnit> related) {
        this.related = related;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    public Person getAssignee() {
        return assignee;
    }

    public void setAssignee(Person assignee) {
        this.assignee = assignee;
    }

    @ManyToMany
    @JoinTable(name = "WorkUnit_Prerequisite", joinColumns = @JoinColumn(name = "WorkUnit", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "Prerequisite", referencedColumnName = "id"))
    public Set<WorkItem> getPrerequisites() {
        return prerequisites;
    }

    public void setPrerequisites(Set<WorkItem> prerequisites) {
        this.prerequisites = prerequisites;
    }

    @ManyToMany
    @JoinTable(name = "WorkUnit_PreviousActivity", joinColumns = @JoinColumn(name = "WorkUnit", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "PreviousActivity", referencedColumnName = "id"))
    public Set<Activity> getPreviousActivities() {
        return previousActivities;
    }

    public void setPreviousActivities(Set<Activity> previousActivities) {
        this.previousActivities = previousActivities;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        WorkUnit workUnit = (WorkUnit) o;

        if (number != workUnit.number) return false;
        if (estimatedTime != workUnit.estimatedTime) return false;
        if (spentTime != workUnit.spentTime) return false;
        if (progress != workUnit.progress) return false;
        if (type != workUnit.type) return false;
        if (priority != workUnit.priority) return false;
        if (severity != workUnit.severity) return false;
        if (dueDate != null ? !dueDate.equals(workUnit.dueDate) : workUnit.dueDate != null) return false;
        if (status != workUnit.status) return false;
        if (subunits != null ? !subunits.equals(workUnit.subunits) : workUnit.subunits != null) return false;
        if (related != null ? !related.equals(workUnit.related) : workUnit.related != null) return false;
        if (assignee != null ? !assignee.equals(workUnit.assignee) : workUnit.assignee != null) return false;
        if (prerequisites != null ? !prerequisites.equals(workUnit.prerequisites) : workUnit.prerequisites != null)
            return false;
        if (previousActivities != null ? !previousActivities.equals(workUnit.previousActivities) : workUnit.previousActivities != null)
            return false;
        return !(category != null ? !category.equals(workUnit.category) : workUnit.category != null);

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + number;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (priority != null ? priority.hashCode() : 0);
        result = 31 * result + (severity != null ? severity.hashCode() : 0);
        result = 31 * result + (int) (estimatedTime ^ (estimatedTime >>> 32));
        result = 31 * result + (int) (spentTime ^ (spentTime >>> 32));
        result = 31 * result + (dueDate != null ? dueDate.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + progress;
        result = 31 * result + (subunits != null ? subunits.hashCode() : 0);
        result = 31 * result + (related != null ? related.hashCode() : 0);
        result = 31 * result + (assignee != null ? assignee.hashCode() : 0);
        result = 31 * result + (prerequisites != null ? prerequisites.hashCode() : 0);
        result = 31 * result + (previousActivities != null ? previousActivities.hashCode() : 0);
        result = 31 * result + (category != null ? category.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "WorkUnit{" +
                "number=" + number +
                ", type=" + type +
                ", priority=" + priority +
                ", severity=" + severity +
                ", estimatedTime=" + estimatedTime +
                ", spentTime=" + spentTime +
                ", dueDate=" + dueDate +
                ", status=" + status +
                ", progress=" + progress +
                ", subunits=" + subunits +
                ", related=" + related +
                ", assignee=" + assignee +
                ", prerequisites=" + prerequisites +
                ", previousActivities=" + previousActivities +
                ", category='" + category + '\'' +
                '}';
    }
}