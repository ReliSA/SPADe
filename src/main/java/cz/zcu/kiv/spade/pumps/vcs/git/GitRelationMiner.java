package cz.zcu.kiv.spade.pumps.vcs.git;

import cz.zcu.kiv.spade.domain.Commit;
import cz.zcu.kiv.spade.pumps.RelationMiner;

class GitRelationMiner extends RelationMiner {

    GitRelationMiner(GitPump pump) {
        super(pump);
    }

    @Override
    protected void mineMentions() {
        // from commit messages
        for (Commit commit : pump.getPi().getProject().getCommits()) {
            mineMentionedGitCommits(commit, commit.getDescription());
        }
    }
}
