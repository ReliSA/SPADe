package cz.zcu.kiv.spade.pumps.abstracts;

import cz.zcu.kiv.spade.pumps.DataPump;

/**
 * pump for mining a complex tool (with VCS and issuetracking capabilities)
 * @param <RootObject> the main object of the given tool mining API
 *
 * @author Petr Pícha
 */
public abstract class ComplexPump<RootObject> extends DataPump<RootObject> implements IVCSPump, IIssueTrackingPump {

    /**
     * constructor, sets projects URI and login credentials
     *
     * @param projectHandle URL of the project instance
     * @param privateKeyLoc private key location for authenticated login
     * @param username      username for authenticated login
     * @param password      password for authenticated login
     */
    public ComplexPump(String projectHandle, String privateKeyLoc, String username, String password) {
        super(projectHandle, privateKeyLoc, username, password);
    }

    @Override
    public abstract void addTags();

    @Override
    public abstract void mineBranches();

    @Override
    public abstract void mineTickets();

    @Override
    public abstract void mineEnums();
}
