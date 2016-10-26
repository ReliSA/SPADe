package cz.zcu.kiv.spade.pumps.git;

import cz.zcu.kiv.spade.domain.enums.Tool;
import cz.zcu.kiv.spade.pumps.DataPump;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;

import java.io.File;
import java.io.IOException;
import java.util.Date;

public class GitPump extends DataPump {

    public GitPump(String projectHandle) {
        super(projectHandle, Tool.GIT);
    }

    public void mineData() {

        Repository repository = getRootObject();

        System.out.println("Project: " + projectName);
        System.out.println("URL: " + projectHandle);
        System.out.println();

        RevWalk revWalk = new RevWalk(repository);

        ObjectId commitId = null;
        try {
            commitId = repository.resolve(Constants.HEAD);
            revWalk.markStart(revWalk.parseCommit(commitId));
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (RevCommit commit : revWalk) {

            System.out.println("Id: " + commit.getId().toString() + "\n" +
                    "Author: " + commit.getAuthorIdent().getName() + "\n" +
                    "Committer: " + commit.getCommitterIdent().getName() + "\n" +
                    "Email: " + commit.getCommitterIdent().getEmailAddress() + "\n" +
                    "Time: " + convertDate(commit.getCommitTime()).toString() + "\n" +
                    "Msg: " + commit.getFullMessage());
        }
    }

    protected Repository getRootObject() {
        trimProjectName();
        File file = new File(ROOT_TEMP_DIR + projectName);
        Git git = null;

        if (file.exists()) DataPump.deleteTempDir(file);
        try {
            git = Git.cloneRepository()
                    .setBare(true)
                    .setURI(projectHandle)
                    .setGitDir(file)
                    .call();
        } catch (GitAPIException e) {
            e.printStackTrace();
        }
        return git.getRepository();
    }

    protected void trimProjectName() {
        String withoutProtocol = projectHandle.split("://")[1];
        String server = withoutProtocol.substring(0, withoutProtocol.indexOf('/'));
        String rest = withoutProtocol.substring(withoutProtocol.indexOf('/'));
        String withoutPort = server.split(":")[0];
        String relativePath = withoutPort + rest;
        projectDir = relativePath.substring(0, relativePath.lastIndexOf(".git"));
        projectName = projectDir.substring(projectDir.lastIndexOf("/") + 1);
    }

    @Override
    protected Date convertDate(Object date) {
        long milliseconds = Long.parseLong(date.toString());
        milliseconds *= 1000;
        return new Date(milliseconds);
    }
}
