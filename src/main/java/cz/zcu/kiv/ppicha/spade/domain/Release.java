package cz.zcu.kiv.ppicha.spade.domain;

import java.util.Date;
import java.util.Set;

public class Release extends Configuration {

    public Release() {
    }

    public Release(long id, String externalId, String name, String description, int number, Date created, Identity author, Set<WorkItemChange> changes, WorkItemChange lastWorkItemChange, boolean isRevision, Set<Artifact> artifacts, Set<WorkUnit> workUnits, Branch branch, String tag) {
        super(id, externalId, name, description, number, created, author, changes, lastWorkItemChange, isRevision, artifacts, workUnits, branch, tag);
    }
}
