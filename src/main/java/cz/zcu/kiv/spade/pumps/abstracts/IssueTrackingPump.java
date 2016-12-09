package cz.zcu.kiv.spade.pumps.abstracts;

import cz.zcu.kiv.spade.domain.WorkUnit;
import cz.zcu.kiv.spade.pumps.DataPump;

import java.util.Map;

public abstract class IssueTrackingPump<RootObjectType> extends DataPump<RootObjectType> implements IIssueTrackingPump {

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
    public abstract Map<Integer, WorkUnit> mineTickets();
}
