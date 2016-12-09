package cz.zcu.kiv.spade.pumps.abstracts;

import cz.zcu.kiv.spade.domain.WorkUnit;

import java.util.Map;

public interface IIssueTrackingPump {

    Map<Integer, WorkUnit> mineTickets();

}
