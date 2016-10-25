package cz.zcu.kiv.spade;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Main {

    private static final String PERSISTENCE_UNIT = "cz.zcu.kiv.spade";

    public static void main(String[] args) {

        //EntityManagerFactory factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT);
        //EntityManager em = factory.createEntityManager();

        //em.close();


        File file = new File("D:/repo");
        Git git = null;

        if(file.exists()){
            try {
                git = Git.open(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                git = Git.cloneRepository()
                        .setBare(true)
                        .setURI("https://github.com/ReliSA/SPADe.git")
                        .setGitDir(file)
                        .call();
            } catch (GitAPIException e) {
                e.printStackTrace();
            }
        }
        Repository repo = git.getRepository();
        RevWalk revWalk = new RevWalk(repo);
        ObjectId commitId = null;
        try {
            commitId = repo.resolve(Constants.HEAD);
            revWalk.markStart(revWalk.parseCommit(commitId));
        } catch (IOException e) {
            e.printStackTrace();
        }
        for(RevCommit commit : revWalk ) {

            System.out.println("Id: " + commit.getId() + "\n" +
                    "Name: " + commit.getName() + "\n" +
                    "Author: " + commit.getAuthorIdent().getName() + "\n" +
                    "Commit: " + commit.getCommitterIdent().getName() + "\n" +
                    "Time: " + commit.getCommitTime() + "\n" +
                    "Short msg: " + commit.getShortMessage() + "\n" +
                    "Full msg: " + commit.getFullMessage().replaceAll("\n\n", "\n"));

        }
    }
}
