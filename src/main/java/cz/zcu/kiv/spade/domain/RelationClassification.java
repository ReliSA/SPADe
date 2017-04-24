package cz.zcu.kiv.spade.domain;

import cz.zcu.kiv.spade.domain.abstracts.BaseEntity;
import cz.zcu.kiv.spade.domain.enums.RelationClass;
import cz.zcu.kiv.spade.domain.enums.RelationSuperClass;

import javax.persistence.*;

@Entity
@Table(name = "wu_relation_classification")
public class RelationClassification extends BaseEntity {

    private RelationClass aClass;
    private RelationSuperClass superClass;

    public RelationClassification() {
        super();
    }

    public RelationClassification(RelationClass aClass) {
        super();
        this.setaClass(aClass);
    }

    @Column(name = "class")
    @Enumerated(EnumType.STRING)
    public RelationClass getaClass() {
        return aClass;
    }

    public void setaClass(RelationClass aClass) {
        this.aClass = aClass;
        if (aClass == RelationClass.UNASSIGNED)
            this.superClass = RelationSuperClass.UNASSIGNED;
        if (aClass == RelationClass.DUPLICATES || aClass == RelationClass.DUPLICATEDBY
                || aClass == RelationClass.COPIEDFROM || aClass == RelationClass.COPIEDBY)
            this.superClass = RelationSuperClass.SIMILARITY;
        if (aClass == RelationClass.RELATESTO)
            this.superClass = RelationSuperClass.GENERAL;
        if (aClass == RelationClass.BLOCKS || aClass == RelationClass.BLOCKEDBY
                || aClass == RelationClass.PRECEDES || aClass == RelationClass.FOLLOWS)
            this.superClass = RelationSuperClass.TEMPORAL;
        if (aClass == RelationClass.CHILDOF || aClass == RelationClass.PARENTOF)
            this.superClass = RelationSuperClass.HIERARCHICAL;
        if (aClass == RelationClass.CAUSES || aClass == RelationClass.CAUSEDBY
                || aClass == RelationClass.RESOLVES || aClass == RelationClass.RESOLVEDBY)
            this.superClass = RelationSuperClass.CAUSAL;
    }

    @Enumerated(EnumType.STRING)
    public RelationSuperClass getSuperClass() {
        return superClass;
    }

    public void setSuperClass(RelationSuperClass superClass) {
        this.superClass = superClass;
    }
}
