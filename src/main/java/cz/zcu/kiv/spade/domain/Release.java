package cz.zcu.kiv.spade.domain;

import java.util.Collection;
import java.util.Date;
import java.util.Set;

public class Release extends Configuration {

    public Release() {
    }

    public Release(long id, String externalId, String name, String description, int number, Date created, Identity author, Collection<WorkItemChange> changes, WorkItemChange lastWorkItemChange, boolean isRevision, Collection<Artifact> artifacts, Set<WorkUnit> workUnits, Branch branch, Collection<String> tag) {
        super(id, externalId, name, description, number, created, author, changes, lastWorkItemChange, isRevision, artifacts, workUnits, branch, tag);
    }
}
