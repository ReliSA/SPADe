package cz.zcu.kiv.spade.domain;

import cz.zcu.kiv.spade.domain.abstracts.DescribedEntity;
import cz.zcu.kiv.spade.domain.enums.WorkUnitSeverityClass;
import cz.zcu.kiv.spade.domain.enums.WorkUnitSeveritySuperclass;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

@Entity
@Table(name = "severity")
public class WorkUnitSeverity extends DescribedEntity {

    private WorkUnitSeverityClass severityClass;
    private WorkUnitSeveritySuperclass severitySuperclass;

    public WorkUnitSeverity() {
        super();
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
