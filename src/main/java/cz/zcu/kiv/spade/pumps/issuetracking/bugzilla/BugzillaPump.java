package cz.zcu.kiv.spade.pumps.issuetracking.bugzilla;

import b4j.core.session.bugzilla.BugzillaClient;
import b4j.core.session.bugzilla.BugzillaRestClientFactory;
import b4j.core.session.bugzilla.async.AsyncBugzillaRestClientFactory;
import cz.zcu.kiv.spade.domain.Project;
import cz.zcu.kiv.spade.domain.ProjectInstance;
import cz.zcu.kiv.spade.domain.enums.Tool;
import cz.zcu.kiv.spade.pumps.issuetracking.IssueTrackingPump;

import javax.persistence.EntityManager;
import java.net.URI;

public class BugzillaPump extends IssueTrackingPump<BugzillaClient, b4j.core.Project> {

    static final String PRODUCT_CRITERION = "product";
    static final String LIMIT_CRITERION = "limit";

    /**
     * constructor, sets projects URL and login credentials
     *
     * @param projectHandle URL of the project instance
     * @param privateKeyLoc private key location for authenticated login
     * @param username      username for authenticated login
     * @param password      password for authenticated login
     */
    public BugzillaPump(String projectHandle, String privateKeyLoc, String username, String password) {
        super(projectHandle, privateKeyLoc, username, password);
        this.tool = Tool.BUGZILLA;
        enumsMiner = new BugzillaEnumsMiner(this);
        peopleMiner = new BugzillaPeopleMiner(this);
        issueMiner = new BugzillaIssueMiner(this);
        segmentMiner = new BugzillaSegmentMiner(this);
        relationMiner = new BugzillaRelationMiner(this);
    }

    @Override
    protected BugzillaClient init() {
        BugzillaRestClientFactory factory = new AsyncBugzillaRestClientFactory();
        return factory.createWithBasicHttpAuthentication(URI.create(HTTPS_PROTOCOL + getServer()), username, password);

        /*BugzillaHttpSession session = new BugzillaHttpSession();
        try {
            session.setBaseUrl(new URL(HTTPS_PROTOCOL + getServer()));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        HttpSessionParams params = new HttpSessionParams();
        AuthorizationCallback callback = new SimpleAuthorizationCallback(username, password);
        params.setAuthorizationCallback(callback);
        session.setHttpSessionParams(params);*/

    }

    @Override
    public ProjectInstance mineData(EntityManager em) {
        pi = super.mineData(em);

        setToolInstance();

        secondaryObject = rootObject.getProductClient().getProduct(pi.getName());

        Project project = new Project();
        project.setName(secondaryObject.getName());
        project.setDescription(secondaryObject.getDescription());

        pi.setExternalId(secondaryObject.getId());
        pi.setProject(project);

        mineContent();

        return pi;
    }
}
