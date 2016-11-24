package cz.zcu.kiv.spade.pumps.git;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import cz.zcu.kiv.spade.domain.*;
import cz.zcu.kiv.spade.domain.enums.ArtifactClass;
import cz.zcu.kiv.spade.domain.enums.Tool;
import cz.zcu.kiv.spade.pumps.DataPump;
import cz.zcu.kiv.spade.pumps.VCSPump;
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
import org.eclipse.jgit.util.FS;
import org.eclipse.jgit.util.io.DisabledOutputStream;

import java.io.File;
import java.io.IOException;
import java.net.URLConnection;
import java.util.*;

/**
 * data pump specific for mining Git repositories
 */
public class GitPump extends DataPump<Repository> implements VCSPump{

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
    public ProjectInstance mineData() {
        ToolInstance ti = new ToolInstance();
        ti.setExternalId(getServer());
        ti.setTool(Tool.GIT);
        Project project = new Project();
        project.setName(getProjectName());
        ProjectInstance pi = new ProjectInstance();
        pi.setName(getProjectName());
        pi.setUrl(projectHandle);
        pi.setToolInstance(ti);

        mineBranches();

        List<Configuration> list = sortConfigsByDate();
        list = cleanUpConfList(list);
        project.setConfigurations(list);

        project.setStartDate(list.get(0).getCreated());

        for (Configuration conf : project.getConfigurations()) {
            for (WorkUnit unit : conf.getWorkUnits()) {
                unit.setProject(project);
            }
        }

        pi.setProject(project);

        return pi;
    }

    /**
     * mines data one branch after another while storing date in private fields
     */
    private void mineBranches() {
        List<Ref> branches = new LinkedList<>();
        Git git = new Git(rootObject);

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
    public Map<String, Set<VCSTag>> loadTags() {
        Map<String, Set<VCSTag>> tags = new HashMap<>();
        RevWalk walk = new RevWalk(rootObject);

        for (Map.Entry<String, Ref> entry : rootObject.getTags().entrySet()) {
            VCSTag tag = new VCSTag();
            tag.setName(entry.getKey());

            try {
                Ref tagRef = rootObject.findRef(entry.getKey());
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
    public void mineCommits(Branch branch) {
        // TODO branch determination
        Map<String, Set<VCSTag>> tags = loadTags();
        RevWalk revWalk = new RevWalk(rootObject);

        try {
            ObjectId commitId = rootObject.resolve(branch.getName());
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
        Identity authorIdent = new Identity();
        authorIdent.setName(commit.getAuthorIdent().getName());
        authorIdent.setEmail(commit.getAuthorIdent().getEmailAddress());

        Person author = new Person();
        author.getIdentities().add(authorIdent);

        Configuration configuration = new Configuration();
        configuration.setExternalId(commit.getId().toString());
        configuration.setName(commit.getId().getName());
        configuration.setDescription(commit.getFullMessage());
        configuration.setCommitted(commit.getCommitterIdent().getWhen());
        configuration.setCreated(commit.getAuthorIdent().getWhen());
        configuration.setAuthor(author);
        configuration.setWorkUnits(getAssociatedWorkUnits(commit));
        configuration.setChanges(mineChanges(commit));
        configuration.setRelations(getRelatedPeople(commit));

        /*for (RevCommit parentCommit : commit.getParents()) {
            Configuration parent = new Configuration();
            parent.setExternalId(parentCommit.getId().toString());
            configuration.getParents().add(parent);
        }*/

        return configuration;
    }

    /**
     * gets all relations of people (except for the author) involved in a commit based on commit message analysis
     *
     * @param commit commit to be analysed
     * @return collection of relations of people to the commit
     */
    private Collection<ConfigPersonRelation> getRelatedPeople(RevCommit commit) {
        Set<ConfigPersonRelation> relations = new HashSet<>();

        Identity committerIdent = new Identity();
        committerIdent.setName(commit.getCommitterIdent().getName());
        committerIdent.setEmail(commit.getCommitterIdent().getEmailAddress());

        Person committer = new Person();
        committer.getIdentities().add(committerIdent);

        ConfigPersonRelation relation = new ConfigPersonRelation();
        relation.setPerson(committer);
        relation.setName("Committed-by");
        relations.add(relation);

        for (FooterLine line : commit.getFooterLines()) {
            if (line.getKey().endsWith("-by") || line.getKey().equals("CC")) {
                String[] parts = line.getValue().split("<");
                String name = parts[0].trim();
                String email = null;
                if (parts.length > 1) email = parts[1].substring(0, parts[1].length() - 1);

                Identity identity = new Identity();
                identity.setName(name);
                identity.setEmail(email);

                Person person = new Person();
                person.getIdentities().add(identity);

                relation = new ConfigPersonRelation();
                relation.setPerson(person);
                relation.setName(line.getKey());

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
            df.setRepository(rootObject);
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
        WorkItemChange change = new WorkItemChange();
        Artifact artifact = new Artifact();

        String type = diff.getChangeType().name();
        String desc = "";
        String newFileName = stripFileName(diff.getNewPath());
        String newFileDir = diff.getNewPath().substring(0, diff.getNewPath().lastIndexOf(newFileName));
        String oldFileName = stripFileName(diff.getOldPath());
        String oldFileDir = diff.getOldPath().substring(0, diff.getOldPath().lastIndexOf(oldFileName));

        artifact.setName(newFileName);
        artifact.setUrl(diff.getNewPath());
        artifact.setArtifactClass(ArtifactClass.FILE);
        if (artifact.getName().contains(".")) {
            artifact.setMimeType(URLConnection.guessContentTypeFromName(artifact.getName()));
            if (artifact.getMimeType() == null) {
                artifact.setMimeType(artifact.getName().substring(artifact.getName().lastIndexOf(".") + 1));
            }
        }

        FieldChange fChange = new FieldChange();
        fChange.setName("url");
        fChange.setOldValue(diff.getOldPath());
        fChange.setNewValue(diff.getNewPath());
        change.getFieldChanges().add(fChange);

        if (type.equals("RENAME") || type.equals("COPY")) {
            if (type.equals("RENAME")) {
                if (newFileName.equals(oldFileName)) type = "MOVE";
                else if (!newFileDir.equals(oldFileDir)) type += " AND MOVE";
            }
            if (diff.getScore() < 100) type += " AND MODIFY";
            desc += "score: " + diff.getScore() + "\n";
        }

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
    protected Repository init() {
        Repository repo = null;

        File file = new File(ROOT_TEMP_DIR + getProjectDir());

        CloneCommand cloneCommand = Git.cloneRepository()
                .setBare(true)
                .setURI(projectHandle)
                .setGitDir(file);

        if (username != null)
            cloneCommand.setCredentialsProvider(new UsernamePasswordCredentialsProvider(username, password));

        if (privateKeyLoc != null) cloneCommand = authenticate(cloneCommand);

        try {
            Git git = cloneCommand.call();
            repo = git.getRepository();
        } catch (GitAPIException e) {
            e.printStackTrace();
        }
        return repo;
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

    @Override
    public void close() {
        rootObject.close();
        super.close();
    }
}
