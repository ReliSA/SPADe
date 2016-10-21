package cz.zcu.kiv.spade.domain;

import cz.zcu.kiv.spade.domain.abstracts.DescribedEntity;
import cz.zcu.kiv.spade.domain.enums.WorkUnitSeverityClass;
import cz.zcu.kiv.spade.domain.enums.WorkUnitSeveritySuperclass;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Entity
public class WorkUnitSeverity extends DescribedEntity {

    private WorkUnitSeverityClass severityClass;
    private WorkUnitSeveritySuperclass severitySuperclass;

    public WorkUnitSeverity() {
    }

    public WorkUnitSeverity(long id, String externalId, String name, String description, WorkUnitSeverityClass severityClass, WorkUnitSeveritySuperclass severitySuperclass) {
        super(id, externalId, name, description);
        this.severityClass = severityClass;
        this.severitySuperclass = severitySuperclass;
    }

    @Enumerated(value = EnumType.STRING)
    public WorkUnitSeverityClass getSeverityClass() {
        return severityClass;
    }

    public void setSeverityClass(WorkUnitSeverityClass severityClass) {
        this.severityClass = severityClass;
    }

    @Enumerated(value = EnumType.STRING)
    public WorkUnitSeveritySuperclass getSeveritySuperclass() {
        return severitySuperclass;
    }

    public void setSeveritySuperclass(WorkUnitSeveritySuperclass severitySuperclass) {
        this.severitySuperclass = severitySuperclass;
    }
}
