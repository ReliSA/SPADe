package cz.zcu.kiv.spade.domain;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "committed_configuration")
@DiscriminatorValue("COMMITTED")
public class CommittedConfiguration extends Configuration {

    private Date committed;

    public CommittedConfiguration() {
        super();
    }

    @Temporal(TemporalType.TIMESTAMP)
    public Date getCommitted() {
        return committed;
    }

    public void setCommitted(Date committed) {
        this.committed = committed;
    }
}
