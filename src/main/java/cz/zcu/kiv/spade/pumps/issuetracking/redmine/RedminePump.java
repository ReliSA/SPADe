package cz.zcu.kiv.spade.pumps.issuetracking.redmine;

import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.RedmineManagerFactory;
import cz.zcu.kiv.spade.App;
import cz.zcu.kiv.spade.domain.Project;
import cz.zcu.kiv.spade.domain.ProjectInstance;
import cz.zcu.kiv.spade.domain.enums.Tool;
import cz.zcu.kiv.spade.pumps.issuetracking.IssueTrackingPump;

import javax.persistence.EntityManager;

/**
 * a pump for mining Redmine data
 *
 * @author Petr Pícha
 */
public class RedminePump extends IssueTrackingPump<RedmineManager, com.taskadapter.redmineapi.bean.Project> {

    private static final String PROJECTS_PERMISSION_ERR_MSG = "Insufficient permissions for projects";

    /**
     * constructor, sets projects URL and login credentials
     *
     * @param projectHandle URL of the project instance
     * @param privateKeyLoc private key location for authenticated login
     * @param username      username for authenticated login
     * @param password      password for authenticated login
     */
    public RedminePump(String projectHandle, String privateKeyLoc, String username, String password) {
        super(projectHandle, privateKeyLoc, username, password);
        this.tool = Tool.REDMINE;
        issueMiner = new RedmineIssueMiner(this);
        peopleMiner = new RedminePeopleMiner(this);
        enumsMiner = new RedmineEnumsMiner(this);
        relationMiner = new RedmineRelationMiner(this);
        wikiMiner = new RedmineWikiMiner(this);
        segmentMiner = new RedmineSegmentMiner(this);
    }

    @Override
    public ProjectInstance mineData(EntityManager em) {
        pi = super.mineData(em);

        setToolInstance();

        try {
            for (com.taskadapter.redmineapi.bean.Project prj : rootObject.getProjectManager().getProjects()) {
                if (prj.getIdentifier().equals(pi.getName())) {
                    secondaryObject = prj;
                }
            }
        } catch (RedmineException e) {
            App.printLogMsg(PROJECTS_PERMISSION_ERR_MSG, false);
        }

        Project project = new Project();
        project.setName(secondaryObject.getName());
        project.setDescription(secondaryObject.getDescription());
        project.setStartDate(secondaryObject.getCreatedOn());

        pi.setExternalId(secondaryObject.getId().toString());
        pi.setProject(project);

        mineContent();

        return pi;
    }

    @Override
    protected RedmineManager init() {
        String serverWithProtocol = HTTPS_PROTOCOL + getServer();
        if (privateKeyLoc != null)
            return RedmineManagerFactory.createWithApiKey(serverWithProtocol, privateKeyLoc);
        if (username != null && password != null)
            return RedmineManagerFactory.createWithUserAuth(serverWithProtocol, username, password);
        return RedmineManagerFactory.createUnauthenticated(serverWithProtocol);
    }
}
