package cz.zcu.kiv.spade.domain;

import cz.zcu.kiv.spade.domain.enums.ResolutionClass;
import cz.zcu.kiv.spade.domain.enums.ResolutionSuperClass;

import javax.persistence.*;

@Entity
@Table(name = "resolution_classification")
public class ResolutionClassification {
    private long id;
    private ResolutionClass aClass;
    private ResolutionSuperClass superClass;

    public ResolutionClassification() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public ResolutionClass getaClass() {
        return aClass;
    }

    public void setaClass(ResolutionClass aClass) {
        this.aClass = aClass;
        if (aClass == ResolutionClass.INVALID || aClass == ResolutionClass.DUPLICATE
                || aClass == ResolutionClass.WONT_FIX || aClass == ResolutionClass.FIXED
                || aClass == ResolutionClass.WORKS_AS_DESIGNED || aClass == ResolutionClass.FINISHED)
            this.superClass = ResolutionSuperClass.FINISHED;
        if (aClass == ResolutionClass.WORKS_FOR_ME || aClass == ResolutionClass.INCOMPLETE
                || aClass == ResolutionClass.UNFINISHED)
            this.superClass = ResolutionSuperClass.UNFINISHED;
    }

    public ResolutionSuperClass getSuperClass() {
        return superClass;
    }

    public void setSuperClass(ResolutionSuperClass superClass) {
        this.superClass = superClass;
    }
}
