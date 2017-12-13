package cz.zcu.kiv.spade.domain;

import cz.zcu.kiv.spade.domain.abstracts.BaseEntity;
import cz.zcu.kiv.spade.domain.enums.SeverityClass;
import cz.zcu.kiv.spade.domain.enums.SeveritySuperClass;

import javax.persistence.*;

@Entity
@Table(name = "severity_classification")
public class SeverityClassification extends BaseEntity {

    private SeverityClass aClass;
    private SeveritySuperClass superClass;

    public SeverityClassification() {
        super();
    }

    public SeverityClassification(SeverityClass aClass) {
        super();
        this.setAClass(aClass);
    }

    @Column(name = "class")
    @Enumerated(EnumType.STRING)
    public SeverityClass getAClass() {
        return aClass;
    }

    public void setAClass(SeverityClass aClass) {
        this.aClass = aClass;
        if (aClass == SeverityClass.UNASSIGNED)
            this.superClass = SeveritySuperClass.UNASSIGNED;
        if (aClass == SeverityClass.TRIVIAL || aClass == SeverityClass.MINOR)
            this.superClass = SeveritySuperClass.MINOR;
        if (aClass == SeverityClass.NORMAL)
            this.superClass = SeveritySuperClass.NORMAL;
        if (aClass == SeverityClass.MAJOR || aClass == SeverityClass.CRITICAL)
            this.superClass = SeveritySuperClass.MAJOR;
    }

    @Enumerated(EnumType.STRING)
    public SeveritySuperClass getSuperClass() {
        return superClass;
    }

    public void setSuperClass(SeveritySuperClass superClass) {
        this.superClass = superClass;
    }
}
