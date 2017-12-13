package cz.zcu.kiv.spade.pumps.vcs.svn;

import cz.zcu.kiv.spade.domain.Commit;
import cz.zcu.kiv.spade.pumps.RelationMiner;

class SvnRelationMiner extends RelationMiner {

    SvnRelationMiner(SvnPump pump) {
        super(pump);
    }

    @Override
    protected void mineMentions() {
        // from commit messages
        for (Commit commit : pump.getPi().getProject().getCommits()) {
            mineMentionedSvnCommits(commit, commit.getDescription());
        }
    }
}
