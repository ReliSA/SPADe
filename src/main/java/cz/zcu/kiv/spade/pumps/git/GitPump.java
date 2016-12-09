package cz.zcu.kiv.spade.pumps.git;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import cz.zcu.kiv.spade.domain.*;
import cz.zcu.kiv.spade.domain.enums.ArtifactClass;
import cz.zcu.kiv.spade.domain.enums.Tool;
import cz.zcu.kiv.spade.pumps.abstracts.VCSPump;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.TransportConfigCallback;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.*;
import org.eclipse.jgit.lib.*;
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
public class GitPump extends VCSPump<Repository> {

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

        Map<String, Configuration> configurationMap = mineBranches();
        addTags(configurationMap);

        List<Configuration> list = sortConfigsByDate(configurationMap.values());
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

    @Override
    public Map<String, Configuration> mineBranches() {
        List<Ref> branches = new LinkedList<>();
        try {
            Git git = new Git(rootObject);
            branches = git.branchList().call();
        } catch (GitAPIException e) {
            e.printStackTrace();
        }

        Map<String, Configuration> configurationMap = new HashMap<>();
        for (Ref branchRef : branches) {
            Branch branch = generateBranch(branchRef);
            mineCommits(configurationMap, branch);
        }

        return configurationMap;
    }

    @Override
    public void addTags(Map<String, Configuration> configurationMap) {
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

                Configuration config = configurationMap.get(commitSHA);
                config.setIsRelease(true);
                config.getTags().add(tag);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        walk.dispose();
    }

    @Override
    public void close() {
        rootObject.close();
        super.close();
    }

    /**
     * mines data from all commits associated with a particular branch
     *
     * @param branch branch to mine commits from
     */
    private void mineCommits(Map<String, Configuration> configurationMap, Branch branch) {
        // TODO branch determination
        RevWalk revWalk = new RevWalk(rootObject);

        try {
            ObjectId commitId = rootObject.resolve(branch.getName());
            revWalk.markStart(revWalk.parseCommit(commitId));
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (RevCommit commit : revWalk) {
            Configuration configuration;
            if (!configurationMap.containsKey(commit.getId().getName())) {
                configuration = mineCommit(commit);
                configurationMap.put(commit.getId().getName(), configuration);
            } else {
                configuration = configurationMap.get(commit.getId().getName());
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
        Configuration configuration = new Configuration();

        configuration.setExternalId(commit.getId().toString());
        configuration.setName(commit.getId().getName());
        configuration.setDescription(commit.getFullMessage());
        configuration.setCommitted(commit.getCommitterIdent().getWhen());
        configuration.setCreated(commit.getAuthorIdent().getWhen());
        configuration.setAuthor(generatePerson(commit.getAuthorIdent()));
        configuration.setChanges(mineChanges(commit));
        configuration.setWorkUnits(collectAssociatedWorkUnits(commit));
        configuration.setRelations(collectRelatedPeople(commit));

        /*for (RevCommit parentCommit : commit.getParents()) {
            Configuration parent = new Configuration();
            parent.setExternalId(parentCommit.getId().toString());
            configuration.getParents().add(parent);
        }*/

        return configuration;
    }

    /**
     * mines all individual file changes in a given commit
     *
     * @param commit commit to be mined
     * @return changes associated with the commit
     */
    private List<WorkItemChange> mineChanges(RevCommit commit) {
        List<WorkItemChange> changes = new ArrayList<>();
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

    /**
     * mines work units mentioned in a commit message
     *
     * @param commit commit to be analysed
     * @return collection of mentioned work units
     */
    private Collection<WorkUnit> collectAssociatedWorkUnits(RevCommit commit) {
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
     * gets all relations of people (except for the author) involved in a commit based on commit message analysis
     *
     * @param commit commit to be analysed
     * @return collection of relations of people to the commit
     */
    private Collection<ConfigPersonRelation> collectRelatedPeople(RevCommit commit) {
        Set<ConfigPersonRelation> relations = new HashSet<>();

        ConfigPersonRelation relation = new ConfigPersonRelation();
        relation.setPerson(generatePerson(commit.getCommitterIdent()));
        relation.setName("Committed-by");
        relations.add(relation);

        for (FooterLine line : commit.getFooterLines()) {
            if (line.getKey().endsWith("-by") || line.getKey().equals("CC")) {
                String[] parts = line.getValue().split("<");
                String name = parts[0].trim();
                String email = "";
                if (parts.length > 1) email = parts[1].substring(0, parts[1].length() - 1);

                relation = new ConfigPersonRelation();
                relation.setPerson(generatePerson(new PersonIdent(name, email)));
                relation.setName(line.getKey());

                relations.add(relation);
            }
        }
        return relations;
    }

    /**
     * get SPADe branch object from Git branch reference
     *
     * @param branchRef branch reference from Git
     * @return branch object from SPADe
     */
    private Branch generateBranch(Ref branchRef) {
        Branch branch = new Branch();
        branch.setExternalId(branchRef.getName());
        branch.setName(stripFileName(branchRef.getName()));
        if (branchRef.getName().endsWith("master")) {
            branch.setIsMain(true);
        }
        return branch;
    }

    private Person generatePerson(PersonIdent user) {
        if (user == null) return null;

        Identity identity = new Identity();
        identity.setName(user.getName());
        identity.setEmail(user.getEmailAddress());

        Person person = new Person();
        person.getIdentities().add(identity);
        return person;
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

    /**
     * gets project name
     *
     * @return project name
     */
    public String getProjectName() {
        return projectHandle.substring(projectHandle.lastIndexOf("/") + 1, projectHandle.lastIndexOf(".git"));
    }
}
