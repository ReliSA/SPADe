package cz.zcu.kiv.spade.pumps.git;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import cz.zcu.kiv.spade.domain.*;
import cz.zcu.kiv.spade.domain.enums.ArtifactClass;
import cz.zcu.kiv.spade.domain.enums.Tool;
import cz.zcu.kiv.spade.pumps.DataPump;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.TransportConfigCallback;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.*;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.*;
import org.eclipse.jgit.transport.*;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.util.FS;
import org.eclipse.jgit.util.io.DisabledOutputStream;

import java.io.File;
import java.io.IOException;
import java.net.URLConnection;
import java.util.*;

public class GitPump extends DataPump {

    /**
     * JGit root object for mining data
     */
    private Repository repository;

    /**
     * @param projectHandle URL of the project instance
     */
    public GitPump(String projectHandle) {
        super(projectHandle);
    }

    /**
     * @param projectHandle URL of the project instance
     * @param username      username for authenticated login
     * @param password      password for authenticated login
     */
    public GitPump(String projectHandle, String username, String password) {
        super(projectHandle, username, password);
    }

    /**
     * @param projectHandle URL of the project instance
     * @param privateKeyLoc private key location for authenticated login
     */
    public GitPump(String projectHandle, String privateKeyLoc) {
        super(projectHandle, privateKeyLoc);
    }

    /**
     * @param projectHandle URL of the project instance
     * @param privateKeyLoc private key location for authenticated login
     * @param username      username for authenticated login
     * @param password      password for authenticated login
     */
    public GitPump(String projectHandle, String privateKeyLoc, String username, String password) {
        super(projectHandle, privateKeyLoc, username, password);
    }

    @Override
    public void mineData() {
        loadRootObject();

        ToolInstance ti = new ToolInstance(1L, getServer(), Tool.GIT, "");
        Project project = new Project();
        project.setName(projectName);
        ProjectInstance pi = new ProjectInstance();
        pi.setName(projectName);
        pi.setUrl(projectHandle);
        pi.setToolInstance(ti);

        mineBranches();
        List<Configuration> list = sortConfigsByDate();

        project.setPersonnel(people);
        project.setConfigurations(list);
        project.setStartDate(list.get(0).getCreated());

        pi.setProject(project);

        printReport(pi);
    }

    /**
     * mines data one branch after another while storing date in private fields
     */
    private void mineBranches() {
        List<Ref> branches = new LinkedList<>();
        Git git = new Git(repository);

        try {
            branches = git.branchList().call();
        } catch (GitAPIException e) {
            e.printStackTrace();
        }

        for (Ref branchRef : branches) {
            Branch branch = getBranch(branchRef);
            mineCommits(branch);
        }
    }

    /**
     * returns configurations in a form of a list sorted by date from earliest
     *
     * @return sorted list of configurations
     */
    private List<Configuration> sortConfigsByDate() {
        List<Configuration> list = new ArrayList<>();
        list.addAll(configurations.values());
        list.sort(new Comparator<Configuration>() {
            @Override
            public int compare(Configuration o1, Configuration o2) {
                int ret = o1.getCommitted().compareTo(o2.getCommitted());
                if (ret != 0) return ret;
                else return o1.getCreated().compareTo(o2.getCreated());
            }
        });
        return list;
    }

    /**
     * get SPADe branch object from Git branch reference
     *
     * @param branchRef branch reference from Git
     * @return branch object from SPADe
     */
    private Branch getBranch(Ref branchRef) {
        Branch branch = new Branch();
        branch.setExternalId(branchRef.getName());
        branch.setName(stripFileName(branchRef.getName()));
        if (branchRef.getName().endsWith("master")) {
            branch.setIsMain(true);
        }
        return branch;
    }

    @Override
    protected Map<String, Set<VCSTag>> loadTags() {
        Map<String, Set<VCSTag>> tags = new HashMap<>();
        RevWalk walk = new RevWalk(repository);

        for (Map.Entry<String, Ref> entry : repository.getTags().entrySet()) {
            VCSTag tag = new VCSTag();
            tag.setName(entry.getKey());

            try {
                Ref tagRef = repository.findRef(entry.getKey());
                RevObject any = walk.parseAny(tagRef.getObjectId());
                tag.setExternalId(any.getId().toString());

                String commitSHA;
                // annotated tag
                if (any.getType() == Constants.OBJ_TAG) {
                    commitSHA = ((RevTag) any).getObject().getId().getName();
                    // not annotated tag
                } else {
                    commitSHA = any.getId().getName();
                }

                if (!tags.containsKey(commitSHA)) {
                    tags.put(commitSHA, new HashSet<>());
                }
                tags.get(commitSHA).add(tag);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        walk.dispose();
        return tags;
    }

    @Override
    protected void mineCommits(Branch branch) {
        // TODO branch determination
        Map<String, Set<VCSTag>> tags = loadTags();
        RevWalk revWalk = new RevWalk(repository);

        try {
            ObjectId commitId = repository.resolve(branch.getName());
            revWalk.markStart(revWalk.parseCommit(commitId));
        } catch (IOException e) {
            e.printStackTrace();
        }


        for (RevCommit commit : revWalk) {
            Configuration configuration;
            if (!configurations.containsKey(commit.getId().toString())) {
                configuration = mineCommit(commit);
                if (tags.containsKey(configuration.getName())) {
                    configuration.getTags().addAll(tags.get(configuration.getName()));
                    configuration.setIsRelease(true);
                }
                configurations.put(commit.getId().toString(), configuration);
            } else {
                configuration = configurations.get(commit.getId().toString());
            }
            configuration.getBranches().add(branch);
        }
        revWalk.dispose();
    }

    /**
     * mines data from a particular commit and returns them in the form of Configuration
     *
     * @param commit commit to mine
     * @return configuration object with commit's data
     */
    private Configuration mineCommit(RevCommit commit) {
        Person author = addPerson(commit.getAuthorIdent().getName(), commit.getAuthorIdent().getEmailAddress());

        Configuration configuration = new Configuration();
        configuration.setExternalId(commit.getId().toString());
        configuration.setName(commit.getId().getName());
        configuration.setDescription(commit.getFullMessage());
        configuration.setCommitted(commit.getCommitterIdent().getWhen());
        configuration.setCreated(commit.getAuthorIdent().getWhen());
        configuration.setAuthor(author);
        configuration.setWorkUnits(getAssociatedWorkUnits(commit));
        configuration.setChanges(mineChanges(commit));
        configuration.setArtifacts(getArtifactsInConf(commit));
        configuration.setRelations(getRelatedPeople(commit));

        return configuration;
    }

    /**
     * gets all the files present in repository in time of a given commit
     *
     * @param commit commit to mine
     * @return all files in the commit
     */
    private Collection<Artifact> getArtifactsInConf(RevCommit commit) {
        Collection<Artifact> artifacts = new HashSet<>();
        TreeWalk treeWalk = new TreeWalk(repository);

        try {

            treeWalk.addTree(commit.getTree());
            treeWalk.setRecursive(true);
            while (treeWalk.next()) {
                String id = treeWalk.getObjectId(0).getName();

                Artifact artifact = new Artifact();
                String fileName = treeWalk.getNameString();
                artifact.setExternalId(id);
                artifact.setArtifactClass(ArtifactClass.FILE);
                artifact.setName(fileName);
                artifact.setUrl(projectHandle + "/" + treeWalk.getPathString());
                if (fileName.contains(".")) {
                    artifact.setMimeType(URLConnection.guessContentTypeFromName(fileName));
                    File file = new File(artifact.getUrl());
                    if (artifact.getMimeType() == null) {
                        artifact.setMimeType(fileName.substring(fileName.lastIndexOf(".") + 1));
                    }
                }

                artifacts.add(artifact);
            }
            treeWalk.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return artifacts;
    }

    /**
     * gets all relations of people (except for the author) involved in a commit based on commit message analysis
     *
     * @param commit commit to be analysed
     * @return collection of relations of people to the commit
     */
    private Collection<ConfigPersonRelation> getRelatedPeople(RevCommit commit) {
        Set<ConfigPersonRelation> relations = new HashSet<>();

        Person committer = addPerson(commit.getCommitterIdent().getName(), commit.getCommitterIdent().getEmailAddress());

        ConfigPersonRelation relation = new ConfigPersonRelation();
        relation.setPerson(committer);
        relation.setDescription("Committed-by");
        relations.add(relation);

        for (FooterLine line : commit.getFooterLines()) {
            if (line.getKey().endsWith("-by") || line.getKey().equals("CC")) {
                String[] parts = line.getValue().split("<");
                String name = parts[0].trim();
                String email = null;
                if (parts.length > 1) email = parts[1].substring(0, parts[1].length() - 1);
                addPerson(name, email);

                relation = new ConfigPersonRelation();
                relation.setPerson(addPerson(name, email));
                relation.setDescription(line.getKey());

                relations.add(relation);
            }
        }
        return relations;
    }

    /**
     * mines work units mentioned in a commit message
     *
     * @param commit commit to be analysed
     * @return collection of mentioned work units
     */
    private Collection<WorkUnit> getAssociatedWorkUnits(RevCommit commit) {
        Set<WorkUnit> units = new HashSet<>();

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
                    units.add(wu);
                }
            }
        }
        return units;
    }

    /**
     * mines all individual file changes in a given commit
     *
     * @param commit commit to be mined
     * @return changes associated with the commit
     */
    private Collection<WorkItemChange> mineChanges(RevCommit commit) {
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
                WorkItemChange change = mineChange(diff, df.toFileHeader(diff).toEditList());
                changes.add(change);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return changes;
    }

    /**
     * mines a particular change of a file
     *
     * @param diff  difference between before- and after-commit version of a file
     * @param edits edited segments of the file
     * @return data in a work item change form
     */
    private WorkItemChange mineChange(DiffEntry diff, EditList edits) {
        Artifact artifact = new Artifact();

        String type = diff.getChangeType().name();
        String desc = "";
        String newFileName = stripFileName(diff.getNewPath());
        String newFileDir = diff.getNewPath().substring(0, diff.getNewPath().lastIndexOf(newFileName));
        String oldFileName = stripFileName(diff.getOldPath());
        String oldFileDir = diff.getOldPath().substring(0, diff.getOldPath().lastIndexOf(oldFileName));

        switch (type) {
            case "DELETE":
                desc += "from: " + diff.getOldPath() + "\n";
                artifact.setName(oldFileName);
                break;
            case "ADD":
            case "MODIFY":
                desc += "to: " + diff.getNewPath() + "\n";
                artifact.setName(newFileName);
                break;
            default:
                if (type.equals("RENAME")) {
                    if (newFileName.equals(oldFileName)) type = "MOVE";
                    else if (!newFileDir.equals(oldFileDir)) type += " AND MOVE";
                }
                if (diff.getScore() < 100) type += " AND MODIFY";
                desc += "from: " + diff.getOldPath() + "\n" +
                        "to: " + diff.getNewPath() + "\n" +
                        "score: " + diff.getScore() + "\n";
                artifact.setName(newFileName);
                break;
        }

        WorkItemChange change = new WorkItemChange();
        change.setChangedItem(artifact);
        change.setName(type);

        int linesAdded = 0, linesDeleted = 0;
        for (Edit edit : edits) {
            linesDeleted += edit.getLengthA();
            linesAdded += edit.getLengthB();
        }
        desc += linesAdded + " lines added, " + linesDeleted + " lines deleted";
        change.setDescription(desc);

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

        try {
            Git git = cloneCommand.call();
            repository = git.getRepository();
        } catch (GitAPIException e) {
            e.printStackTrace();
        }
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
}
