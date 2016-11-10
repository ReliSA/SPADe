package cz.zcu.kiv.spade.domain;

import cz.zcu.kiv.spade.domain.enums.ProgramClass;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.util.Collection;
import java.util.Date;

@Entity
public class DevelopmentProgram extends Project {

    private ProgramClass programClass;

    public DevelopmentProgram() {
        super();
    }

    @Enumerated(value = EnumType.STRING)
    public ProgramClass getProgramClass() {
        return programClass;
    }

    public void setProgramClass(ProgramClass programClass) {
        this.programClass = programClass;
    }

}
