package cz.zcu.kiv.ppicha.spade.domain;

import cz.zcu.kiv.ppicha.spade.domain.enums.WorkUnitPrioritySuperclass;
import cz.zcu.kiv.ppicha.spade.domain.enums.WorkUnitSeveritySuperclass;
import cz.zcu.kiv.ppicha.spade.domain.enums.WorkUnitStatusSuperclass;

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
    private double estimatedTime;
    private double spentTime;
    private double estimatedRemaining;
    private Date startDate;
    private Date dueDate;
    private WorkUnitStatus status;
    private int progress;
    private Person assignee;
    private Set<WorkItem> prerequisites;
    private String category;
    private Set<Configuration> configurations;

    public WorkUnit() {
        this.prerequisites = new LinkedHashSet<>();
        this.configurations = new LinkedHashSet<>();
    }

    public WorkUnit(long id, String externalId, String name, String description, Date created, Identity author, String url,
                    int number, WorkUnitType type, WorkUnitPriority priority, WorkUnitSeverity severity,
                    double estimatedTime, double spentTime, double estimatedRemaining, Date startDate, Date dueDate, WorkUnitStatus status, int progress,
                    Person assignee, Set<WorkItem> prerequisites, String category, Set<Configuration> configurations) {
        super(id, externalId, name, description, created, author, url);
        this.number = number;
        this.type = type;
        this.priority = priority;
        this.severity = severity;
        this.estimatedTime = estimatedTime;
        this.spentTime = spentTime;
        this.estimatedRemaining = estimatedRemaining;
        this.startDate = startDate;
        this.dueDate = dueDate;
        this.status = status;
        this.progress = progress;
        this.assignee = assignee;
        this.prerequisites = prerequisites;
        this.category = category;
        this.configurations =configurations;
    }

    @Column(nullable = false, updatable = false)
    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public WorkUnitType getType() {
        return type;
    }

    public void setType(WorkUnitType type) {
        this.type = type;
    }

    public WorkUnitPriority getPriority() {
        return priority;
    }

    public void setPriority(WorkUnitPriority priority) {
        this.priority = priority;
    }

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

    public double getEstimatedRemaining() {
        return estimatedRemaining;
    }

    public void setEstimatedRemaining(double estimatedRemaining) {
        this.estimatedRemaining = estimatedRemaining;
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

    public WorkUnitStatus getStatus() {
        return status;
    }

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
    public Set<WorkItem> getPrerequisites() {
        return prerequisites;
    }

    public void setPrerequisites(Set<WorkItem> prerequisites) {
        this.prerequisites = prerequisites;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @ManyToMany
    @JoinTable(name = "WorkUnit_Configuration", joinColumns = @JoinColumn(name = "work_unit_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "configuration_id", referencedColumnName = "id"))
    public Set<Configuration> getConfigurations() {
        return configurations;
    }

    public void setConfigurations(Set<Configuration> configurations) {
        this.configurations = configurations;
    }
}