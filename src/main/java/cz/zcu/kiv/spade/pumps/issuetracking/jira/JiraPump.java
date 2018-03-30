package cz.zcu.kiv.spade.pumps.issuetracking.jira;

import com.atlassian.jira.rest.client.JiraRestClient;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import cz.zcu.kiv.spade.domain.Project;
import cz.zcu.kiv.spade.domain.ProjectInstance;
import cz.zcu.kiv.spade.domain.enums.Tool;
import cz.zcu.kiv.spade.pumps.DataPump;
import cz.zcu.kiv.spade.pumps.issuetracking.IssueTrackingPump;
import cz.zcu.kiv.spade.pumps.vcs.git.GitPump;

import java.net.URI;
import java.util.concurrent.ExecutionException;

public class JiraPump extends IssueTrackingPump<JiraRestClient, com.atlassian.jira.rest.client.domain.Project> {

    private static final String SERVER_URL_FORMAT = "https://%s/" + Tool.JIRA.name().toLowerCase();
    static final String ATTACHMENT_FIELD_NAME = "Attachment";

    private String repo;

    /**
     * constructor, sets projects URL and login credentials
     *
     * @param projectHandle URL of the project instance
     * @param privateKeyLoc private key location for authenticated login
     * @param username      username for authenticated login
     * @param password      password for authenticated login
     */
    public JiraPump(String projectHandle, String privateKeyLoc, String username, String password) {
        super(projectHandle, privateKeyLoc, username, password);
        this.tool = Tool.JIRA;
        issueMiner = new JiraXmlIssueMiner(this);
        peopleMiner = new JiraPeopleMiner(this);
        enumsMiner = new JiraEnumsMiner(this);
        relationMiner = new JiraRelationMiner(this);
        segmentMiner = new JiraSegmentMiner(this);
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

        try {
            secondaryObject = (rootObject.getProjectClient().getProject(pi.getName())).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        Project project;
        if ((project = pi.getProject()) == null) project = new Project();
        project.setName(secondaryObject.getName());
        project.setDescription(secondaryObject.getDescription());

        String self = secondaryObject.getSelf().toString();
        pi.setExternalId(self.substring(self.lastIndexOf(SLASH) + 1));
        pi.setName(secondaryObject.getKey());
        pi.setDescription(secondaryObject.getName() + LINE_BREAK + secondaryObject.getDescription());
        pi.setProject(project);

        mineContent();

        return pi;
    }

    @Override
    protected JiraRestClient init() {
        URI jiraServerUri = URI.create(String.format(SERVER_URL_FORMAT, getServer()));
        return new AsynchronousJiraRestClientFactory().createWithBasicHttpAuthentication(jiraServerUri, username, password);
    }
}
