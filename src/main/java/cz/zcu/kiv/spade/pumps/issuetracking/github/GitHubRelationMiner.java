package cz.zcu.kiv.spade.pumps.issuetracking.github;

import cz.zcu.kiv.spade.domain.*;
import cz.zcu.kiv.spade.pumps.issuetracking.IssueTrackingRelationMiner;

import java.util.HashSet;
import java.util.Set;

class GitHubRelationMiner extends IssueTrackingRelationMiner {

    GitHubRelationMiner(GitHubPump pump) {
        super(pump);
    }

    @Override
    protected void mineMentions() {
        Set<VCSTag> tags = new HashSet<>();
        for (Commit commit : pump.getPi().getProject().getCommits()) {
            // unit mentions from commit messages
            mineMentionedUnits(commit, commit.getDescription());
            for (VCSTag tag : commit.getTags()) {
                if (!tags.contains(tag)) {
                    // from release descriptions
                    mineAllMentionedItemsGit(commit, tag.getDescription());
                    tags.add(tag);
                }
            }
        }
        // from work unit descriptions
        for (WorkUnit unit : pump.getPi().getProject().getUnits()) {
            mineAllMentionedItemsGit(unit);
        }
        for (Configuration configuration : pump.getPi().getProject().getConfigurations()) {
            if (!(configuration instanceof Commit || configuration instanceof CommittedConfiguration)) {
                for (WorkItemChange change : configuration.getChanges()) {
                    // from issue and commit comments
                    if ((change.getChangedItem() instanceof WorkUnit
                            || change.getChangedItem() instanceof Commit)
                            && change.getType().equals(WorkItemChange.Type.COMMENT)) {
                        mineAllMentionedItemsGit(change.getChangedItem(), configuration.getDescription());
                    }
                }
            }
        }
    }

    @Override
    protected void mineRelations() {
        // GitHub doesn't have the functionality of relating issues to one another other than mentions
    }
}
