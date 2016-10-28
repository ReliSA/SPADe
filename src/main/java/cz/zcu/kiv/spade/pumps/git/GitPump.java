package cz.zcu.kiv.spade.pumps.git;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import cz.zcu.kiv.spade.pumps.DataPump;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.TransportConfigCallback;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.Edit;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.FooterLine;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.transport.*;
import org.eclipse.jgit.util.FS;
import org.eclipse.jgit.util.io.DisabledOutputStream;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class GitPump extends DataPump {

    private Repository repository;
    private Set<String> people = new HashSet<>();
    private Map<String, Set<String>> commitsToTickets  = new HashMap<>();

    public GitPump(String projectHandle) {
        super(projectHandle);
    }

    public GitPump(String projectHandle, String username, String password) {
        super(projectHandle, username, password);
    }

    public GitPump(String projectHandle, String privateKeyLoc) {
        super(projectHandle, privateKeyLoc);
    }

    public GitPump(String projectHandle, String privateKeyLoc, String username, String password) {
        super(projectHandle, privateKeyLoc, username, password);
    }

    @Override
    public void mineData() {
        loadRootObject();
        Git git = new Git(repository);
        List<Ref> branches = new LinkedList<>();
        try {
            branches = git.branchList().call();
        } catch (GitAPIException e) {
            e.printStackTrace();
        }

        System.out.println();

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
        System.out.println("People: " + people.size() + " " + people.toString());
        System.out.println("Mentions: " + commitsToTickets.size());
        for (Map.Entry ticketsPerCommit : commitsToTickets.entrySet()) {
            for (String ticket : commitsToTickets.get(ticketsPerCommit.getKey())){
                System.out.println("\tCommit: " + ticketsPerCommit.getKey() + " \tTicket: #" + ticket);
            }
        }
    }

    private void mineCommits() {
        System.out.println("Commits:\n");

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

        analyzeCommitMsg(commit);

        String mentions = "";
        if (commitsToTickets.containsKey(commit.getId().getName())) {
            mentions = commitsToTickets.get(commit.getId().getName()).toString();
            mentions = mentions.substring(1, mentions.length() - 1);
        }

        System.out.print("Id:\n" +
                "\t" + commit.getId().getName() + "\n" +
                "Author:\n" +
                "\t" + commit.getAuthorIdent().getName() + "\n" +
                "Committer:\n" +
                "\t" + commit.getCommitterIdent().getName() + "\n" +
                "Email:\n" +
                "\t" + commit.getCommitterIdent().getEmailAddress() + "\n" +
                "Time:\n" +
                "\t" + convertDate(commit.getCommitTime()).toString() + "\n" +
                "Mentioned work units:\n" +
                "\t" + mentions + "\n" +
                "Msg:\n" +
                "\t" + commit.getFullMessage() + "\n" +
                "----------------------------------------\n");

        mineChanges(commit);
    }

    protected void analyzeCommitMsg(RevCommit commit) {

        for (FooterLine line : commit.getFooterLines()) {
            if (line.getKey().endsWith("-by")) {
                String[] parts = line.getValue().split("<");
                people.add(parts[0].trim());
            }
        }

        Set<String> mentions = new HashSet<>();
        if (commit.getFullMessage().contains("#")) {
            String[] parts = commit.getFullMessage().split("#");
            for (int i = 1; i < parts.length; i++) {
                String mention = "";
                for (int j = 0; j < parts[i].length(); j++) {
                    if (Character.isDigit(parts[i].charAt(j)))
                        mention += parts[i].charAt(j);
                    else break;
                }
                if (!mention.isEmpty()) {
                    mentions.add(mention);
                }
            }
            if (!mentions.isEmpty()) commitsToTickets.put(commit.getId().getName(), mentions);
        }
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
            System.out.println("Files changed: " + diffs.size());

            int additions = 0, deletions = 0;

            for (DiffEntry diff : diffs) {
                int linesAdded = 0, linesDeleted = 0;

                mineChange(diff);

                for (Edit edit : df.toFileHeader(diff).toEditList()) {
                    linesDeleted += edit.getLengthA();
                    linesAdded += edit.getLengthB();
                    System.out.println(edit.getType().toString());
                }
                additions += linesAdded;
                deletions += linesDeleted;
                System.out.println("\t\tLines: " + linesAdded + " added, " + linesDeleted + " deleted");
            }

            System.out.println("Total: " + additions + " lines added, " + deletions + " lines deleted");
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
    protected void loadRootObject() {
        File file = new File(ROOT_TEMP_DIR + getProjectDir());
        if (file.exists()) DataPump.deleteTempDir(file);

        CloneCommand cloneCommand = Git.cloneRepository()
                .setBare(true)
                .setURI(projectHandle)
                .setGitDir(file);

        if (username != null)
            cloneCommand.setCredentialsProvider(new UsernamePasswordCredentialsProvider(username, password));

        if (privateKeyLoc != null) cloneCommand = authenticate(cloneCommand);

        Git git = null;
        try {
            git = cloneCommand.call();
        } catch (GitAPIException e) {
            e.printStackTrace();
        }
        repository = git.getRepository();
    }

    private CloneCommand authenticate(CloneCommand cloneCommand) {
        SshSessionFactory sshSessionFactory = new JschConfigSessionFactory() {
            @Override
            protected void configure(OpenSshConfig.Host host, Session session) {
            }

            @Override
            protected JSch createDefaultJSch(FS fs) throws JSchException {
                JSch defaultJSch = super.createDefaultJSch(fs);
                defaultJSch.addIdentity(privateKeyLoc);
                return defaultJSch;
            }
        };

        cloneCommand.setTransportConfigCallback(new TransportConfigCallback() {
            @Override
            public void configure(Transport transport) {
                SshTransport sshTransport = (SshTransport) transport;
                sshTransport.setSshSessionFactory(sshSessionFactory);
            }
        });
        return cloneCommand;
    }

    @Override
    protected Date convertDate(Object date) {
        long milliseconds = Long.parseLong(date.toString());
        milliseconds *= 1000;
        return new Date(milliseconds);
    }
}
