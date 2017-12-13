package cz.zcu.kiv.spade.pumps.issuetracking.jira;

import com.atlassian.jira.rest.client.JiraRestClient;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import cz.zcu.kiv.spade.domain.Project;
import cz.zcu.kiv.spade.domain.ProjectInstance;
import cz.zcu.kiv.spade.domain.enums.Tool;
import cz.zcu.kiv.spade.pumps.issuetracking.IssueTrackingPump;

import javax.persistence.EntityManager;
import java.net.URI;
import java.util.concurrent.ExecutionException;

public class JiraPump extends IssueTrackingPump<JiraRestClient, com.atlassian.jira.rest.client.domain.Project> {

    private static final String SERVER_URL_FORMAT = "https://%s/" + Tool.JIRA.name().toLowerCase();

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
        issueMiner = new JiraIssueMiner(this);
        peopleMiner = new JiraPeopleMiner(this);
        enumsMiner = new JiraEnumsMiner(this);
        relationMiner = new JiraRelationMiner(this);
        segmentMiner = new JiraSegmentMiner(this);
    }

    @Override
    public ProjectInstance mineData(EntityManager em) {
        pi = super.mineData(em);

        setToolInstance();

        try {
            secondaryObject = (rootObject.getProjectClient().getProject(pi.getName())).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        Project project = new Project();
        project.setName(secondaryObject.getName());
        project.setDescription(secondaryObject.getDescription());

        pi.setExternalId(secondaryObject.getSelf().toString());
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
