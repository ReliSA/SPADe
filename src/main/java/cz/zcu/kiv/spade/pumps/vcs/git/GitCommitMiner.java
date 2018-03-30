package cz.zcu.kiv.spade.pumps.vcs.git;

import cz.zcu.kiv.spade.App;
import cz.zcu.kiv.spade.domain.*;
import cz.zcu.kiv.spade.domain.enums.ArtifactClass;
import cz.zcu.kiv.spade.pumps.DataPump;
import cz.zcu.kiv.spade.pumps.vcs.CommitMiner;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.*;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.patch.FileHeader;
import org.eclipse.jgit.revwalk.FooterLine;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.util.io.DisabledOutputStream;

import java.io.IOException;
import java.net.URLConnection;
import java.util.*;

class GitCommitMiner extends CommitMiner<RevCommit> {

    private static final String MINING_BRANCH_START_FORMAT = "mining branch \"%s\" (%d/%d)";
    private static final String COMMITS_MINED_FORMAT = "%d commits mined";
    private static final String SCORE_FORMAT = "score: %d\n";
    private static final String LINES_CHANGED_FORMAT = "%d lines added, %d lines deleted";
    private static final String MISSING_OBJECT_ERR_MSG = "Missing object";
    private static final String REF_HEADS_PREFIX = "refs/heads/";
    private static final String COMMITTER_FIELD_NAME = "Committed-by";
    private static final String CC_FIELD_NAME = "CC";
    private static final String FOOTLINE_FIELD_NAME_SUFFIX = "-by";
    private static final String EMAIL_START_CHAR = "<";
    private static final String ID_FIELD_NAME = "id";
    private static final String NAME_FIELD_NAME = "name";
    private static final int COMMIT_BATCH_SIZE = 5000;

    GitCommitMiner(GitPump pump) {
        super(pump);
        this.defaultBranchName = GitPump.GIT_DEFAULT_BRANCH_NAME;
        this.artifactMap = new HashMap<>();
    }

    @Override
    public void mineItems() {
        List<Ref> branchRefs = new ArrayList<>();
        List<Branch> branches = new ArrayList<>();
        try {
            Git git = new Git((Repository) pump.getRootObject());
            branchRefs = git.branchList().call();
        } catch (GitAPIException e) {
            e.printStackTrace();
        }

        int i = 1;
        for (Ref branchRef : branchRefs) {
            Branch branch = generateBranch(stripBranchName(branchRef.getName()), branchRef.getName());
            App.printLogMsg(this, String.format(MINING_BRANCH_START_FORMAT, branch.getName(), i, branchRefs.size()));
            mineCommits(branch);
            branches.add(branch);
            i++;
        }
        if (branches.size() == 1) {
            branches.get(0).setIsMain(true);
        }
    }

    @Override
    protected void mineCommits(Branch branch) {
        // TODO branch determination
        RevWalk revWalk = new RevWalk((Repository) pump.getRootObject());

        try {
            ObjectId commitId = ((Repository) pump.getRootObject()).resolve(branch.getName());
            revWalk.markStart(revWalk.parseCommit(commitId));
        } catch (IOException e) {
            e.printStackTrace();
        }

        boolean newCommit;
        int commitCount = pump.getPi().getProject().getCommits().size();
        for (RevCommit gitCommit : revWalk) {
            newCommit = false;
            String shortSHA = gitCommit.getId().getName().substring(0, GitPump.SHORT_COMMIT_HASH_LENGTH);
            if (!pump.getPi().getProject().containsCommit(shortSHA)) {
                mineItem(gitCommit);
                newCommit = true;
            }
            Commit commit = pump.getPi().getProject().getCommit(shortSHA);
            commit.getBranches().add(branch);
            if (newCommit) {
                commitCount = pump.getPi().getProject().getCommits().size();
                if ((commitCount % COMMIT_BATCH_SIZE) == 0) {
                    App.printLogMsg(this, String.format(COMMITS_MINED_FORMAT, commitCount));
                }
            }
        }
        App.printLogMsg(this, String.format(COMMITS_MINED_FORMAT, commitCount));
        revWalk.dispose();
    }

    @Override
    protected void mineItem(RevCommit gitCommit) {
        Commit commit = new Commit();
        commit.setIdentifier(gitCommit.getId().getName().substring(0, GitPump.SHORT_COMMIT_HASH_LENGTH));
        commit.setExternalId(gitCommit.getId().toString());
        commit.setName(gitCommit.getId().getName());
        commit.setDescription(gitCommit.getFullMessage().trim());
        commit.setCommitted(gitCommit.getCommitterIdent().getWhen());
        commit.setCreated(gitCommit.getAuthorIdent().getWhen());
        commit.setAuthor(addPerson(((GitPeopleMiner) pump.getPeopleMiner()).generateIdentity(gitCommit.getAuthorIdent())));
        commit.setChanges(mineChanges(gitCommit, commit.getCreated(), commit.getAuthor()));
        commit.setRelations(collectRelatedPeople(gitCommit));

        /*for (RevCommit parentCommit : commit.getParents()) {
            Configuration parent = new Configuration();
            parent.setExternalId(parentCommit.getId().toString());
            configuration.getParents().add(parent);
        }*/

        pump.getPi().getProject().addCommit(commit);
    }

    @Override
    protected List<WorkItemChange> mineChanges(RevCommit commit, Date created, Person author) {
        List<WorkItemChange> changes = new ArrayList<>();
        RevTree parentTree = null;
        if (commit.getParentCount() != 0) {
            parentTree = commit.getParent(0).getTree();
        }

        DiffFormatter df = new DiffFormatter(DisabledOutputStream.INSTANCE);
        df.setRepository((Repository) pump.getRootObject());
        df.setDiffComparator(RawTextComparator.DEFAULT);
        df.setDetectRenames(true);

        List<DiffEntry> diffs = new ArrayList<>();

        try {
            diffs = df.scan(parentTree, commit.getTree());
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (DiffEntry diff : diffs) {
            FileHeader header = null;
            try {
                header = df.toFileHeader(diff);
            } catch (IOException e) {
                if (e instanceof MissingObjectException) {
                    App.printLogMsg(this, MISSING_OBJECT_ERR_MSG);
                    continue;
                }
                e.printStackTrace();
            }

            if (header != null) {
                for (WorkItemChange change : mineFileChanges(diff, header.toEditList())) {
                    if (change.getType().equals(WorkItemChange.Type.ADD) || change.getType().equals(WorkItemChange.Type.COPY)){
                        change.getChangedItem().setCreated(created);
                        change.getChangedItem().setAuthor(author);
                    }
                    changes.add(change);
                }
            }
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
    private Collection<WorkItemChange> mineFileChanges(DiffEntry diff, EditList edits) {
        List<WorkItemChange> changes = new ArrayList<>();

        Artifact artifact = getArtifact(diff);

        for (WorkItemChange.Type type : collectTypes(diff)) {
            WorkItemChange change = new WorkItemChange();
            String desc = "";

            if (type.equals(WorkItemChange.Type.MOVE) || type.equals(WorkItemChange.Type.COPY) || type.equals(WorkItemChange.Type.RENAME)) {
                change.getFieldChanges().add(new FieldChange(URL_FIELD_NAME, diff.getOldPath(), diff.getNewPath()));
            }
            if (type.equals(WorkItemChange.Type.RENAME)) {
                change.getFieldChanges().add( new FieldChange(NAME_FIELD_NAME, stripFileName(diff.getOldPath()), stripFileName(diff.getNewPath())));
            }

            if (type.equals(WorkItemChange.Type.MODIFY) || type.equals(WorkItemChange.Type.ADD)) {
                int linesAdded = 0, linesDeleted = 0;
                for (Edit edit : edits) {
                    linesDeleted += edit.getLengthA();
                    linesAdded += edit.getLengthB();
                }
                desc += String.format(LINES_CHANGED_FORMAT, linesAdded, linesDeleted);
            }

            if (!type.equals(WorkItemChange.Type.ADD) && !type.equals(WorkItemChange.Type.DELETE) && !diff.getNewId().toString().equals(diff.getOldId().toString())) {
                change.getFieldChanges().add(new FieldChange(ID_FIELD_NAME, diff.getOldId().name(), diff.getNewId().name()));
            }
            if (type.equals(WorkItemChange.Type.COPY) || type.equals(WorkItemChange.Type.MOVE)) {
                if (diff.getScore() < PERCENTAGE_MAX) {
                    desc += String.format(SCORE_FORMAT, diff.getScore());
                }
            }

            change.setType(type);
            change.setDescription(desc);
            change.setChangedItem(artifact);
            changes.add(change);
        }

        return changes;
    }

    private Artifact getArtifact(DiffEntry diff) {
        String newId = diff.getNewId().name();
        String oldId = diff.getOldId().name();

        Artifact artifact;
        if (!artifactMap.containsKey(newId)) {
            if (!artifactMap.containsKey(oldId)) {
                artifact = new Artifact();
            } else {
                artifact = artifactMap.get(oldId);
            }
        } else {
            artifact = artifactMap.get(newId);
        }
        artifact.setName(stripFileName(diff.getNewPath()));
        artifact.setExternalId(newId);
        artifact.setUrl(diff.getNewPath());
        artifact.setArtifactClass(ArtifactClass.FILE);
        if (artifact.getName().contains(DataPump.DOT)) {
            artifact.setMimeType(URLConnection.guessContentTypeFromName(artifact.getName()));
            if (artifact.getMimeType() == null) {
                artifact.setMimeType(artifact.getName().substring(artifact.getName().lastIndexOf(DataPump.DOT) + 1));
            }
        }
        artifactMap.put(oldId, artifact);
        artifactMap.put(newId, artifact);
        return artifact;
    }

    private Collection<WorkItemChange.Type> collectTypes(DiffEntry diff) {
        String newFileName = stripFileName(diff.getNewPath());
        String newFileDir = diff.getNewPath().replace(newFileName, "");

        String oldFileName = stripFileName(diff.getOldPath());
        String oldFileDir = diff.getOldPath().replace(oldFileName, "");

        List<WorkItemChange.Type> types = new ArrayList<>();
        WorkItemChange.Type mainType = WorkItemChange.Type.valueOf(diff.getChangeType().name());

        if (mainType.equals(WorkItemChange.Type.RENAME) || mainType.equals(WorkItemChange.Type.COPY)) {
            if (mainType.equals(WorkItemChange.Type.RENAME)) {
                if (newFileName.equals(oldFileName)) {
                    mainType = WorkItemChange.Type.MOVE;
                } else if (!newFileDir.equals(oldFileDir)) {
                    types.add(WorkItemChange.Type.MOVE);
                }
            }
            if (diff.getScore() < PERCENTAGE_MAX) {
                types.add(WorkItemChange.Type.MODIFY);
            }
        }
        types.add(mainType);
        return types;
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
        relation.setPerson(addPerson(((GitPeopleMiner) pump.getPeopleMiner()).generateIdentity(commit.getCommitterIdent())));
        relation.setName(COMMITTER_FIELD_NAME);
        relations.add(relation);

        for (FooterLine line : commit.getFooterLines()) {
            if (line.getKey().endsWith(FOOTLINE_FIELD_NAME_SUFFIX) || line.getKey().equals(CC_FIELD_NAME)) {
                String[] parts = line.getValue().split(EMAIL_START_CHAR);
                String name = parts[0].trim();
                String email = "";
                if (parts.length > 1) {
                    email = parts[1].substring(0, parts[1].length() - 1).trim();
                }

                ConfigPersonRelation footlineRelation = new ConfigPersonRelation();
                footlineRelation.setPerson(addPerson(((GitPeopleMiner) pump.getPeopleMiner()).generateIdentity(new PersonIdent(name, email))));
                footlineRelation.setName(line.getKey());

                relations.add(footlineRelation);
            }
        }
        return relations;
    }

    /**
     * gets branch proper name from its reference name
     *
     * @param name reference name
     * @return branch name
     */
    private String stripBranchName(String name) {
        return name.replace(REF_HEADS_PREFIX, "");
    }

    /**
     * cuts off the path part of the file path
     *
     * @param path path of the file
     * @return simple name of the file
     */
    private String stripFileName(String path) {
        if (path.contains(DataPump.SLASH)) {
            return path.substring(path.lastIndexOf(DataPump.SLASH) + 1);
        } else return path;
    }
}
