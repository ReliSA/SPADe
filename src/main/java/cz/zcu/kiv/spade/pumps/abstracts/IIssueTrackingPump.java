package cz.zcu.kiv.spade.pumps.abstracts;

/**
 * interface for pumps mining issuetracking tools
 *
 * @author Petr Pícha
 */
public interface IIssueTrackingPump {

    void mineAllRelations();

    /**
     * mines all the priority values used in the project
     */
    void minePriorities();

    /**
     * mines all the issue type values used in the project
     */
    void mineWUTypes();
}
