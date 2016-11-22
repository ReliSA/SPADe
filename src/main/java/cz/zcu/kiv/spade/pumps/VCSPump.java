package cz.zcu.kiv.spade.pumps;

import cz.zcu.kiv.spade.domain.Branch;
import cz.zcu.kiv.spade.domain.VCSTag;

import java.util.Map;
import java.util.Set;

/**
 * interface for data pumps mining VCS tools (or VCS parts of tools)
 */
public interface VCSPump {

    /**
     * loads a map using commit's external ID as a key and a set of associated tags as a value
     *
     * @return map of tags per commit ID
     */
    Map<String, Set<VCSTag>> loadTags();

    /**
     * mines data from all commits associated with a particular branch
     *
     * @param branch branch to mine commits from
     */
    void mineCommits(Branch branch);
}
