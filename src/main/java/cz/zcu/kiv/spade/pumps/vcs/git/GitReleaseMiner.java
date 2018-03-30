package cz.zcu.kiv.spade.pumps.vcs.git;

import cz.zcu.kiv.spade.App;
import cz.zcu.kiv.spade.domain.Commit;
import cz.zcu.kiv.spade.domain.VCSTag;
import cz.zcu.kiv.spade.pumps.ReleaseMiner;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevObject;
import org.eclipse.jgit.revwalk.RevTag;
import org.eclipse.jgit.revwalk.RevWalk;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

class GitReleaseMiner extends ReleaseMiner {

    private static final String TAGS_MINED_FORMAT = "%d/%d tags mined";
    private static final int TAG_BATCH_SIZE = 200;

    GitReleaseMiner(GitPump pump) {
        super(pump);
    }

    @Override
    public void mineTags() {
        RevWalk walk = new RevWalk((Repository) pump.getRootObject());

        Set<Map.Entry<String, Ref>> refs = ((Repository) pump.getRootObject()).getTags().entrySet();
        int count = 0;
        for (Map.Entry<String, Ref> entry : refs) {

            RevObject any = null;
            try {
                Ref tagRef = ((Repository) pump.getRootObject()).findRef(entry.getKey());
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
            commitSHA = commitSHA.substring(0, GitPump.SHORT_COMMIT_HASH_LENGTH);

            if (!pump.getPi().getProject().containsCommit(commitSHA)) {
                if (any instanceof RevCommit) {
                    ((GitCommitMiner) ((GitPump) pump).getCommitMiner()).mineItem((RevCommit) any);
                } else {
                    try {
                        commitSHA = any.getId().getName().substring(0, GitPump.SHORT_COMMIT_HASH_LENGTH);
                        if (!pump.getPi().getProject().containsCommit(commitSHA)) {
                            RevCommit commit = ((Repository) pump.getRootObject()).parseCommit(any.getId());
                            ((GitCommitMiner) ((GitPump) pump).getCommitMiner()).mineItem(commit);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            Commit commit = pump.getPi().getProject().getCommit(commitSHA);
            if (commit != null) {
                commit.getTags().add(tag);
            }
            count++;
            if (count % TAG_BATCH_SIZE == 0) {
                App.printLogMsg(this, String.format(TAGS_MINED_FORMAT, count, refs.size()));
            }
        }
        walk.dispose();
    }
}
