package cz.zcu.kiv.spade.domain;

import cz.zcu.kiv.spade.domain.enums.ProgramType;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import java.util.Collection;
import java.util.Date;

@Entity
public class DevelopmentProgram extends Project {

    private ProgramType type;

    public DevelopmentProgram() {
    }

    public DevelopmentProgram(long id, String externalId, String name, String description, DevelopmentProgram program, Date startDate, Date endDate,
                              Collection<Person> personnel, Collection<Configuration> configurations, ProgramType type) {
        super(id, externalId, name, description, program, startDate, endDate, personnel, configurations);
        this.type = type;
    }

    public ProgramType getType() {
        return type;
    }

    public void setType(ProgramType type) {
        this.type = type;
    }

}
