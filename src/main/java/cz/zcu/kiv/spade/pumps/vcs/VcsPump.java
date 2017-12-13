package cz.zcu.kiv.spade.pumps.vcs;

import cz.zcu.kiv.spade.domain.Commit;
import cz.zcu.kiv.spade.domain.Configuration;
import cz.zcu.kiv.spade.pumps.DataPump;
import cz.zcu.kiv.spade.pumps.RelationMiner;
import cz.zcu.kiv.spade.pumps.ReleaseMiner;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public abstract class VcsPump<RootObjectType, SecondaryObjectType> extends DataPump<RootObjectType, SecondaryObjectType> {

    protected CommitMiner commitMiner;
    protected ReleaseMiner releaseMiner;
    protected RelationMiner relationMiner;

    /**
     * @param projectHandle URL of the project instance
     * @param privateKeyLoc private key location for authenticated login
     * @param username      username for authenticated login
     * @param password      password for authenticated login
     */
    protected VcsPump(String projectHandle, String privateKeyLoc, String username, String password) {
        super(projectHandle, privateKeyLoc, username, password);
    }

    @Override
    protected void mineContent() {
        if (peopleMiner != null) {
            peopleMiner.setEntityManager();
            peopleMiner.mineGroups();
            peopleMiner.minePeople();
        }
        if (commitMiner != null) {
            commitMiner.setEntityManager();
            commitMiner.mineItems();
        }
        if (releaseMiner != null) {
            releaseMiner.setEntityManager();
            releaseMiner.mineTags();
        }

        List<Configuration> list = sortConfigsByDate();
        if (!list.isEmpty()) {
            this.pi.getProject().setStartDate(list.get(0).getCreated());
        }

        if (relationMiner != null) {
            relationMiner.setEntityManager();
            relationMiner.mineAllRelations();
        }
    }

    /**
     * returns configurations in a form of a list sorted by date from earliest
     *
     * @return sorted list of configurations
     */
    private List<Configuration> sortConfigsByDate() {
        List<Configuration> list = new ArrayList<>();
        list.addAll(pi.getProject().getConfigurations());
        list.sort(getConfigurationByDateComparator());
        return list;
    }

    /**
     * gets a new comparator instance for comparing Commit instances based on creation and commit dates
     *
     * @return a new Commit comparator
     */
    private static Comparator<Configuration> getConfigurationByDateComparator() {
        return new Comparator<Configuration>() {
            @Override
            public int compare(Configuration o1, Configuration o2) {
                if ((o1 instanceof Commit) && (o2 instanceof Commit)) {
                    Commit c1 = (Commit) o1;
                    Commit c2 = (Commit) o2;
                    return compareCommits(c1, c2);
                } else {
                    return o1.getCreated().compareTo(o2.getCreated());
                }
            }

            /**
             * compares two Commit instances based on date of creation and commit date
             * @param o1 first commit
             * @param o2 second commit
             * @return comparison result
             */
            private int compareCommits(Commit o1, Commit o2) {

                int ret = o1.getCommitted().compareTo(o2.getCommitted());
                if (ret != 0) return ret;
                else {
                    return o1.getCreated().compareTo(o2.getCreated());
                }
            }
        };
    }

    public CommitMiner getCommitMiner() {
        return commitMiner;
    }
}
