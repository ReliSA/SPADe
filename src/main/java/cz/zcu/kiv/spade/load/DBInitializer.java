package cz.zcu.kiv.spade.load;

import cz.zcu.kiv.spade.dao.*;
import cz.zcu.kiv.spade.dao.jpa.*;
import cz.zcu.kiv.spade.domain.*;
import cz.zcu.kiv.spade.domain.enums.*;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

public class DBInitializer {

    private EntityManager em;

    public DBInitializer(EntityManager em) {
        this.em = em;
    }

    public void initializeDatabase() {
        loadRoleClassifications();
        loadStatusClassifications();
        loadPriorityClassifications();
        loadSeverityClassifications();
        loadResolutionClassifications();
        loadTypeClassifications();
        loadRelationClassifications();
    }

    private void loadRoleClassifications() {
        GenericDAO<RoleClassification> dao = new RoleClassificationDAO_JPA(em);

        for (RoleClass aClass : RoleClass.values()) {
            dao.save(new RoleClassification(aClass));
        }
    }

    private void loadStatusClassifications() {
        GenericDAO<StatusClassification> dao = new StatusClassificationDAO_JPA(em);

        for (StatusClass aClass : StatusClass.values()) {
            dao.save(new StatusClassification(aClass));
        }
    }

    private void loadPriorityClassifications() {
        GenericDAO<PriorityClassification> dao = new PriorityClassificationDAO_JPA(em);

        for (PriorityClass aClass : PriorityClass.values()) {
            dao.save(new PriorityClassification(aClass));
        }
    }

    private void loadSeverityClassifications() {
        GenericDAO<SeverityClassification> dao = new SeverityClassificationDAO_JPA(em);

        for (SeverityClass aClass : SeverityClass.values()) {
            dao.save(new SeverityClassification(aClass));
        }
    }

    private void loadResolutionClassifications() {
        GenericDAO<ResolutionClassification> dao = new ResolutionClassificationDAO_JPA(em);

        for (ResolutionClass aClass : ResolutionClass.values()) {
            dao.save(new ResolutionClassification(aClass));
        }
    }

    private void loadTypeClassifications() {
        GenericDAO<WorkUnitTypeClassification> dao = new WorkUnitTypeClassificationDAO_JPA(em);

        for (WorkUnitTypeClass aClass : WorkUnitTypeClass.values()) {
            dao.save(new WorkUnitTypeClassification(aClass));
        }
    }

    private void loadRelationClassifications() {
        GenericDAO<RelationClassification> dao = new RelationClassificationDAO_JPA(em);

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
        pi.setStatuses(getDeafultStatuses());
        pi.setWuTypes(getDefaultWUTypes());
    }

    private List<Role> getDefaultRoles() {
        List<Role> roles = new ArrayList<>();
        RoleClassificationDAO dao = new RoleClassificationDAO_JPA(em);

        RoleClassification analystClass = dao.findByClass(RoleClass.ANALYST);
        RoleClassification designerClass = dao.findByClass(RoleClass.DESIGNER);
        RoleClassification developerClass = dao.findByClass(RoleClass.DEVELOPER);
        RoleClassification documenterClass = dao.findByClass(RoleClass.DOCUMENTER);
        RoleClassification mentorClass = dao.findByClass(RoleClass.MENTOR);
        RoleClassification nonmemberClass = dao.findByClass(RoleClass.NONMEMBER);
        RoleClassification projectManagerClass = dao.findByClass(RoleClass.PROJECTMANAGER);
        RoleClassification testerClass = dao.findByClass(RoleClass.TESTER);
        RoleClassification teamMemberClass = dao.findByClass(RoleClass.TEAMMEMBER);
        RoleClassification stakeholderClass = dao.findByClass(RoleClass.STAKEHOLDER);

        roles.add(new Role("analyst", analystClass));
        roles.add(new Role("bussiness analyst", analystClass));

        roles.add(new Role("designer", designerClass));
        roles.add(new Role("architect", designerClass));
        roles.add(new Role("system architect", designerClass));
        roles.add(new Role("ux designer", designerClass));

        roles.add(new Role("developer", developerClass));

        roles.add(new Role("documenter", documenterClass));

        roles.add(new Role("mentor", mentorClass));
        roles.add(new Role("scrum master", mentorClass));

        roles.add(new Role("non-member", nonmemberClass));
        roles.add(new Role("anonymous", nonmemberClass));

        roles.add(new Role("project manager", projectManagerClass));
        roles.add(new Role("manager", projectManagerClass));
        roles.add(new Role("owner", projectManagerClass));
        roles.add(new Role("product manager", projectManagerClass));
        roles.add(new Role("project lead", projectManagerClass));
        roles.add(new Role("project leader", projectManagerClass));
        roles.add(new Role("team leader", projectManagerClass));
        roles.add(new Role("administrator", projectManagerClass));
        roles.add(new Role("tempo project manager", projectManagerClass));

        roles.add(new Role("tester", testerClass));

        roles.add(new Role("team member", teamMemberClass));
        roles.add(new Role("member", teamMemberClass));

        roles.add(new Role("project admin", stakeholderClass));
        roles.add(new Role("stakeholder", stakeholderClass));
        roles.add(new Role("product owner", stakeholderClass));
        roles.add(new Role("bussiness owner", stakeholderClass));
        roles.add(new Role("release train manager", stakeholderClass));
        roles.add(new Role("watcher", stakeholderClass));
        roles.add(new Role("reporter", stakeholderClass));
        roles.add(new Role("user", stakeholderClass));
        roles.add(new Role("customer", stakeholderClass));

        return roles;
    }

    private List<Status> getDeafultStatuses() {
        List<Status> statuses = new ArrayList<>();
        StatusClassificationDAO dao = new StatusClassificationDAO_JPA(em);

        StatusClassification newClass = dao.findByClass(StatusClass.NEW);
        StatusClassification acceptedClass = dao.findByClass(StatusClass.ACCEPTED);
        StatusClassification inProgressClass = dao.findByClass(StatusClass.INPROGRESS);
        StatusClassification resolvedClass = dao.findByClass(StatusClass.RESOLVED);
        StatusClassification verifiedClass = dao.findByClass(StatusClass.VERIFIED);
        StatusClassification doneClass = dao.findByClass(StatusClass.DONE);
        StatusClassification invalidClass = dao.findByClass(StatusClass.INVALID);

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
        PriorityClassificationDAO dao = new PriorityClassificationDAO_JPA(em);

        PriorityClassification lowestClass = dao.findByClass(PriorityClass.LOWEST);
        PriorityClassification lowClass = dao.findByClass(PriorityClass.LOW);
        PriorityClassification normalClass = dao.findByClass(PriorityClass.NORMAL);
        PriorityClassification highClass = dao.findByClass(PriorityClass.HIGH);
        PriorityClassification highestClass = dao.findByClass(PriorityClass.HIGHEST);

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
        SeverityClassificationDAO dao = new SeverityClassificationDAO_JPA(em);

        SeverityClassification trivialClass = dao.findByClass(SeverityClass.TRIVIAL);
        SeverityClassification minorClass = dao.findByClass(SeverityClass.MINOR);
        SeverityClassification normalClass = dao.findByClass(SeverityClass.NORMAL);
        SeverityClassification majorClass = dao.findByClass(SeverityClass.MAJOR);
        SeverityClassification criticalClass = dao.findByClass(SeverityClass.CRITICAL);

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
        ResolutionClassificationDAO dao = new ResolutionClassificationDAO_JPA(em);

        ResolutionClassification invalidClass = dao.findByClass(ResolutionClass.INVALID);
        ResolutionClassification duplicateClass = dao.findByClass(ResolutionClass.DUPLICATE);
        ResolutionClassification wontFixClass = dao.findByClass(ResolutionClass.WONTFIX);
        ResolutionClassification fixedClass = dao.findByClass(ResolutionClass.FIXED);
        ResolutionClassification worksAsDesignedClass = dao.findByClass(ResolutionClass.WORKSASDESIGNED);
        ResolutionClassification finishedClass = dao.findByClass(ResolutionClass.FINISHED);
        ResolutionClassification worksForMeClass = dao.findByClass(ResolutionClass.WORKSFORME);
        ResolutionClassification incompleteClass = dao.findByClass(ResolutionClass.INCOMPLETE);
        ResolutionClassification unfinishedClass = dao.findByClass(ResolutionClass.UNFINISHED);

        resolutions.add(new Resolution("invalid", invalidClass));

        resolutions.add(new Resolution("duplicate", duplicateClass));

        resolutions.add(new Resolution("won't fix", wontFixClass));
        resolutions.add(new Resolution("won't do", wontFixClass));

        resolutions.add(new Resolution("fixed", fixedClass));
        resolutions.add(new Resolution("done", fixedClass));
        resolutions.add(new Resolution("fixed upstream", fixedClass));

        resolutions.add(new Resolution("works as designed", worksAsDesignedClass));

        resolutions.add(new Resolution("finished", finishedClass));

        resolutions.add(new Resolution("works for me", worksForMeClass));

        resolutions.add(new Resolution("incomplete", incompleteClass));
        resolutions.add(new Resolution("cannot reproduce", incompleteClass));

        resolutions.add(new Resolution("unfinished", unfinishedClass));

        return resolutions;
    }

    private List<WorkUnitType> getDefaultWUTypes() {
        List<WorkUnitType> types = new ArrayList<>();
        WorkUnitTypeClassificationDAO dao = new WorkUnitTypeClassificationDAO_JPA(em);

        WorkUnitTypeClassification bugClass = dao.findByClass(WorkUnitTypeClass.BUG);
        WorkUnitTypeClassification taskClass = dao.findByClass(WorkUnitTypeClass.TASK);
        WorkUnitTypeClassification enhancementClass = dao.findByClass(WorkUnitTypeClass.ENHANCEMENT);
        WorkUnitTypeClassification featureClass = dao.findByClass(WorkUnitTypeClass.FEATURE);

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
        RelationClassificationDAO dao = new RelationClassificationDAO_JPA(em);

        RelationClassification duplicatesClass = dao.findByClass(RelationClass.DUPLICATES);
        RelationClassification duplicatedByClass = dao.findByClass(RelationClass.DUPLICATEDBY);
        RelationClassification blocksClass = dao.findByClass(RelationClass.BLOCKS);
        RelationClassification blockedByClass = dao.findByClass(RelationClass.BLOCKEDBY);
        RelationClassification relatesToClass = dao.findByClass(RelationClass.RELATESTO);
        RelationClassification precedesClas = dao.findByClass(RelationClass.PRECEDES);
        RelationClassification followsClass = dao.findByClass(RelationClass.FOLLOWS);
        RelationClassification copiedFromClass = dao.findByClass(RelationClass.COPIEDFROM);
        RelationClassification copiedByClass = dao.findByClass(RelationClass.COPIEDBY);
        RelationClassification childOfClass = dao.findByClass(RelationClass.CHILDOF);
        RelationClassification parentOfClass = dao.findByClass(RelationClass.PARENTOF);
        RelationClassification causesClass = dao.findByClass(RelationClass.CAUSES);
        RelationClassification causedByClass = dao.findByClass(RelationClass.CAUSEDBY);
        RelationClassification resolvesClass = dao.findByClass(RelationClass.RESOLVES);
        RelationClassification resolvedByClass = dao.findByClass(RelationClass.RESOLVEDBY);

        relations.add(new Relation("duplicates", duplicatesClass));
        relations.add(new Relation("duplicate of", duplicatesClass));

        relations.add(new Relation("duplicated by", duplicatedByClass));

        relations.add(new Relation("blocks", blocksClass));

        relations.add(new Relation("blocked by", blockedByClass));
        relations.add(new Relation("depends on", blockedByClass));

        relations.add(new Relation("relates to", relatesToClass));
        relations.add(new Relation("related", relatesToClass));
        relations.add(new Relation("related to", relatesToClass));
        relations.add(new Relation("mentions", relatesToClass));
        relations.add(new Relation("mentioned by", relatesToClass));
        relations.add(new Relation("has attached", relatesToClass));
        relations.add(new Relation("attached to", relatesToClass));

        relations.add(new Relation("precedes", precedesClas));
        relations.add(new Relation("precedessor", precedesClas));
        relations.add(new Relation("before", precedesClas));

        relations.add(new Relation("follows", followsClass));
        relations.add(new Relation("successor", followsClass));
        relations.add(new Relation("after", followsClass));

        relations.add(new Relation("copied from", copiedFromClass));
        relations.add(new Relation("clones", copiedFromClass));

        relations.add(new Relation("copied by", copiedByClass));
        relations.add(new Relation("copied to", copiedByClass));
        relations.add(new Relation("cloned by", copiedByClass));

        relations.add(new Relation("child of", childOfClass));
        relations.add(new Relation("subtask of", childOfClass));

        relations.add(new Relation("parent of", parentOfClass));

        relations.add(new Relation("causes", causesClass));

        relations.add(new Relation("caused by", causedByClass));

        relations.add(new Relation("resolves", resolvesClass));

        relations.add(new Relation("resolved by", resolvedByClass));

        return relations;
    }
}
