package cz.zcu.kiv.spade.domain;

import cz.zcu.kiv.spade.domain.enums.*;

import java.util.ArrayList;
import java.util.List;

public class Defaults {

    public static List<Role> getDefaultRoles() {
        List<Role> roles = new ArrayList<>();

        roles.add(new Role("analyst", RoleClass.ANALYST));
        roles.add(new Role("bussinessanalyst", RoleClass.ANALYST));

        roles.add(new Role("designer", RoleClass.DESIGNER));
        roles.add(new Role("architect", RoleClass.DESIGNER));
        roles.add(new Role("systemarchitect", RoleClass.DESIGNER));
        roles.add(new Role("uxdesigner", RoleClass.DESIGNER));

        roles.add(new Role("developer", RoleClass.DEVELOPER));

        roles.add(new Role("documenter", RoleClass.DOCUMENTER));

        roles.add(new Role("mentor", RoleClass.MENTOR));
        roles.add(new Role("scrummaster", RoleClass.MENTOR));

        roles.add(new Role("nonmember", RoleClass.NON_MEMBER));
        roles.add(new Role("anonymous", RoleClass.NON_MEMBER));

        roles.add(new Role("projectmanager", RoleClass.PROJECT_MANAGER));
        roles.add(new Role("manager", RoleClass.PROJECT_MANAGER));
        roles.add(new Role("owner", RoleClass.PROJECT_MANAGER));
        roles.add(new Role("productmanager", RoleClass.PROJECT_MANAGER));
        roles.add(new Role("projectlead", RoleClass.PROJECT_MANAGER));
        roles.add(new Role("projectleader", RoleClass.PROJECT_MANAGER));
        roles.add(new Role("administrator", RoleClass.PROJECT_MANAGER));
        roles.add(new Role("tempoprojectmanager", RoleClass.PROJECT_MANAGER));

        return roles;
    }

    public static List<Status> getDeafultStatuses() {
        List<Status> statuses = new ArrayList<>();

        statuses.add(new Status("new", StatusClass.NEW));
        statuses.add(new Status("todo", StatusClass.NEW));
        statuses.add(new Status("unconfirmed", StatusClass.NEW));
        statuses.add(new Status("funnel", StatusClass.NEW));
        statuses.add(new Status("analysis", StatusClass.NEW));
        statuses.add(new Status("open", StatusClass.NEW));

        statuses.add(new Status("accepted", StatusClass.ACCEPTED));
        statuses.add(new Status("assigned", StatusClass.ACCEPTED));
        statuses.add(new Status("backlog", StatusClass.ACCEPTED));

        statuses.add(new Status("inprogress", StatusClass.IN_PROGRESS));

        statuses.add(new Status("resolved", StatusClass.RESOLVED));
        statuses.add(new Status("test", StatusClass.RESOLVED));

        statuses.add(new Status("verified", StatusClass.VERIFIED));
        statuses.add(new Status("feedback", StatusClass.VERIFIED));

        statuses.add(new Status("done", StatusClass.DONE));
        statuses.add(new Status("closed", StatusClass.DONE));
        statuses.add(new Status("fixed", StatusClass.DONE));
        statuses.add(new Status("approved", StatusClass.DONE));

        statuses.add(new Status("invalid", StatusClass.INVALID));
        statuses.add(new Status("cancelled", StatusClass.INVALID));
        statuses.add(new Status("rejected", StatusClass.INVALID));

        return statuses;
    }

    public static List<Priority> getDefaultPriorities() {
        List<Priority> priorities = new ArrayList<>();

        priorities.add(new Priority("lowest", PriorityClass.LOWEST));

        priorities.add(new Priority("low", PriorityClass.LOW));

        priorities.add(new Priority("normal", PriorityClass.NORMAL));
        priorities.add(new Priority("medium", PriorityClass.NORMAL));
        priorities.add(new Priority("standard", PriorityClass.NORMAL));
        priorities.add(new Priority("moderate", PriorityClass.NORMAL));
        priorities.add(new Priority("common", PriorityClass.NORMAL));

        priorities.add(new Priority("high", PriorityClass.HIGH));

        priorities.add(new Priority("highest", PriorityClass.HIGHEST));
        priorities.add(new Priority("immediate", PriorityClass.HIGHEST));
        priorities.add(new Priority("urgent", PriorityClass.HIGHEST));

        return priorities;
    }

    public static List<Severity> getDefaultSeverities() {
        List<Severity> severities = new ArrayList<>();

        severities.add(new Severity("trivial", SeverityClass.TRIVIAL));

        severities.add(new Severity("minor", SeverityClass.MINOR));
        severities.add(new Severity("small", SeverityClass.MINOR));

        severities.add(new Severity("normal", SeverityClass.NORMAL));
        severities.add(new Severity("moderate", SeverityClass.NORMAL));
        severities.add(new Severity("common", SeverityClass.NORMAL));
        severities.add(new Severity("standard", SeverityClass.NORMAL));
        severities.add(new Severity("medium", SeverityClass.NORMAL));

        severities.add(new Severity("major", SeverityClass.MAJOR));
        severities.add(new Severity("big", SeverityClass.MAJOR));

        severities.add(new Severity("critical", SeverityClass.CRITICAL));
        severities.add(new Severity("blocker", SeverityClass.CRITICAL));

        return severities;
    }

    public static List<Resolution> getDefaultResolutions() {
        List<Resolution> resolutions = new ArrayList<>();

        resolutions.add(new Resolution("invalid", ResolutionClass.INVALID));

        resolutions.add(new Resolution("duplicate", ResolutionClass.DUPLICATE));

        resolutions.add(new Resolution("wontfix", ResolutionClass.WONT_FIX));
        resolutions.add(new Resolution("wontdo", ResolutionClass.WONT_FIX));

        resolutions.add(new Resolution("fixed", ResolutionClass.FIXED));
        resolutions.add(new Resolution("done", ResolutionClass.FIXED));
        resolutions.add(new Resolution("fixedupstream", ResolutionClass.FIXED));

        resolutions.add(new Resolution("worksasdesigned", ResolutionClass.WORKS_AS_DESIGNED));

        resolutions.add(new Resolution("finished", ResolutionClass.FINISHED));

        resolutions.add(new Resolution("worksforme", ResolutionClass.WORKS_FOR_ME));

        resolutions.add(new Resolution("incomplete", ResolutionClass.INCOMPLETE));
        resolutions.add(new Resolution("cannotreproduce", ResolutionClass.INCOMPLETE));

        resolutions.add(new Resolution("unfinished", ResolutionClass.UNFINISHED));

        return resolutions;
    }

    public static List<WorkUnitType> getDefaultWUTypes() {
        List<WorkUnitType> types = new ArrayList<>();

        types.add(new WorkUnitType("bug", WorkUnitTypeClass.BUG));
        types.add(new WorkUnitType("defect", WorkUnitTypeClass.BUG));

        types.add(new WorkUnitType("task", WorkUnitTypeClass.TASK));

        types.add(new WorkUnitType("enhancement", WorkUnitTypeClass.ENHANCEMENT));
        types.add(new WorkUnitType("improvement", WorkUnitTypeClass.ENHANCEMENT));

        types.add(new WorkUnitType("feature", WorkUnitTypeClass.FEATURE));
        types.add(new WorkUnitType("newfeature", WorkUnitTypeClass.FEATURE));

        return types;
    }
}
