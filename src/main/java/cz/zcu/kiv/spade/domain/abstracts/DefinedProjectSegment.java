package cz.zcu.kiv.spade.domain.abstracts;

import cz.zcu.kiv.spade.domain.Configuration;

import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToOne;
import java.util.Date;

@MappedSuperclass
public abstract class DefinedProjectSegment extends ProjectSegment {

    protected Date created;
    protected Configuration configuration;

    public DefinedProjectSegment() {
        super();
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    @JoinColumn(name = "configurationId")
    @OneToOne(fetch = FetchType.LAZY)
    public Configuration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }
}
