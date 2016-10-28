package cz.zcu.kiv.spade.pumps.git;

import cz.zcu.kiv.spade.pumps.DataPump;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.FooterLine;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.util.io.DisabledOutputStream;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class GitPump extends DataPump {

    private Repository repository;
    private Set<String> people;

    public GitPump(String projectHandle) {
        super(projectHandle);
        this.people = new HashSet<>();
    }

    @Override
    public void mineData() {

        Git git = new Git(repository);
        List<Ref> branches = new LinkedList<>();
        try {
            branches = git.branchList().call();
        } catch (GitAPIException e) {
            e.printStackTrace();
        }

        mineCommits();

        System.out.println();
        System.out.println("Project: " + projectName);
        System.out.println("URL: " + projectHandle);
        System.out.println("Tags: " + repository.getTags().size() + " " + repository.getTags().keySet().toString());
        System.out.print("Branches: " + branches.size() + " [");
        for (int i = 0; i < branches.size(); i++) {
            System.out.print(stripFileName(branches.get(i).getName()));
            if (i != branches.size() - 1) System.out.print(", ");
            else System.out.println("]");
        }
        System.out.println("Contributors: " + people.size() + " " + people.toString());
    }

    private void mineCommits() {
        System.out.println("Changes:\n");

        RevWalk revWalk = new RevWalk(repository);

        try {
            ObjectId commitId = repository.resolve(Constants.HEAD);
            revWalk.markStart(revWalk.parseCommit(commitId));
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (RevCommit commit : revWalk) {
            mineCommit(commit);
        }
    }

    private void mineCommit(RevCommit commit) {
        people.add(commit.getAuthorIdent().getName());
        people.add(commit.getCommitterIdent().getName());

        for (FooterLine line : commit.getFooterLines()){
            if (line.getKey().endsWith("-by")) {
                String[] parts = line.getValue().split("<");
                people.add(parts[0].trim());
            }
        }

        /*System.out.print("Id:\n" +
                "\t" + commit.getId().getName() + "\n" +
                "Author:\n" +
                "\t" + commit.getAuthorIdent().getName() + "\n" +
                "Committer:\n" +
                "\t" + commit.getCommitterIdent().getName() + "\n" +
                "Email:\n" +
                "\t" + commit.getCommitterIdent().getEmailAddress() + "\n" +
                "Time:\n" +
                "\t" + convertDate(commit.getCommitTime()).toString() + "\n" +
                "Msg:\n" +
                "\t" + commit.getFullMessage() + "\n" +
                "----------------------------------------\n");

        mineChanges(commit);*/
    }

    private void mineChanges(RevCommit commit) {
        System.out.println("Changes:");
        try {
            RevTree parentTree = null;
            if (commit.getParentCount() != 0) parentTree = commit.getParent(0).getTree();

            DiffFormatter df = new DiffFormatter(DisabledOutputStream.INSTANCE);
            df.setRepository(repository);
            df.setDiffComparator(RawTextComparator.DEFAULT);
            df.setDetectRenames(true);

            List<DiffEntry> diffs = df.scan(parentTree, commit.getTree());
            for (DiffEntry diff : diffs) {
                mineChange(diff);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("========================================");
    }

    private void mineChange(DiffEntry diff) {
        String type = diff.getChangeType().name();
        String desc = "";
        String newFileName = stripFileName(diff.getNewPath());
        String newFileDir = diff.getNewPath().substring(0, diff.getNewPath().lastIndexOf(newFileName));
        String oldFileName = stripFileName(diff.getOldPath());
        String oldFileDir = diff.getOldPath().substring(0, diff.getOldPath().lastIndexOf(oldFileName));

        if (type.equals("DELETE")) {
            desc += " " + oldFileName + "\n" +
                    "\t\t" + diff.getOldPath();
        } else if (type.equals("ADD") || type.equals("MODIFY")) {
            desc += " " + newFileName + "\n" +
                    "\t\t" + diff.getNewPath();
        } else {
            if (type.equals("RENAME")) {
                if (newFileName.equals(oldFileName)) type = "MOVE";
                else if (!newFileDir.equals(oldFileDir)) type += " AND MOVE";
            }
            if (diff.getScore() < 100) type += " AND MODIFY";
            desc += " " + newFileName + "\n" +
                    "\t\tfrom: " + diff.getOldPath() + "\n" +
                    "\t\tto: " + diff.getNewPath() + "\n" +
                    "\t\tscore: " + diff.getScore();
        }
        System.out.println("\t" + type + desc);
    }

    @Override
    protected void getRootObject() {
        File file = new File(ROOT_TEMP_DIR + getProjectDir());
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
        repository = git.getRepository();
    }

    protected String getProjectDir() {
        String withoutProtocol = projectHandle.split("://")[1];
        String server = withoutProtocol.substring(0, withoutProtocol.indexOf('/'));
        String pathOnServer = withoutProtocol.substring(withoutProtocol.indexOf('/'));
        String withoutPort = server.split(":")[0] + pathOnServer;
        return withoutPort.substring(0, withoutPort.lastIndexOf(".git"));
    }

    @Override
    protected Date convertDate(Object date) {
        long milliseconds = Long.parseLong(date.toString());
        milliseconds *= 1000;
        return new Date(milliseconds);
    }

    private String stripFileName(String path) {
        if (path.contains("/")) return path.substring(path.lastIndexOf("/") + 1);
        else return path;
    }

}
