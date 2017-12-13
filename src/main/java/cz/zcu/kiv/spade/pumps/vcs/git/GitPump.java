package cz.zcu.kiv.spade.pumps.vcs.git;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import cz.zcu.kiv.spade.App;
import cz.zcu.kiv.spade.domain.ProjectInstance;
import cz.zcu.kiv.spade.domain.enums.Tool;
import cz.zcu.kiv.spade.pumps.vcs.VcsPump;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.transport.*;
import org.eclipse.jgit.util.FS;

import javax.persistence.EntityManager;
import java.io.File;
import java.io.IOException;

/**
 * data pump specific for mining git repositories
 *
 * @author Petr Pícha
 */
public class GitPump extends VcsPump<Repository, Git> {

    public static final String GIT_DEFAULT_BRANCH_NAME = "master";
    public static final int SHORT_COMMIT_HASH_LENGTH = 7;
    private static final String REPO_CLONED_LOG_MSG = "git repository cloned";

    /**
     * @param projectHandle URL of the project instance
     * @param privateKeyLoc private key location for authenticated login
     * @param username      username for authenticated login
     * @param password      password for authenticated login
     */
    public GitPump(String projectHandle, String privateKeyLoc, String username, String password) {
        super(projectHandle, privateKeyLoc, username, password);
        this.tool = Tool.GIT;
        commitMiner = new GitCommitMiner(this);
        peopleMiner = new GitPeopleMiner(this);
        relationMiner = new GitRelationMiner(this);
        releaseMiner = new GitReleaseMiner(this);
    }

    @Override
    protected Repository init() {
        Repository repo = null;

        File file = new File(App.ROOT_TEMP_DIR + getProjectDir());

        CloneCommand cloneCommand = Git.cloneRepository()
                .setBare(true)
                .setURI(projectHandle)
                .setGitDir(file);

        if (username != null)
            cloneCommand.setCredentialsProvider(new UsernamePasswordCredentialsProvider(username, password));

        if (privateKeyLoc != null) {
            cloneCommand = authenticate(cloneCommand);
        }

        try {
            Git git = cloneCommand.call();
            repo = git.getRepository();
        } catch (GitAPIException e) {
            e.printStackTrace();
        }
        App.printLogMsg(REPO_CLONED_LOG_MSG);
        return repo;
    }

    @Override
    public ProjectInstance mineData(EntityManager em) {
        pi = super.mineData(em);

        try {
            pi.setDescription(rootObject.getGitwebDescription());
        } catch (IOException e) {
            e.printStackTrace();
        }

        setToolInstance();

        mineContent();

        return pi;
    }

    @Override
    public void close() {
        rootObject.close();
        super.close();
    }

    /**
     * authenticates a clone command using protected field values of the pump
     *
     * @param cloneCommand unauthenticated clone command
     * @return authenticated clone command
     */
    private CloneCommand authenticate(CloneCommand cloneCommand) {
        SshSessionFactory sshSessionFactory = new JschConfigSessionFactory() {
            @Override
            protected void configure(OpenSshConfig.Host host, Session session) {
                //unnecessary to override
            }

            @Override
            protected JSch createDefaultJSch(FS fs) throws JSchException {
                JSch defaultJSch = super.createDefaultJSch(fs);
                defaultJSch.addIdentity(privateKeyLoc);
                return defaultJSch;
            }
        };

        cloneCommand.setTransportConfigCallback(transport -> {
            SshTransport sshTransport = (SshTransport) transport;
            sshTransport.setSshSessionFactory(sshSessionFactory);
        });
        return cloneCommand;
    }
}
