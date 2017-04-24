package cz.zcu.kiv.spade.domain;

import cz.zcu.kiv.spade.domain.abstracts.BaseEntity;
import cz.zcu.kiv.spade.domain.enums.ResolutionClass;
import cz.zcu.kiv.spade.domain.enums.ResolutionSuperClass;

import javax.persistence.*;

@Entity
@Table(name = "resolution_classification")
public class ResolutionClassification extends BaseEntity {

    private ResolutionClass aClass;
    private ResolutionSuperClass superClass;

    public ResolutionClassification() {
        super();
    }

    public ResolutionClassification(ResolutionClass aClass) {
        super();
        this.setaClass(aClass);
    }

    @Column(name = "class")
    @Enumerated(EnumType.STRING)
    public ResolutionClass getaClass() {
        return aClass;
    }

    public void setaClass(ResolutionClass aClass) {
        this.aClass = aClass;
        if (aClass == ResolutionClass.UNASSIGNED)
            this.superClass = ResolutionSuperClass.UNASSIGNED;
        if (aClass == ResolutionClass.INVALID || aClass == ResolutionClass.DUPLICATE
                || aClass == ResolutionClass.WONTFIX || aClass == ResolutionClass.FIXED
                || aClass == ResolutionClass.WORKSASDESIGNED || aClass == ResolutionClass.FINISHED)
            this.superClass = ResolutionSuperClass.FINISHED;
        if (aClass == ResolutionClass.WORKSFORME || aClass == ResolutionClass.INCOMPLETE
                || aClass == ResolutionClass.UNFINISHED)
            this.superClass = ResolutionSuperClass.UNFINISHED;
    }

    @Enumerated(EnumType.STRING)
    public ResolutionSuperClass getSuperClass() {
        return superClass;
    }

    public void setSuperClass(ResolutionSuperClass superClass) {
        this.superClass = superClass;
    }
}
