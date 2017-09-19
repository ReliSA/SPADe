package cz.zcu.kiv.spade.pumps.impl;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import cz.zcu.kiv.spade.App;
import cz.zcu.kiv.spade.domain.*;
import cz.zcu.kiv.spade.domain.enums.ArtifactClass;
import cz.zcu.kiv.spade.domain.enums.Tool;
import cz.zcu.kiv.spade.pumps.abstracts.VCSPump;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.*;
import org.eclipse.jgit.lib.*;
import org.eclipse.jgit.revwalk.*;
import org.eclipse.jgit.transport.*;
import org.eclipse.jgit.util.FS;
import org.eclipse.jgit.util.io.DisabledOutputStream;

import javax.persistence.EntityManager;
import java.io.File;
import java.io.IOException;
import java.net.URLConnection;
import java.util.*;

/**
 * data pump specific for mining Git repositories
 *
 * @author Petr Pícha
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
        this.tool = Tool.GIT;
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

        if (privateKeyLoc != null) cloneCommand = authenticate(cloneCommand);

        try {
            Git git = cloneCommand.call();
            repo = git.getRepository();
        } catch (GitAPIException e) {
            e.printStackTrace();
        }
        App.printLogMsg("Git repository cloned");
        return repo;
    }

    @Override
    public ProjectInstance mineData(EntityManager em) {
        pi = super.mineData(em);

        setToolInstance();

        mineBranches();
        addTags();

        List<Configuration> list = sortConfigsByDate();
        list = cleanUpCommitList(list);
        pi.getProject().getConfigurations().addAll(list);

        pi.getProject().setStartDate(list.get(0).getCreated());

        addWorkItemAuthors();

        mineMentions();

        return pi;
    }

    @Override
    protected void mineMentions() {
        // from commit messages
        for (Commit commit : pi.getProject().getCommits()) {
            mineMentionedGitCommits(commit, commit.getDescription());
        }
    }

    @Override
    public void mineBranches() {
        List<Ref> branches = new LinkedList<>();
        try {
            Git git = new Git(rootObject);
            branches = git.branchList().call();
        } catch (GitAPIException e) {
            e.printStackTrace();
        }

        int i = 1;
        for (Ref branchRef : branches) {
            Branch branch = generateBranch(branchRef);
            App.printLogMsg("started mining branch \"" + branch.getName() + "\" (" + i + "/" + branches.size() + ")");
            mineCommits(branch);
            i++;
        }
    }

    @Override
    public void addTags() {
        RevWalk walk = new RevWalk(rootObject);

        for (Map.Entry<String, Ref> entry : rootObject.getTags().entrySet()) {

            RevObject any = null;
            try {
                Ref tagRef = rootObject.findRef(entry.getKey());
                any = walk.parseAny(tagRef.getObjectId());
            } catch (IOException | ClassCastException e) {
                e.printStackTrace();
            }
            if (any == null) continue;

            VCSTag tag = new VCSTag();
            tag.setName(entry.getKey());
            tag.setExternalId(any.getId().toString());

            String commitSHA;
            // annotated tag
            if (any.getType() == Constants.OBJ_TAG) {
                commitSHA = ((RevTag) any).getObject().getId().getName();
                // not annotated tag
            } else {
                commitSHA = any.getId().getName();
            }
            commitSHA = commitSHA.substring(0, 7);

            if (!pi.getProject().containsCommit(commitSHA)) {
                if (any instanceof RevCommit) {
                    mineCommit((RevCommit) any);
                } else {
                    try {
                        commitSHA = any.getId().getName().substring(0, 7);
                        if (!pi.getProject().containsCommit(commitSHA)) {
                            RevCommit commit = rootObject.parseCommit(any.getId());
                            mineCommit(commit);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            Commit commit = pi.getProject().getCommit(commitSHA);
            if (commit != null) {
                commit.getTags().add(tag);
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
    private void mineCommits(Branch branch) {
        // TODO branch determination
        RevWalk revWalk = new RevWalk(rootObject);

        try {
            ObjectId commitId = rootObject.resolve(branch.getName());
            revWalk.markStart(revWalk.parseCommit(commitId));
        } catch (IOException e) {
            e.printStackTrace();
        }

        int count = 0, original = 0;
        for (RevCommit gitCommit : revWalk) {
            String shortSHA = gitCommit.getId().getName().substring(0, 7);
            if (!pi.getProject().containsCommit(shortSHA)) {
                mineCommit(gitCommit);
                original++;
            }
            Commit commit = pi.getProject().getCommit(shortSHA);
            commit.getBranches().add(branch);
            count++;
            if ((count % 5000) == 0) App.printLogMsg(count + " commits mined");
        }
        App.printLogMsg(count + " commits mined (" + original + " originals)");
        revWalk.dispose();
    }

    /**
     * mines data from a particular commit and returns them in the form of Configuration
     *
     * @param gitCommit commit to mine
     */
    private void mineCommit(RevCommit gitCommit) {
        Commit commit = new Commit();
        commit.setIdentifier(gitCommit.getId().getName().substring(0, 7));
        commit.setExternalId(gitCommit.getId().toString());
        commit.setName(gitCommit.getId().getName());
        commit.setDescription(gitCommit.getFullMessage().trim());
        commit.setCommitted(gitCommit.getCommitterIdent().getWhen());
        commit.setCreated(gitCommit.getAuthorIdent().getWhen());
        commit.setAuthor(addPerson(generateIdentity(gitCommit.getAuthorIdent())));
        commit.setChanges(mineChanges(gitCommit));
        commit.setRelations(collectRelatedPeople(gitCommit));

        /*for (RevCommit parentCommit : commit.getParents()) {
            Configuration parent = new Configuration();
            parent.setExternalId(parentCommit.getId().toString());
            configuration.getParents().add(parent);
        }*/

        pi.getProject().addCommit(commit);
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
     * gets all relations of people (except for the author) involved in a commit based on commit message analysis
     *
     * @param commit commit to be analysed
     * @return collection of relations of people to the commit
     */
    private Collection<ConfigPersonRelation> collectRelatedPeople(RevCommit commit) {
        Set<ConfigPersonRelation> relations = new HashSet<>();

        ConfigPersonRelation relation = new ConfigPersonRelation();
        relation.setPerson(addPerson(generateIdentity(commit.getCommitterIdent())));
        relation.setName("Committed-by");
        relations.add(relation);

        for (FooterLine line : commit.getFooterLines()) {
            if (line.getKey().endsWith("-by") || line.getKey().equals("CC")) {
                String[] parts = line.getValue().split("<");
                String name = parts[0].trim();
                String email = "";
                if (parts.length > 1) email = parts[1].substring(0, parts[1].length() - 1).trim();

                ConfigPersonRelation relation2 = new ConfigPersonRelation();
                relation2.setPerson(addPerson(generateIdentity(new PersonIdent(name, email))));
                relation2.setName(line.getKey());

                relations.add(relation2);
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
        branch.setName(stripBranchName(branchRef.getName()));
        if (branchRef.getName().endsWith("master")) {
            branch.setIsMain(true);
        }
        return branch;
    }

    private String stripBranchName(String name) {
        return name.replace("refs/heads/", "");
    }

    /**
     * generates SPADe Identity instance based on Git user data
     * @param user Git user
     * @return Identity instance
     */
    private Identity generateIdentity(PersonIdent user) {

        Identity identity = new Identity();
        if (user.getName() != null && !user.getName().isEmpty()) {
            identity.setName(user.getName());
        } else {
            identity.setName(user.getEmailAddress().split("@")[0]);
        }
        identity.setEmail(user.getEmailAddress());

        if (identity.getName().equals("unknown")) {
            identity.setName(user.getEmailAddress().split("@")[0]);
        }

        return identity;
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

        cloneCommand.setTransportConfigCallback(transport -> {
            SshTransport sshTransport = (SshTransport) transport;
            sshTransport.setSshSessionFactory(sshSessionFactory);
        });
        return cloneCommand;
    }

    /**
     * gets project name
     *
     * @return project name
     */
    public String getProjectName() {
        return projectHandle.substring(projectHandle.lastIndexOf("/") + 1, projectHandle.lastIndexOf(App.GIT_SUFFIX));
    }
}
