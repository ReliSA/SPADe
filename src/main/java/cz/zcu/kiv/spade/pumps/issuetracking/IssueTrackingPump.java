package cz.zcu.kiv.spade.pumps.issuetracking;

import cz.zcu.kiv.spade.load.DBInitializer;
import cz.zcu.kiv.spade.pumps.*;

public abstract class IssueTrackingPump<RootObject, SecondaryObject> extends DataPump<RootObject, SecondaryObject> {

    protected IssueMiner issueMiner;
    protected EnumsMiner enumsMiner;
    protected WikiMiner wikiMiner;
    protected SegmentMiner segmentMiner;
    protected IssueTrackingRelationMiner relationMiner;

    /**
     * @param projectHandle URL of the project instance
     * @param privateKeyLoc private key location for authenticated login
     * @param username      username for authenticated login
     * @param password      password for authenticated login
     */
    protected IssueTrackingPump(String projectHandle, String privateKeyLoc, String username, String password) {
        super(projectHandle, privateKeyLoc, username, password);
    }

    protected void mineContent() {
        new DBInitializer(getEntityManager()).setDefaultEnums(pi);
        if (enumsMiner != null) {
            enumsMiner.setEntityManager();
            enumsMiner.mineEnums();
        }
        if (peopleMiner != null) {
            peopleMiner.setEntityManager();
            peopleMiner.mineGroups();
            peopleMiner.minePeople();
        }
        if (issueMiner != null) {
            issueMiner.setEntityManager();
            issueMiner.mineItems();
        }
        if (segmentMiner != null) {
            segmentMiner.setEntityManager();
            segmentMiner.mineIterations();
        }
        if (wikiMiner != null) {
            wikiMiner.setEntityManager();
            wikiMiner.mineWiki();
        }
        if (relationMiner != null) {
            relationMiner.setEntityManager();
            relationMiner.mineAllRelations();
        }
        if (enumsMiner != null) {
            enumsMiner.addDeletedStatus();
            enumsMiner.assignDefaultEnums();
        }
    }
}
