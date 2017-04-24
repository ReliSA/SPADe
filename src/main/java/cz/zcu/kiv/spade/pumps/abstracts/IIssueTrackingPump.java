package cz.zcu.kiv.spade.pumps.abstracts;

import cz.zcu.kiv.spade.domain.abstracts.ProjectSegment;

import java.util.Collection;

public interface IIssueTrackingPump {

    void mineTickets();

    Collection<ProjectSegment> mineIterations();

    void mineEnums();
}
