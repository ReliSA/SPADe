package cz.zcu.kiv.spade.domain;

import cz.zcu.kiv.spade.domain.abstracts.DescribedEntity;
import cz.zcu.kiv.spade.domain.enums.WorkUnitSeverityClass;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Entity
public class WorkUnitSeverity extends DescribedEntity {

    private WorkUnitSeverityClass severityClass;

    public WorkUnitSeverity() {
    }

    public WorkUnitSeverity(long id, String externalId, String name, String description, WorkUnitSeverityClass severityClass) {
        super(id, externalId, name, description);
        this.severityClass = severityClass;
    }

    @Enumerated(value = EnumType.STRING)
    public WorkUnitSeverityClass getSeverityClass() {
        return severityClass;
    }

    public void setSeverityClass(WorkUnitSeverityClass severitySuperclass) {
        this.severityClass = severitySuperclass;
    }
}
