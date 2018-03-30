package cz.zcu.kiv.spade.pumps.issuetracking.redmine;

import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.RedmineManagerFactory;
import cz.zcu.kiv.spade.App;
import cz.zcu.kiv.spade.domain.Project;
import cz.zcu.kiv.spade.domain.ProjectInstance;
import cz.zcu.kiv.spade.domain.enums.Tool;
import cz.zcu.kiv.spade.pumps.DataPump;
import cz.zcu.kiv.spade.pumps.issuetracking.IssueTrackingPump;
import cz.zcu.kiv.spade.pumps.vcs.git.GitPump;

/**
 * a pump for mining Redmine data
 *
 * @author Petr Pícha
 */
public class RedminePump extends IssueTrackingPump<RedmineManager, com.taskadapter.redmineapi.bean.Project> {

    private static final String PROJECTS_PERMISSION_ERR_MSG = "Insufficient permissions for projects";
    private static final String MB = "MB";
    private static final String KB = "KB";
    private static final String RIGHT_PARENTHESIS = ")";
    private static final String LEFT_PARENTHESIS = "(";
    private static final int KILO = 1024;

    private String repo;

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
    public ProjectInstance mineData() {
        pi = super.mineData();

        if (repo != null && !repo.isEmpty()) {
            mineRepository();
            pi.setUrl(projectHandle);
            pi.setName(getProjectName());
        }

        setToolInstance();

        try {
            for (com.taskadapter.redmineapi.bean.Project prj : rootObject.getProjectManager().getProjects()) {
                if (prj.getIdentifier().equals(pi.getName())) {
                    secondaryObject = prj;
                }
            }
        } catch (RedmineException e) {
            App.printLogMsg(this, PROJECTS_PERMISSION_ERR_MSG);
        }

        Project project;
        if ((project = pi.getProject()) == null) project = new Project();
        project.setName(secondaryObject.getName());
        project.setDescription(secondaryObject.getDescription());
        project.setStartDate(secondaryObject.getCreatedOn());

        pi.setExternalId(secondaryObject.getId().toString());
        pi.setName(secondaryObject.getIdentifier());
        pi.setDescription(secondaryObject.getName() + LINE_BREAK + secondaryObject.getDescription());
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

    @Override
    public void setRepo(String repo) {
        this.repo = repo;
    }

    private void mineRepository() {
        if (repo == null) return;
        DataPump gitPump = new GitPump(repo, null, username, password);
        pi = gitPump.mineData();
        gitPump.close();
        pi.getStats().setRepo(System.currentTimeMillis());
        App.printLogMsg(this, "repo mined");
    }

    long parseSize(String sizeString) {
        sizeString = sizeString.replace(LEFT_PARENTHESIS, "").replace(RIGHT_PARENTHESIS, "").trim();
        String sizeUnit = sizeString.split(SPACE)[1];
        sizeString = sizeString.split(SPACE)[0];
        long size = (long) Double.parseDouble(sizeString);
        if (sizeUnit.equals(KB)) {
            size = KILO * size;
        } else if (sizeUnit.equals(MB)) {
            size = KILO * KILO * size;
        }
        return size;
    }
}
