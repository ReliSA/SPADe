package cz.zcu.kiv.spade.domain.abstracts;

import cz.zcu.kiv.spade.domain.Configuration;
import cz.zcu.kiv.spade.domain.Project;

import javax.persistence.FetchType;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToOne;
import java.util.Date;

@MappedSuperclass
public class DefinedProjectSegment extends ProjectSegment {

    protected Date created;
    protected Configuration configuration;

    public DefinedProjectSegment() {
    }

    public DefinedProjectSegment(long id, String externalId, String name, String description, Project project, Date startDate, Date endDate, Date created, Configuration configuration) {
        super(id, externalId, name, description, project, startDate, endDate);
        this.created = created;
        this.configuration = configuration;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    @OneToOne(fetch = FetchType.LAZY)
    public Configuration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }
}
