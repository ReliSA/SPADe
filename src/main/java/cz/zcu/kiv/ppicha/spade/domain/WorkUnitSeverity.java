package cz.zcu.kiv.ppicha.spade.domain;

import cz.zcu.kiv.ppicha.spade.domain.abstracts.DescribedEntity;
import cz.zcu.kiv.ppicha.spade.domain.enums.WorkUnitSeveritySuperclass;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

public class WorkUnitSeverity extends DescribedEntity {

    private WorkUnitSeveritySuperclass severitySuperclass;

    public WorkUnitSeverity() {
    }

    public WorkUnitSeverity(long id, String externalId, String name, String description, WorkUnitSeveritySuperclass severitySuperclass) {
        super(id, externalId, name, description);
        this.severitySuperclass = severitySuperclass;
    }

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    public WorkUnitSeveritySuperclass getSeveritySuperclass() {
        return severitySuperclass;
    }

    public void setSeveritySuperclass(WorkUnitSeveritySuperclass severitySuperclass) {
        this.severitySuperclass = severitySuperclass;
    }
}
