package cz.zcu.kiv.spade.pumps.abstracts;

import cz.zcu.kiv.spade.domain.abstracts.ProjectSegment;
import cz.zcu.kiv.spade.pumps.DataPump;

import java.util.ArrayList;
import java.util.Collection;

/**
 * a generic VCS tool pump
 * @param <RootObjectType> the main object of the given tool mining API
 *
 * @author Petr Pícha
 */
public abstract class VCSPump<RootObjectType> extends DataPump<RootObjectType> {

    /**
     * constructor, sets projects URI and login credentials
     *
     * @param projectHandle URL of the project instance
     * @param privateKeyLoc private key location for authenticated login
     * @param username      username for authenticated login
     * @param password      password for authenticated login
     */
    public VCSPump(String projectHandle, String privateKeyLoc, String username, String password) {
        super(projectHandle, privateKeyLoc, username, password);
    }

    @Override
    protected void mineCategories() {}

    @Override
    protected void minePeople() {}

    @Override
    protected void mineRoles() {}

    @Override
    public void mineAllRelations() {}

    @Override
    protected void mineTickets() {}

    @Override
    protected Collection<ProjectSegment> collectIterations() {
        return new ArrayList<>();
    }

    @Override
    protected void mineWiki() {}

    @Override
    protected void minePriorities() {}

    @Override
    protected void mineWUTypes() {}

    @Override
    protected void mineResolutions() {}

    @Override
    protected void mineStatuses() {}

    @Override
    protected void mineSeverities() {}


    @Override
    protected void mineWURelationTypes() {}
}
