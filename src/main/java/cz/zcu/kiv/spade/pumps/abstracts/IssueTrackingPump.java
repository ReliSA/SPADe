package cz.zcu.kiv.spade.pumps.abstracts;

import cz.zcu.kiv.spade.pumps.DataPump;

public abstract class IssueTrackingPump<RootObjectType> extends DataPump<RootObjectType> {

    public static final String DEFAULT_ISSUE_MENTION_REGEX = "(?<=^#|\\W#|_#|$#)\\d{1,4}(?=\\W|_|^|$)";

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
    protected void mineTags() {}

    @Override
    protected void mineBranches() {}
}
