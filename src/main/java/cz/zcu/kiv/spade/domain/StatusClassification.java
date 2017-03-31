package cz.zcu.kiv.spade.domain;

import cz.zcu.kiv.spade.domain.enums.StatusClass;
import cz.zcu.kiv.spade.domain.enums.StatusSuperClass;

import javax.persistence.*;

@Entity
@Table(name = "status_classification")
public class StatusClassification {
    private long id;
    private StatusClass aClass;
    private StatusSuperClass superClass;

    public StatusClassification() {
        aClass = StatusClass.NEW;
        superClass = StatusSuperClass.OPEN;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Column(name = "class")
    @Enumerated(EnumType.STRING)
    public StatusClass getaClass() {
        return aClass;
    }

    public void setaClass(StatusClass aClass) {
        this.aClass = aClass;
        if (aClass == StatusClass.NEW || aClass == StatusClass.ACCEPTED
                || aClass == StatusClass.IN_PROGRESS || aClass == StatusClass.RESOLVED
                || aClass == StatusClass.VERIFIED)
            this.superClass = StatusSuperClass.OPEN;
        if (aClass == StatusClass.DONE || aClass == StatusClass.INVALID)
            this.superClass = StatusSuperClass.CLOSED;
    }

    @Enumerated(EnumType.STRING)
    public StatusSuperClass getSuperClass() {
        return superClass;
    }

    public void setSuperClass(StatusSuperClass superClass) {
        this.superClass = superClass;
    }
}
