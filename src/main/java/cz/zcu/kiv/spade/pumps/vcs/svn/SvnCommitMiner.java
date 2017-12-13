package cz.zcu.kiv.spade.pumps.vcs.svn;

import cz.zcu.kiv.spade.domain.*;
import cz.zcu.kiv.spade.domain.enums.ArtifactClass;
import cz.zcu.kiv.spade.pumps.DataPump;
import cz.zcu.kiv.spade.pumps.vcs.CommitMiner;
import org.tmatesoft.svn.core.*;
import org.tmatesoft.svn.core.internal.wc.admin.SVNEntry;
import org.tmatesoft.svn.core.internal.wc2.ng.SvnDiffGenerator;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc2.SvnDiff;
import org.tmatesoft.svn.core.wc2.SvnOperationFactory;
import org.tmatesoft.svn.core.wc2.SvnTarget;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.*;

class SvnCommitMiner extends CommitMiner<SVNLogEntry> {

    private static final String SVN_DEFAULT_BRANCH_NAME = "trunk";
    private static final String SVN_DEFAULT_BRANCHES_DIR = "branches";

    SvnCommitMiner(SvnPump pump) {
        super(pump);
        this.defaultBranchName = SVN_DEFAULT_BRANCH_NAME;
        this.artifactMap = new HashMap<>();
    }

    @Override
    protected void mineCommits(Branch branch) {
        Collection logEntries = null;
        try {
            logEntries = ((SVNRepository) pump.getRootObject()).log(new String[] {branch.getName()} , null , -1 , 1 , true , true);
        } catch (SVNException e) {
            e.printStackTrace();
        }
        if (logEntries == null) return;

        for (Object logEntry : logEntries) {
            mineItem((SVNLogEntry) logEntry);
            Commit commit = pump.getPi().getProject().getCommit(Long.toString(((SVNLogEntry) logEntry).getRevision()));
            commit.getBranches().add(branch);
        }
    }

    void mineCommits(String tagName, long revision) {
        Collection logEntries = null;
        try {
            logEntries = ((SVNRepository) pump.getRootObject()).log(new String[] {tagName} , null , revision , 1 , true , true);
        } catch (SVNException e) {
            e.printStackTrace();
        }
        if (logEntries == null) return;

        for (Object logEntry : logEntries) {
            mineItem((SVNLogEntry) logEntry);
        }
    }

    @Override
    public void mineItems() {
        List<Branch> branches = new ArrayList<>();
        Collection entries = new ArrayList();
        try {
            entries = ((SVNRepository) pump.getRootObject()).getDir("", -1 , null , (Collection) null);
        } catch (SVNException e) {
            e.printStackTrace();
        }
        for (Object object : entries) {
            SVNEntry entry = (SVNEntry) object;
            if (entry.getKind() == SVNNodeKind.DIR) {

                if (entry.getName().equals(SVN_DEFAULT_BRANCH_NAME)) {
                    Branch trunk = generateBranch(entry.getName(), entry.getURL());
                    mineCommits(trunk);
                    branches.add(trunk);

                }
                if (entry.getName().equals(SVN_DEFAULT_BRANCHES_DIR)) {
                    Collection branchEntries = new ArrayList();
                    try {
                        branchEntries = ((SVNRepository) pump.getRootObject()).getDir(DataPump.SLASH + entry.getName(), -1 , null , (Collection) null);
                    } catch (SVNException e) {
                        e.printStackTrace();
                    }
                    for (Object branchObject : branchEntries) {
                        SVNEntry branchEntry = (SVNEntry) branchObject;
                        if (branchEntry.getKind() == SVNNodeKind.DIR) {
                            Branch branch = generateBranch(branchEntry.getName(), branchEntry.getURL());
                            mineCommits(branch);
                            branches.add(branch);
                        }
                    }
                }
            }
        }
        if (branches.size() == 0) {
            Branch defaultBranch = generateBranch("", pump.getPi().getUrl());
            mineCommits(defaultBranch);
            branches.add(defaultBranch);
        }
        if (branches.size() == 1) {
            branches.get(0).setIsMain(true);
        }
    }

    @Override
    protected void mineItem(SVNLogEntry revision) {
        Commit commit = new Commit();
        commit.setIdentifier(Long.toString(revision.getRevision()));
        commit.setExternalId(Long.toString(revision.getRevision()));
        commit.setName(Long.toString(revision.getRevision()));
        commit.setDescription(revision.getMessage().trim());
        commit.setCommitted(revision.getDate());
        commit.setCreated(revision.getDate());
        commit.setAuthor(addPerson(((SvnPeopleMiner) pump.getPeopleMiner()).generateIdentity(revision.getAuthor())));
        commit.setChanges(mineChanges(revision, commit.getCreated(), commit.getAuthor()));

        pump.getPi().getProject().addCommit(commit);
    }

    @Override
    protected  List<WorkItemChange> mineChanges(SVNLogEntry revision, Date created, Person author) {

        List<WorkItemChange> changes = new ArrayList<>();

        Set<String> deleted = new LinkedHashSet<>();
        for (SVNLogEntryPath diff : revision.getChangedPaths().values()) {
            WorkItemChange change = mineChange(diff, revision.getRevision());
            if (change == null){
                continue;
            }

            if (change.getType().equals(WorkItemChange.Type.ADD) || change.getType().equals(WorkItemChange.Type.COPY)){
                change.getChangedItem().setCreated(created);
                change.getChangedItem().setAuthor(author);
            }
            if (change.getType().equals(WorkItemChange.Type.DELETE)) {
                for (FieldChange fChange : change.getFieldChanges()) {
                    if (fChange.getName().equals(URL_FIELD_NAME)) {
                        deleted.add(fChange.getOldValue());
                    }
                }
            }
            changes.add(change);
        }
        for (WorkItemChange change : changes) {
            if (change.getType().equals(WorkItemChange.Type.COPY) && deleted.contains(change.getChangedItem().getUrl())) {
                change.setType(WorkItemChange.Type.MOVE);
            }
        }

        return changes;
    }

    private WorkItemChange mineChange(SVNLogEntryPath diff, long revision) {
        WorkItemChange change = new WorkItemChange();

        SVNNodeKind nodeKind = diff.getKind();

        if (nodeKind.equals(SVNNodeKind.NONE) || nodeKind.equals(SVNNodeKind.UNKNOWN)){
            return null;
        }

        switch (diff.getType()) {
            case SVNLogEntryPath.TYPE_REPLACED:
            case SVNLogEntryPath.TYPE_MODIFIED:
                change.setType(WorkItemChange.Type.MODIFY);
                break;
            case SVNLogEntryPath.TYPE_DELETED:
                change.setType(WorkItemChange.Type.DELETE);
                break;
            case SVNLogEntryPath.TYPE_ADDED:
                change.setType(WorkItemChange.Type.ADD);
                if (diff.getCopyPath() != null) {
                    change.setType(WorkItemChange.Type.COPY);
                }
                break;
            default:
                break;
        }

        Artifact artifact;
        if ((artifact = artifactMap.get(diff.getPath())) == null) {
            artifact = new Artifact();

            artifact.setName(diff.getPath().substring(diff.getPath().lastIndexOf(DataPump.SLASH)));
            artifact.setUrl(diff.getPath());
            artifact.setExternalId(diff.getPath());

            if (diff.getKind().equals(SVNNodeKind.FILE)) {
                artifact.setArtifactClass(ArtifactClass.FILE);
                SVNProperties fileProperties = new SVNProperties();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                try {
                    ((SVNRepository) pump.getRootObject()).getFile(diff.getPath(), revision, fileProperties, stream);
                } catch (SVNException e) {
                    e.printStackTrace();
                }
                artifact.setMimeType(fileProperties.getStringValue(SVNProperty.MIME_TYPE));
                artifact.setDescription(stream.toString());
                artifact.setSize(Long.parseLong(fileProperties.getStringValue(SVNProperty.WORKING_SIZE)));
                artifact.setExternalId(fileProperties.getStringValue(SVNProperty.CHECKSUM));
            }

            if (diff.getKind().equals(SVNNodeKind.DIR)) {
                artifact.setArtifactClass(ArtifactClass.FOLDER);
            }

            artifactMap.put(diff.getPath(), artifact);
        }

        if (diff.getKind().equals(SVNNodeKind.FILE)) {

            final SvnOperationFactory svnOperationFactory = new SvnOperationFactory();
            try {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                final SvnDiffGenerator diffGenerator = new SvnDiffGenerator();
                diffGenerator.setBasePath(new File(diff.getPath()));
                final SvnDiff edit = svnOperationFactory.createDiff();
                SVNURL file = SVNURL.fromFile(new File(diff.getPath()));
                edit.setSources(SvnTarget.fromURL(file, SVNRevision.create(revision)), SvnTarget.fromURL(file, SVNRevision.create(revision - 1)));
                edit.setDiffGenerator(diffGenerator);
                edit.setOutput(stream);
                edit.run();
                change.setDescription(stream.toString());
            } catch (SVNException e) {
                e.printStackTrace();
            } finally {
                svnOperationFactory.dispose();
            }
        }

        if (!diff.getPath().equals(diff.getCopyPath())) {
            change.getFieldChanges().add(new FieldChange(URL_FIELD_NAME, diff.getPath(), diff.getCopyPath()));
        }

        change.setChangedItem(artifact);

        return change;
    }
}
