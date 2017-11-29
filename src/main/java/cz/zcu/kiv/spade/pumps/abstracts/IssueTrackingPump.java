package cz.zcu.kiv.spade.pumps.abstracts;

import cz.zcu.kiv.spade.domain.*;
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
    public abstract void mineTickets();

    @Override
    public abstract Collection<ProjectSegment> mineIterations();

    @Override
    public void mineEnums() {
        mineWUTypes();
        minePriorities();
        mineRoles();
    }

    @Override
    public abstract void mineAllRelations();

    @Override
    public abstract void mineCategories();

    @Override
    public abstract void minePeople();

    /**
     * gets a Role instance with a given name
     * @param name role name
     * @return Role instance or null
     */
    protected Role resolveRole(String name) {
        for (Role role : pi.getRoles()) {
            if (name.equals(role.getName())) {
                return role;
            }
        }
        return null;
    }

    @Override
    public abstract void mineRoles();

    @Override
    public abstract void minePriorities();
}
