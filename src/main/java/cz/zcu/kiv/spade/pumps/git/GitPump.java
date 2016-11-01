package cz.zcu.kiv.spade.pumps.git;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import cz.zcu.kiv.spade.domain.*;
import cz.zcu.kiv.spade.domain.enums.Tool;
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
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class GitPump extends DataPump {

    private Repository repository;
    private Map<String, Set<String>> commitBranches = new TreeMap<>();

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

        ToolInstance ti = new ToolInstance(0, getServer(), Tool.GIT, "");

        Project project = new Project(0, "", projectName, "", null, null, null, new LinkedHashSet<>(), new LinkedHashSet<>());

        ProjectInstance pi = new ProjectInstance(0, "", projectName, "", ti, project, projectHandle);

        //pi.setExternalId();
        //pi.setDescription();

        List<Ref> branches = new LinkedList<>();
        try {
            branches = git.branchList().call();
        } catch (GitAPIException e) {
            e.printStackTrace();
        }

        for (Ref branchRef : branches){
            Branch branch = new Branch();
            branch.setExternalId(branchRef.getName());
            branch.setName(stripFileName(branchRef.getName()));
            if (branchRef.getName().equals("master")) {
                branch.setIsMain(true);
            }
            mineCommits(branch);
        }

        /*System.out.println();
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
        }*/
    }

    private void mineCommits(Branch branch) {

        RevWalk revWalk = new RevWalk(repository);

        try {
            ObjectId commitId = repository.resolve(branch.getName());
            revWalk.markStart(revWalk.parseCommit(commitId));
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (RevCommit commit : revWalk) {
            Configuration configuration = mineCommit(commit);
            configuration.setBranch(branch);
            if (commitBranches.containsKey(commit.getId().getName())) {
                commitBranches.get(commit.getId().getName()).add(stripFileName(branch.getName()));
            } else {
                Set<String> branchNames = new HashSet<>();
                branchNames.add(stripFileName(branch.getName()));
                commitBranches.put(commit.getId().getName(), branchNames);
            }
        }
    }

    private Configuration mineCommit(RevCommit commit) {
        Identity authorI = new Identity();
        authorI.setName(commit.getAuthorIdent().getName());
        authorI.setEmail(commit.getAuthorIdent().getEmailAddress());

        Person author = new Person();
        author.setName(authorI.getName());
        author.getIdentities().add(authorI);

        Identity committerI = new Identity();
        committerI.setName(commit.getCommitterIdent().getName());
        committerI.setEmail(commit.getCommitterIdent().getEmailAddress());

        Person committer = new Person();
        committer.setName(authorI.getName());
        committer.getIdentities().add(committerI);

        ConfigPersonRelation relation = new ConfigPersonRelation();
        relation.setAuthor(committer);
        relation.setName("Committed-by");

        Configuration configuration = new Configuration();
        configuration.setExternalId(commit.getId().toString());
        configuration.setName(commit.getId().getName());
        configuration.setDescription(commit.getFullMessage());
        configuration.setCreated(convertDate(commit.getCommitTime()));
        configuration.setAuthor(author);
        configuration.setWorkUnits(getAssociatedWorkUnits(commit));
        configuration.getRelations().addAll(getRelatedPeople(commit));
        configuration.setChanges(mineChanges(commit, configuration));

        return configuration;
    }

    private Set<ConfigPersonRelation> getRelatedPeople(RevCommit commit) {
        Set<ConfigPersonRelation> relations = new HashSet<>();
        for (FooterLine line : commit.getFooterLines()) {
            if (line.getKey().endsWith("-by") || line.getKey().equals("CC")) {
                Identity identity = new Identity();
                String[] parts = line.getValue().split("<");
                identity.setName(parts[0].trim());
                if(parts.length > 1) identity.setEmail(parts[1].substring(0, parts[1].length() - 1));

                Person person = new Person();
                person.setName(identity.getName());
                person.getIdentities().add(identity);

                ConfigPersonRelation relation = new ConfigPersonRelation();
                relation. setAuthor(person);
                relation.setName(line.getKey());

                relations.add(relation);
            }
        }
        return relations;
    }

    protected Set<WorkUnit> getAssociatedWorkUnits(RevCommit commit) {
        Set<WorkUnit> associated = new HashSet<>();

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
                    WorkUnit wu = new WorkUnit();
                    wu.setNumber(Integer.parseInt(mention));
                    associated.add(wu);
                }
            }
        }
        return associated;
    }

    private Set<WorkItemChange> mineChanges(RevCommit commit, Configuration configuration) {
        Set<WorkItemChange> changes = new LinkedHashSet<>();
        try {
            RevTree parentTree = null;
            if (commit.getParentCount() != 0) parentTree = commit.getParent(0).getTree();

            DiffFormatter df = new DiffFormatter(DisabledOutputStream.INSTANCE);
            df.setRepository(repository);
            df.setDiffComparator(RawTextComparator.DEFAULT);
            df.setDetectRenames(true);

            List<DiffEntry> diffs = df.scan(parentTree, commit.getTree());

            for (DiffEntry diff : diffs) {
                int linesAdded = 0, linesDeleted = 0;

                WorkItemChange change = mineChange(diff);

                for (Edit edit : df.toFileHeader(diff).toEditList()) {
                    linesDeleted += edit.getLengthA();
                    linesAdded += edit.getLengthB();

                    String desc = change.getDescription() + "\\ttEdit type: " + edit.getType().toString() + "\n";
                    desc += "\t\t\tLines: " + edit.getLengthB() + " added, " + edit.getLengthA() + " deleted" + "\n";

                    change.setDescription(desc);
                }
                String desc = change.getDescription() + "\tLines: " + linesAdded + " added, " + linesDeleted + " deleted" + "\n";
                change.setDescription(desc);

                System.out.println(change);

                changes.add(change);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return changes;
    }

    private WorkItemChange mineChange(DiffEntry diff) {
        Artifact artifact = new Artifact();

        String type = diff.getChangeType().name();
        String desc = "";
        String newFileName = stripFileName(diff.getNewPath());
        String newFileDir = diff.getNewPath().substring(0, diff.getNewPath().lastIndexOf(newFileName));
        String oldFileName = stripFileName(diff.getOldPath());
        String oldFileDir = diff.getOldPath().substring(0, diff.getOldPath().lastIndexOf(oldFileName));

        if (type.equals("DELETE")) {
            desc += "\tfrom: " + diff.getOldPath();
            artifact.setName(oldFileName);
        } else if (type.equals("ADD") || type.equals("MODIFY")) {
            desc += "\tto: " + diff.getNewPath();
            artifact.setName(newFileName);
        } else {
            if (type.equals("RENAME")) {
                if (newFileName.equals(oldFileName)) type = "MOVE";
                else if (!newFileDir.equals(oldFileDir)) type += " AND MOVE";
            }
            if (diff.getScore() < 100) type += " AND MODIFY";
            desc += "\tfrom: " + diff.getOldPath() + "\n" +
                    "\tto: " + diff.getNewPath() + "\n" +
                    "\tscore: " + diff.getScore();
            artifact.setName(newFileName);
        }

        WorkItemChange change = new WorkItemChange();
        change.setChangedItem(artifact);
        change.setDescription("Change type: " + type + "\n" + desc + "\n");

        return change;
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
