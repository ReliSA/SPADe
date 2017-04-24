package cz.zcu.kiv.spade.pumps.abstracts;

public interface IVCSPump {
    /**
     * loads a map using commit's external ID as a key and a set of associated tags as a value
     */
    void addTags();

    /**
     * mines data one branch after another while storing date in private fields
     */
    void mineBranches();
}
