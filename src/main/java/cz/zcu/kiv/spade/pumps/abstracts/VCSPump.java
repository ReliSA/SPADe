package cz.zcu.kiv.spade.pumps.abstracts;

import cz.zcu.kiv.spade.pumps.DataPump;

public abstract class VCSPump<RootObjectType> extends DataPump<RootObjectType> implements IVCSPump {

    /**
     * @param projectHandle URL of the project instance
     * @param privateKeyLoc private key location for authenticated login
     * @param username      username for authenticated login
     * @param password      password for authenticated login
     */
    public VCSPump(String projectHandle, String privateKeyLoc, String username, String password) {
        super(projectHandle, privateKeyLoc, username, password);
    }

    @Override
    public abstract void addTags();

    @Override
    public abstract void mineBranches();
}
