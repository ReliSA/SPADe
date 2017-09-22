package cz.zcu.kiv.spade.pumps.abstracts;

import cz.zcu.kiv.spade.domain.abstracts.ProjectSegment;

import java.util.Collection;

/**
 * interface for pumps mining issuetracking tools
 *
 * @author Petr Pícha
 */
public interface IIssueTrackingPump {

    /**
     * mines all the tickets/issues in the project
     */
    void mineTickets();

    /**
     * mines all iterations (milestones, phases, etc.) in the project and saves each one as Iteration, Phase and Activity
     * for future analysis
     * @return all project segments in all 3 forms
     */
    Collection<ProjectSegment> mineIterations();

    /**
     * mines all the enumeration values used in the project
     */
    void mineEnums();
}
