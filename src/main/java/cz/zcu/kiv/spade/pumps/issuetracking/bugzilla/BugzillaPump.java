package cz.zcu.kiv.spade.pumps.issuetracking.bugzilla;

import b4j.core.session.bugzilla.BugzillaClient;
import b4j.core.session.bugzilla.BugzillaRestClientFactory;
import b4j.core.session.bugzilla.async.AsyncBugzillaRestClientFactory;
import b4j.util.HttpClients;
import b4j.util.HttpSessionParams;
import com.atlassian.httpclient.api.HttpClient;
import cz.zcu.kiv.spade.domain.Project;
import cz.zcu.kiv.spade.domain.ProjectInstance;
import cz.zcu.kiv.spade.domain.enums.Tool;
import cz.zcu.kiv.spade.pumps.DataPump;
import cz.zcu.kiv.spade.pumps.issuetracking.IssueTrackingPump;
import cz.zcu.kiv.spade.pumps.vcs.git.GitPump;

import java.net.URI;

public class BugzillaPump extends IssueTrackingPump<BugzillaClient, b4j.core.Project> {

    private static final String EQUAL = "=";
    private static final String AMP = "&";
    private static final String SPACE_HTML_CODE = "%20";
    private static final String QUESTION_MARK = "\\?";

    static final String PRODUCT_CRITERION = "product";
    static final String LIMIT_CRITERION = "limit";

    private String repo;

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
        issueMiner = new BugzillaXmlIssueMiner(this);
        segmentMiner = new BugzillaSegmentMiner(this);
        relationMiner = new BugzillaRelationMiner(this);
    }

    @Override
    protected BugzillaClient init() {
        URI serverUri = URI.create(HTTPS_PROTOCOL + getServer());
        HttpSessionParams httpSessionParams = new HttpSessionParams();
        HttpClient httpClient = HttpClients.createAtlassianClient(serverUri, httpSessionParams);
        BugzillaRestClientFactory factory = new AsyncBugzillaRestClientFactory();
        BugzillaClient bugzillaClient = factory.create(serverUri, httpClient);
        bugzillaClient.login(username, password);
        return bugzillaClient;
    }

    @Override
    public void setRepo(String repo) {
        this.repo = repo;
    }

    private void mineRepository() {
        if (repo == null) return;
        DataPump gitPump = new GitPump(repo, null, null, null);
        pi = gitPump.mineData();
        gitPump.close();
        pi.getStats().setRepo(System.currentTimeMillis());
    }

    @Override
    public ProjectInstance mineData() {
        pi = super.mineData();

        mineRepository();

        pi.setUrl(projectHandle);
        pi.setName(getProjectName());
        setToolInstance();

        secondaryObject = rootObject.getProductClient().getProduct(pi.getName());

        Project project;
        if ((project = pi.getProject()) == null) project = new Project();
        project.setName(secondaryObject.getName());
        project.setDescription(secondaryObject.getDescription());

        pi.setExternalId(secondaryObject.getId());
        pi.setProject(project);

        mineContent();

        return pi;
    }

    @Override
    protected String getProjectName() {
        String[] params = projectHandle.split(QUESTION_MARK)[1].split(AMP);
        for (String param : params) {
            if (param.startsWith(PRODUCT_CRITERION)) {
                return param.split(EQUAL)[1].replace(SPACE_HTML_CODE, SPACE);
            }
        }
        return null;
    }
}
