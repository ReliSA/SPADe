package cz.zcu.kiv.spade.domain;

import cz.zcu.kiv.spade.domain.abstracts.BaseEntity;
import cz.zcu.kiv.spade.domain.enums.StatusClass;
import cz.zcu.kiv.spade.domain.enums.StatusSuperClass;

import javax.persistence.*;

@Entity
@Table(name = "status_classification")
public class StatusClassification extends BaseEntity {

    private StatusClass aClass;
    private StatusSuperClass superClass;

    public StatusClassification() {
        super();
        aClass = StatusClass.NEW;
        superClass = StatusSuperClass.OPEN;
    }

    public StatusClassification(StatusClass aClass) {
        super();
        this.setAClass(aClass);
    }

    @Column(name = "class")
    @Enumerated(EnumType.STRING)
    public StatusClass getAClass() {
        return aClass;
    }

    public void setAClass(StatusClass aClass) {
        this.aClass = aClass;
        if (aClass == StatusClass.UNASSIGNED)
            this.superClass = StatusSuperClass.UNASSIGNED;
        if (aClass == StatusClass.NEW || aClass == StatusClass.ACCEPTED
                || aClass == StatusClass.INPROGRESS || aClass == StatusClass.RESOLVED
                || aClass == StatusClass.VERIFIED || aClass == StatusClass.OPEN)
            this.superClass = StatusSuperClass.OPEN;
        if (aClass == StatusClass.DONE || aClass == StatusClass.CLOSED
                || aClass == StatusClass.INVALID || aClass == StatusClass.DELETED)
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
