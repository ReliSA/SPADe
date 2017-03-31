package cz.zcu.kiv.spade.domain;

import cz.zcu.kiv.spade.domain.enums.WorkUnitTypeClass;

import javax.persistence.*;

@Entity
@Table(name = "wu_type_classification")
public class WorkUnitTypeClassification {
    private long id;
    private WorkUnitTypeClass aClass;

    public WorkUnitTypeClassification() {
        aClass = WorkUnitTypeClass.TASK;
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
    public WorkUnitTypeClass getaClass() {
        return aClass;
    }

    public void setaClass(WorkUnitTypeClass aClass) {
        this.aClass = aClass;
    }
}
