package cz.zcu.kiv.spade.load;

import cz.zcu.kiv.spade.dao.*;
import cz.zcu.kiv.spade.dao.jpa.*;
import cz.zcu.kiv.spade.domain.*;
import cz.zcu.kiv.spade.domain.enums.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.ArrayList;
import java.util.List;

public class DBInitializer {

    /** a JPA persistence unit for creating a blank SPADe database */
    private static final String PERSISTENCE_UNIT_CREATE = "create";
    /** JPA entity manager for creating a blank SPADe database */
    private EntityManager createManager;

    public void initializeDatabase() {
        if (createManager == null || !createManager.isOpen()) {
            EntityManagerFactory factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_CREATE);
            createManager = factory.createEntityManager();
        }

        loadRoleClassifications();
        loadStatusClassifications();
        loadPriorityClassifications();
        loadSeverityClassifications();
        loadResolutionClassifications();
        loadTypeClassifications();
        loadRelationClassifications();

        if (createManager != null && createManager.isOpen()) {
            createManager.close();
        }
    }

    private void loadRoleClassifications() {
        GenericDAO<RoleClassification> dao = new RoleClassificationDAO_JPA(createManager);

        for (RoleClass aClass : RoleClass.values()) {
            dao.save(new RoleClassification(aClass));
        }
    }

    private void loadStatusClassifications() {
        GenericDAO<StatusClassification> dao = new StatusClassificationDAO_JPA(createManager);

        for (StatusClass aClass : StatusClass.values()) {
            dao.save(new StatusClassification(aClass));
        }
    }

    private void loadPriorityClassifications() {
        GenericDAO<PriorityClassification> dao = new PriorityClassificationDAO_JPA(createManager);

        for (PriorityClass aClass : PriorityClass.values()) {
            dao.save(new PriorityClassification(aClass));
        }
    }

    private void loadSeverityClassifications() {
        GenericDAO<SeverityClassification> dao = new SeverityClassificationDAO_JPA(createManager);

        for (SeverityClass aClass : SeverityClass.values()) {
            dao.save(new SeverityClassification(aClass));
        }
    }

    private void loadResolutionClassifications() {
        GenericDAO<ResolutionClassification> dao = new ResolutionClassificationDAO_JPA(createManager);

        for (ResolutionClass aClass : ResolutionClass.values()) {
            dao.save(new ResolutionClassification(aClass));
        }
    }

    private void loadTypeClassifications() {
        GenericDAO<WorkUnitTypeClassification> dao = new WorkUnitTypeClassificationDAO_JPA(createManager);

        for (WorkUnitTypeClass aClass : WorkUnitTypeClass.values()) {
            dao.save(new WorkUnitTypeClassification(aClass));
        }
    }

    private void loadRelationClassifications() {
        GenericDAO<RelationClassification> dao = new RelationClassificationDAO_JPA(createManager);

        for (RelationClass aClass : RelationClass.values()) {
            dao.save(new RelationClassification(aClass));
        }
    }

    public void setDefaultEnums(ProjectInstance pi) {
        pi.setPriorities(getDefaultPriorities());
        pi.setRelations(getDefaultRelations());
        pi.setResolutions(getDefaultResolutions());
        pi.setRoles(getDefaultRoles());
        pi.setSeverities(getDefaultSeverities());
        pi.setStatuses(getDefaultStatuses());
        pi.setWuTypes(getDefaultWUTypes());
    }

    private List<Role> getDefaultRoles() {
        List<Role> roles = new ArrayList<>();

        RoleClassification analystClass = new RoleClassification(RoleClass.ANALYST);
        RoleClassification designerClass = new RoleClassification(RoleClass.DESIGNER);
        RoleClassification developerClass = new RoleClassification(RoleClass.DEVELOPER);
        RoleClassification documenterClass = new RoleClassification(RoleClass.DOCUMENTER);
        RoleClassification mentorClass = new RoleClassification(RoleClass.MENTOR);
        RoleClassification nonmemberClass = new RoleClassification(RoleClass.NONMEMBER);
        RoleClassification projectManagerClass = new RoleClassification(RoleClass.PROJECTMANAGER);
        RoleClassification testerClass = new RoleClassification(RoleClass.TESTER);
        RoleClassification teamMemberClass = new RoleClassification(RoleClass.TEAMMEMBER);
        RoleClassification stakeholderClass = new RoleClassification(RoleClass.STAKEHOLDER);

        roles.add(new Role("non-member", nonmemberClass));
        roles.add(new Role("anonymous", nonmemberClass));

        roles.add(new Role("mentor", mentorClass));
        roles.add(new Role("scrum master", mentorClass));

        roles.add(new Role("project manager", projectManagerClass));
        roles.add(new Role("manager", projectManagerClass));
        roles.add(new Role("owner", projectManagerClass));
        roles.add(new Role("product manager", projectManagerClass));
        roles.add(new Role("project lead", projectManagerClass));
        roles.add(new Role("project leader", projectManagerClass));
        roles.add(new Role("team leader", projectManagerClass));
        roles.add(new Role("administrator", projectManagerClass));
        roles.add(new Role("tempo project manager", projectManagerClass));

        roles.add(new Role("project admin", stakeholderClass));
        roles.add(new Role("stakeholder", stakeholderClass));
        roles.add(new Role("product owner", stakeholderClass));
        roles.add(new Role("business owner", stakeholderClass));
        roles.add(new Role("release train manager", stakeholderClass));
        roles.add(new Role("watcher", stakeholderClass));
        roles.add(new Role("reporter", stakeholderClass));
        roles.add(new Role("user", stakeholderClass));
        roles.add(new Role("customer", stakeholderClass));

        roles.add(new Role("team member", teamMemberClass));
        roles.add(new Role("member", teamMemberClass));

        roles.add(new Role("analyst", analystClass));
        roles.add(new Role("business analyst", analystClass));

        roles.add(new Role("designer", designerClass));
        roles.add(new Role("architect", designerClass));
        roles.add(new Role("system architect", designerClass));
        roles.add(new Role("ux designer", designerClass));

        roles.add(new Role("developer", developerClass));

        roles.add(new Role("tester", testerClass));

        roles.add(new Role("documenter", documenterClass));

        return roles;
    }

    private List<Status> getDefaultStatuses() {
        List<Status> statuses = new ArrayList<>();

        StatusClassification newClass = new StatusClassification(StatusClass.NEW);
        StatusClassification acceptedClass = new StatusClassification(StatusClass.ACCEPTED);
        StatusClassification inProgressClass = new StatusClassification(StatusClass.INPROGRESS);
        StatusClassification resolvedClass = new StatusClassification(StatusClass.RESOLVED);
        StatusClassification verifiedClass = new StatusClassification(StatusClass.VERIFIED);
        StatusClassification doneClass = new StatusClassification(StatusClass.DONE);
        StatusClassification invalidClass = new StatusClassification(StatusClass.INVALID);

        statuses.add(new Status("new", newClass));
        statuses.add(new Status("todo", newClass));
        statuses.add(new Status("unconfirmed", newClass));
        statuses.add(new Status("funnel", newClass));
        statuses.add(new Status("analysis", newClass));
        statuses.add(new Status("open", newClass));

        statuses.add(new Status("accepted", acceptedClass));
        statuses.add(new Status("assigned", acceptedClass));
        statuses.add(new Status("backlog", acceptedClass));

        statuses.add(new Status("in progress", inProgressClass));

        statuses.add(new Status("resolved", resolvedClass));
        statuses.add(new Status("test", resolvedClass));

        statuses.add(new Status("verified", verifiedClass));
        statuses.add(new Status("feedback", verifiedClass));

        statuses.add(new Status("done", doneClass));
        statuses.add(new Status("closed", doneClass));
        statuses.add(new Status("fixed", doneClass));
        statuses.add(new Status("approved", doneClass));

        statuses.add(new Status("invalid", invalidClass));
        statuses.add(new Status("cancelled", invalidClass));
        statuses.add(new Status("rejected", invalidClass));

        return statuses;
    }

    private List<Priority> getDefaultPriorities() {
        List<Priority> priorities = new ArrayList<>();

        PriorityClassification lowestClass = new PriorityClassification(PriorityClass.LOWEST);
        PriorityClassification lowClass = new PriorityClassification(PriorityClass.LOW);
        PriorityClassification normalClass = new PriorityClassification(PriorityClass.NORMAL);
        PriorityClassification highClass = new PriorityClassification(PriorityClass.HIGH);
        PriorityClassification highestClass = new PriorityClassification(PriorityClass.HIGHEST);

        priorities.add(new Priority("lowest", lowestClass));

        priorities.add(new Priority("low", lowClass));

        priorities.add(new Priority("normal", normalClass));
        priorities.add(new Priority("medium", normalClass));
        priorities.add(new Priority("standard", normalClass));
        priorities.add(new Priority("moderate", normalClass));
        priorities.add(new Priority("common", normalClass));

        priorities.add(new Priority("high", highClass));

        priorities.add(new Priority("highest", highestClass));
        priorities.add(new Priority("immediate", highestClass));
        priorities.add(new Priority("urgent", highestClass));

        return priorities;
    }

    private List<Severity> getDefaultSeverities() {
        List<Severity> severities = new ArrayList<>();

        SeverityClassification trivialClass = new SeverityClassification(SeverityClass.TRIVIAL);
        SeverityClassification minorClass = new SeverityClassification(SeverityClass.MINOR);
        SeverityClassification normalClass = new SeverityClassification(SeverityClass.NORMAL);
        SeverityClassification majorClass = new SeverityClassification(SeverityClass.MAJOR);
        SeverityClassification criticalClass = new SeverityClassification(SeverityClass.CRITICAL);

        severities.add(new Severity("trivial", trivialClass));

        severities.add(new Severity("minor", minorClass));
        severities.add(new Severity("small", minorClass));

        severities.add(new Severity("normal", normalClass));
        severities.add(new Severity("moderate", normalClass));
        severities.add(new Severity("common", normalClass));
        severities.add(new Severity("standard", normalClass));
        severities.add(new Severity("medium", normalClass));

        severities.add(new Severity("major", majorClass));
        severities.add(new Severity("big", majorClass));

        severities.add(new Severity("critical", criticalClass));
        severities.add(new Severity("blocker", criticalClass));

        return severities;
    }

    private List<Resolution> getDefaultResolutions() {
        List<Resolution> resolutions = new ArrayList<>();

        ResolutionClassification invalidClass = new ResolutionClassification(ResolutionClass.INVALID);
        ResolutionClassification duplicateClass = new ResolutionClassification(ResolutionClass.DUPLICATE);
        ResolutionClassification wontFixClass = new ResolutionClassification(ResolutionClass.WONTFIX);
        ResolutionClassification fixedClass = new ResolutionClassification(ResolutionClass.FIXED);
        ResolutionClassification worksAsDesignedClass = new ResolutionClassification(ResolutionClass.WORKSASDESIGNED);
        ResolutionClassification finishedClass = new ResolutionClassification(ResolutionClass.FINISHED);
        ResolutionClassification worksForMeClass = new ResolutionClassification(ResolutionClass.WORKSFORME);
        ResolutionClassification incompleteClass = new ResolutionClassification(ResolutionClass.INCOMPLETE);
        ResolutionClassification unfinishedClass = new ResolutionClassification(ResolutionClass.UNFINISHED);

        resolutions.add(new Resolution("duplicate", duplicateClass));

        resolutions.add(new Resolution("invalid", invalidClass));

        resolutions.add(new Resolution("won't fix", wontFixClass));
        resolutions.add(new Resolution("won't do", wontFixClass));

        resolutions.add(new Resolution("works as designed", worksAsDesignedClass));

        resolutions.add(new Resolution("fixed", fixedClass));
        resolutions.add(new Resolution("done", fixedClass));
        resolutions.add(new Resolution("fixed upstream", fixedClass));

        resolutions.add(new Resolution("finished", finishedClass));

        resolutions.add(new Resolution("incomplete", incompleteClass));
        resolutions.add(new Resolution("cannot reproduce", incompleteClass));

        resolutions.add(new Resolution("works for me", worksForMeClass));

        resolutions.add(new Resolution("unfinished", unfinishedClass));

        return resolutions;
    }

    private List<WorkUnitType> getDefaultWUTypes() {
        List<WorkUnitType> types = new ArrayList<>();

        WorkUnitTypeClassification bugClass = new WorkUnitTypeClassification(WorkUnitTypeClass.BUG);
        WorkUnitTypeClassification taskClass = new WorkUnitTypeClassification(WorkUnitTypeClass.TASK);
        WorkUnitTypeClassification enhancementClass = new WorkUnitTypeClassification(WorkUnitTypeClass.ENHANCEMENT);
        WorkUnitTypeClassification featureClass = new WorkUnitTypeClassification(WorkUnitTypeClass.FEATURE);

        types.add(new WorkUnitType("bug", bugClass));
        types.add(new WorkUnitType("defect", bugClass));

        types.add(new WorkUnitType("task", taskClass));

        types.add(new WorkUnitType("enhancement", enhancementClass));
        types.add(new WorkUnitType("improvement", enhancementClass));

        types.add(new WorkUnitType("feature", featureClass));
        types.add(new WorkUnitType("new feature", featureClass));

        return types;
    }

    private List<Relation> getDefaultRelations() {
        List<Relation> relations = new ArrayList<>();

        RelationClassification duplicatesClass = new RelationClassification(RelationClass.DUPLICATES);
        RelationClassification duplicatedByClass = new RelationClassification(RelationClass.DUPLICATEDBY);
        RelationClassification blocksClass = new RelationClassification(RelationClass.BLOCKS);
        RelationClassification blockedByClass = new RelationClassification(RelationClass.BLOCKEDBY);
        RelationClassification relatesToClass = new RelationClassification(RelationClass.RELATESTO);
        RelationClassification precedesClass = new RelationClassification(RelationClass.PRECEDES);
        RelationClassification followsClass = new RelationClassification(RelationClass.FOLLOWS);
        RelationClassification copiedFromClass = new RelationClassification(RelationClass.COPIEDFROM);
        RelationClassification copiedByClass = new RelationClassification(RelationClass.COPIEDBY);
        RelationClassification childOfClass = new RelationClassification(RelationClass.CHILDOF);
        RelationClassification parentOfClass = new RelationClassification(RelationClass.PARENTOF);
        RelationClassification causesClass = new RelationClassification(RelationClass.CAUSES);
        RelationClassification causedByClass = new RelationClassification(RelationClass.CAUSEDBY);
        RelationClassification resolvesClass = new RelationClassification(RelationClass.RESOLVES);
        RelationClassification resolvedByClass = new RelationClassification(RelationClass.RESOLVEDBY);

        relations.add(new Relation("duplicates", duplicatesClass));
        relations.add(new Relation("duplicate of", duplicatesClass));

        relations.add(new Relation("duplicated by", duplicatedByClass));

        relations.add(new Relation("blocks", blocksClass));

        relations.add(new Relation("blocked by", blockedByClass));
        relations.add(new Relation("depends on", blockedByClass));

        relations.add(new Relation("relates to", relatesToClass));
        relations.add(new Relation("relates", relatesToClass));
        relations.add(new Relation("related", relatesToClass));
        relations.add(new Relation("related to", relatesToClass));
        relations.add(new Relation("mentions", relatesToClass));
        relations.add(new Relation("mentioned by", relatesToClass));
        relations.add(new Relation("has attached", relatesToClass));
        relations.add(new Relation("attached to", relatesToClass));

        relations.add(new Relation("precedes", precedesClass));
        relations.add(new Relation("predecessor", precedesClass));
        relations.add(new Relation("before", precedesClass));

        relations.add(new Relation("follows", followsClass));
        relations.add(new Relation("successor", followsClass));
        relations.add(new Relation("after", followsClass));

        relations.add(new Relation("copied from", copiedFromClass));
        relations.add(new Relation("clones", copiedFromClass));

        relations.add(new Relation("copied by", copiedByClass));
        relations.add(new Relation("copied to", copiedByClass));
        relations.add(new Relation("cloned by", copiedByClass));

        relations.add(new Relation("child of", childOfClass));
        relations.add(new Relation("sub-task of", childOfClass));

        relations.add(new Relation("parent of", parentOfClass));

        relations.add(new Relation("causes", causesClass));

        relations.add(new Relation("caused by", causedByClass));

        relations.add(new Relation("resolves", resolvesClass));

        relations.add(new Relation("resolved by", resolvedByClass));

        return relations;
    }
}
