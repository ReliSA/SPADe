package cz.zcu.kiv.spade.pumps.vcs.svn;

import cz.zcu.kiv.spade.App;
import cz.zcu.kiv.spade.domain.Commit;
import cz.zcu.kiv.spade.domain.VCSTag;
import cz.zcu.kiv.spade.pumps.ReleaseMiner;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.internal.wc.admin.SVNEntry;
import org.tmatesoft.svn.core.io.SVNRepository;

import java.util.Collection;

class SvnReleaseMiner extends ReleaseMiner {

    private static final String SVN_DEFAULT_TAG_DIR = "/tags";
    private static final String NO_TAGS_ERR_MSG = "Tag directory not found";

    SvnReleaseMiner(SvnPump pump) {
        super(pump);
    }

    @Override
    public void mineTags() {
        try {
            SVNNodeKind nodeKind = ((SVNRepository) pump.getRootObject()).checkPath(SVN_DEFAULT_TAG_DIR ,  -1);
            if (nodeKind.equals(SVNNodeKind.DIR)) {
                Collection tagEntries = ((SVNRepository) pump.getRootObject()).getDir(SVN_DEFAULT_TAG_DIR, -1 , null , (Collection) null);
                for (Object tagObject : tagEntries) {
                    SVNEntry tagEntry = (SVNEntry) tagObject;
                    if (tagEntry.getKind() == SVNNodeKind.DIR) {
                        VCSTag tag = new VCSTag();
                        tag.setName(tagEntry.getName());
                        tag.setExternalId(tagEntry.getURL());
                        if (!pump.getPi().getProject().containsCommit(Long.toString(tagEntry.getRevision()))){
                            ((SvnCommitMiner) ((SvnPump) pump).getCommitMiner()).mineCommits(tagEntry.getName(), tagEntry.getRevision());
                        }
                        Commit commit = pump.getPi().getProject().getCommit(Long.toString(tagEntry.getRevision()));
                        commit.getTags().add(tag);
                    }
                }
            }
        } catch (SVNException e) {
            App.printLogMsg(NO_TAGS_ERR_MSG, false);
        }
    }
}
