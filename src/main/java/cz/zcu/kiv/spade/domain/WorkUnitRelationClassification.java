package cz.zcu.kiv.spade.domain;

import cz.zcu.kiv.spade.domain.enums.WorkUnitRelationClass;
import cz.zcu.kiv.spade.domain.enums.WorkUnitRelationSuperClass;

import javax.persistence.*;

@Entity
@Table(name = "wu_relation_classification")
public class WorkUnitRelationClassification {
    private long id;
    private WorkUnitRelationClass aClass;
    private WorkUnitRelationSuperClass superClass;

    public WorkUnitRelationClassification() {
        aClass = WorkUnitRelationClass.RELATES_TO;
        superClass = WorkUnitRelationSuperClass.GENERAL;
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
    public WorkUnitRelationClass getaClass() {
        return aClass;
    }

    public void setaClass(WorkUnitRelationClass aClass) {
        this.aClass = aClass;
        if (aClass == WorkUnitRelationClass.DUPLICATES || aClass == WorkUnitRelationClass.COPIED_FROM)
            this.superClass = WorkUnitRelationSuperClass.SIMILARITY;
        if (aClass == WorkUnitRelationClass.RELATES_TO)
            this.superClass = WorkUnitRelationSuperClass.GENERAL;
        if (aClass == WorkUnitRelationClass.BLOCKS || aClass == WorkUnitRelationClass.PRECEDES)
            this.superClass = WorkUnitRelationSuperClass.TEMPORAL;
        if (aClass == WorkUnitRelationClass.CHILD)
            this.superClass = WorkUnitRelationSuperClass.HIERARCHICAL;
        if (aClass == WorkUnitRelationClass.CAUSES || aClass == WorkUnitRelationClass.RESOLVES)
            this.superClass = WorkUnitRelationSuperClass.CAUSAL;
    }

    @Enumerated(EnumType.STRING)
    public WorkUnitRelationSuperClass getSuperClass() {
        return superClass;
    }

    public void setSuperClass(WorkUnitRelationSuperClass superClass) {
        this.superClass = superClass;
    }
}
