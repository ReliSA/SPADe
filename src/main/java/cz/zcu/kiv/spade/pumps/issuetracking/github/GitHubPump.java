package cz.zcu.kiv.spade.pumps.issuetracking.github;

import cz.zcu.kiv.spade.App;
import cz.zcu.kiv.spade.domain.*;
import cz.zcu.kiv.spade.domain.enums.Tool;
import cz.zcu.kiv.spade.pumps.DataPump;
import cz.zcu.kiv.spade.pumps.issuetracking.IssueTrackingPump;
import cz.zcu.kiv.spade.pumps.vcs.git.GitPump;
import org.kohsuke.github.*;

import java.io.IOException;
import java.util.*;

/**
 * data pump for mining GitHub repositories
 *
 * @author Petr Pícha
 */
public class GitHubPump extends IssueTrackingPump<GHRepository, GitHub> {

    private static final String CONNECTED_LOG_MSG = "connected...";
    private static final String CONNECTION_LOG_FORMAT = "username: %s, remaining rate limit: %d, reset at: %s";
    static final String COMMIT_URL_SUFFIX = "/commit/";
    private static final int WAIT_MILLSECS = 5000;
    private static final int LIMIT_THRESHOLD = 500;

    private String repo;
    /**
     * list of usernames necessary for continuous mining (rate limit workaround)
     */
    private List<String> usernames = new ArrayList<>();
    /**
     * list of passwords necessary for continuous mining (rate limit workaround)
     */
    private List<String> passwords = new ArrayList<>();
    private final GitHubReleaseMiner releaseMiner;

    /**
     * a constructor, sets project's URL and login credentials
     *
     * @param projectHandle URL of the project instance
     * @param privateKeyLoc private key location for authenticated login
     * @param username      username for authenticated login
     * @param password      password for authenticated login
     */
    public GitHubPump(String projectHandle, String privateKeyLoc, String username, String password) {
        super(projectHandle, privateKeyLoc, username, password);
        this.tool = Tool.GITHUB;
        issueMiner = new GitHubIssueMiner(this);
        peopleMiner = new GitHubPeopleMiner(this);
        enumsMiner = new GitHubEnumsMiner(this);
        relationMiner = new GitHubRelationMiner(this);
        wikiMiner = new GitHubWikiMiner(this);
        segmentMiner = new GitHubSegmentMiner(this);
        releaseMiner = new GitHubReleaseMiner(this);

        usernames.add(0, username);
        usernames.add(1, "spade01");
        usernames.add(2, "spade02");
        usernames.add(3, "spade03");
        passwords.add(0, password);
        passwords.add(1, "papepi48");
        passwords.add(2, "papepi48");
        passwords.add(3, "papepi48");
    }

    @Override
    protected GHRepository init() {
        return null;
    }

    /**
     * initializes connection to the GitHub server and swaps user accounts and reconnects if necessary (rate limit runs out)
     *
     * @param wait true if there should be a 5s waiting period before reconnecting, false if users should be switched
     * @return an instance for mining GitHub data
     */
    private GHRepository init(boolean wait) {
        GHRepository repo;
        while (true) {
            try {
                if (wait) {
                    Thread.sleep(WAIT_MILLSECS);
                } else {
                    int index = (usernames.indexOf(username) + 1) % usernames.size();
                    username = usernames.get(index);
                    password = passwords.get(index);
                }
                secondaryObject = GitHub.connectUsingPassword(username, password);
                GHRateLimit limit = secondaryObject.getRateLimit();
                App.printLogMsg(this, CONNECTED_LOG_MSG);
                App.printLogMsg(this, String.format(CONNECTION_LOG_FORMAT, username, limit.remaining, limit.reset.toString()));
                repo = secondaryObject.getRepository(getProjectFullName());
                if (limit.remaining < LIMIT_THRESHOLD) continue;
                break;
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
        return repo;
    }

    @Override
    public void setRepo(String repo) {
        this.repo = repo;
    }

    private void mineRepository() {
        if (repo == null) repo = projectHandle;
        DataPump gitPump = new GitPump(repo, null, null, null);
        pi = gitPump.mineData();
        gitPump.close();
        pi.getStats().setRepo(System.currentTimeMillis());
    }

    @Override
    public ProjectInstance mineData() {
        pi = super.mineData();

        mineRepository();

        this.tool = Tool.GITHUB;
        pi.getToolInstance().setTool(tool);
        setToolInstance();

        rootObject = init(false);

        enhanceGitData();

        mineContent();

        return pi;
    }

    private void enhanceGitData() {
        if (rootObject.getDescription() != null) {
            pi.getProject().setDescription(rootObject.getDescription().trim());
        }
        Date creation = null;
        try {
            creation = rootObject.getCreatedAt();
        } catch (IOException e) {
            rootObject = init(true);
        }
        if (creation != null && creation.before(pi.getProject().getStartDate())) {
            pi.getProject().setStartDate(creation);
        }

        setDefaultBranch();
        enhanceCommits();
        new GitHubCommentMiner(this).mineCommitComments();
        releaseMiner.mineTags();
    }

    /**
     * finds and sets a default repository branch
     */
    private void setDefaultBranch() {
        String defaultBranch = rootObject.getDefaultBranch();
        if (!defaultBranch.equals(GitPump.GIT_DEFAULT_BRANCH_NAME)) {
            Map<String, Branch> branches = new HashMap<>();
            for (Commit commit : pi.getProject().getCommits()) {
                for (Branch branch : commit.getBranches()) {
                    if (!branches.containsKey(branch.getName())) {
                        branch.setIsMain(false);
                        branches.put(branch.getName(), branch);
                    }
                }
            }
            branches.get(defaultBranch).setIsMain(true);
        }
    }

    /**
     * adds correct URLs to commits mined from git
     */
    private void enhanceCommits() {
        for (Commit commit : pi.getProject().getCommits()) {
            String commitUrlPrefix = projectHandle.replace(App.GIT_SUFFIX, "") + COMMIT_URL_SUFFIX;
            commit.setUrl(commitUrlPrefix + commit.getName());
        }
    }

    /**
     * checks current access rate limit for GitHub server
     */
    void checkRateLimit() {
        GHRateLimit limit;
        while (true) {
            try {
                limit = secondaryObject.getRateLimit();
                break;
            } catch (IOException e) {
                rootObject = init(true);
            }
        }
        if (limit != null && limit.remaining < LIMIT_THRESHOLD) {
            App.printLogMsg(this, String.format(CONNECTION_LOG_FORMAT, username, limit.remaining, limit.getResetDate().toString()));
            rootObject = init(false);
        }
    }

    /**
     * gets project full name (organisation/project)
     *
     * @return project name
     */
    private String getProjectFullName() {
        return getProjectDir().substring(getProjectDir().indexOf(DataPump.SLASH) + 1);
    }

    void resetRootObject() {
        this.rootObject = init(true);
    }

    enum GitHubRole {
        collaborator,
        contributor,
        owner
    }
}
