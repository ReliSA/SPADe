package cz.zcu.kiv.spade.pumps.vcs;

import cz.zcu.kiv.spade.domain.*;
import cz.zcu.kiv.spade.pumps.ItemMiner;

import java.util.Date;
import java.util.List;
import java.util.Map;

public abstract class CommitMiner<CommitObject> extends ItemMiner<CommitObject> {

    protected static final String URL_FIELD_NAME = "url";

    protected String defaultBranchName;
    protected Map<String, Artifact> artifactMap;

    protected CommitMiner(VcsPump pump) {
        super(pump);
    }

    /**
     * mines data from all commits associated with a particular branch
     *
     * @param branch branch to mine commits from
     */
    protected abstract void mineCommits(Branch branch);

    /**
     * get SPADe branch object from git branch reference
     *
     * @param name branch name
     * @param id branch id from source tool
     * @return branch object from SPADe
     */
    protected Branch generateBranch(String name, String id) {
        Branch branch = new Branch();
        branch.setExternalId(id);
        branch.setName(name);
        if (name.equals(defaultBranchName)) {
            branch.setIsMain(true);
        }
        return branch;
    }

    /**
     * mines all individual file changes in a given commit
     *
     * @param commit commit to be mined
     * @param created date of commit
     * @param author commit author
     * @return changes associated with the commit
     */
    protected abstract List<WorkItemChange> mineChanges(CommitObject commit, Date created, Person author);
}
