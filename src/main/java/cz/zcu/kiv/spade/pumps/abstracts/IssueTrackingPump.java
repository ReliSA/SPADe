package cz.zcu.kiv.spade.pumps.abstracts;

import cz.zcu.kiv.spade.domain.abstracts.ProjectSegment;
import cz.zcu.kiv.spade.pumps.DataPump;

import java.util.Collection;

public abstract class IssueTrackingPump<RootObjectType> extends DataPump<RootObjectType> implements IIssueTrackingPump {

    public static final String WU_MENTION_REGEX = "(?<=^#|\\W#|_#|$#)\\d{1,4}(?=\\W|_|^|$)";

    /**
     * @param projectHandle URL of the project instance
     * @param privateKeyLoc private key location for authenticated login
     * @param username      username for authenticated login
     * @param password      password for authenticated login
     */
    public IssueTrackingPump(String projectHandle, String privateKeyLoc, String username, String password) {
        super(projectHandle, privateKeyLoc, username, password);
    }

    @Override
    public void mineEnums() {
        super.mineEnums();
        mineWUTypes();
        minePriorities();
    }

    @Override
    public abstract void mineAllRelations();

    @Override
    protected abstract void mineCategories();

    @Override
    protected abstract void minePeople();

    @Override
    protected abstract void mineRoles();

    @Override
    public abstract void minePriorities();

    @Override
    public abstract void mineWUTypes();

    @Override
    protected abstract void mineTickets();

    @Override
    protected abstract Collection<ProjectSegment> mineIterations();
}
