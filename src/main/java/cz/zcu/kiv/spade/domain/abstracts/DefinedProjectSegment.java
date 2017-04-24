package cz.zcu.kiv.spade.domain.abstracts;

import cz.zcu.kiv.spade.domain.Commit;

import javax.persistence.*;
import java.util.Date;

@MappedSuperclass
public abstract class DefinedProjectSegment extends ProjectSegment {

    private Date created;
    protected Commit commit;

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
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    public Commit getCommit() {
        return commit;
    }

    public void setCommit(Commit commit) {
        this.commit = commit;
    }
}
