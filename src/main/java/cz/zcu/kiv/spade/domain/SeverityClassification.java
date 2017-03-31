package cz.zcu.kiv.spade.domain;

import cz.zcu.kiv.spade.domain.abstracts.BaseEntity;
import cz.zcu.kiv.spade.domain.enums.SeverityClass;
import cz.zcu.kiv.spade.domain.enums.SeveritySuperClass;

import javax.persistence.*;

@Entity
@Table(name = "severity_classification")
public class SeverityClassification extends BaseEntity {
    private long id;
    private SeverityClass aClass;
    private SeveritySuperClass superClass;

    public SeverityClassification() {
        super();
        aClass = SeverityClass.NORMAL;
        superClass = SeveritySuperClass.NORMAL;
    }

    @Column(name = "class")
    @Enumerated(EnumType.STRING)
    public SeverityClass getaClass() {
        return aClass;
    }

    public void setaClass(SeverityClass aClass) {
        this.aClass = aClass;
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
