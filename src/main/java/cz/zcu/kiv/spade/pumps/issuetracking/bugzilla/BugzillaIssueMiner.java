package cz.zcu.kiv.spade.pumps.issuetracking.bugzilla;

import b4j.core.*;
import b4j.core.session.bugzilla.BugzillaClient;
import cz.zcu.kiv.spade.domain.*;
import cz.zcu.kiv.spade.domain.Priority;
import cz.zcu.kiv.spade.domain.Resolution;
import cz.zcu.kiv.spade.domain.Severity;
import cz.zcu.kiv.spade.domain.Status;
import cz.zcu.kiv.spade.domain.enums.*;
import cz.zcu.kiv.spade.pumps.issuetracking.IssueMiner;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

class BugzillaIssueMiner extends IssueMiner<Issue> {

    private final BugzillaCommentMiner commentMiner;
    private final BugzillaAttachmentMiner attachmentMiner;

    BugzillaIssueMiner(BugzillaPump pump) {
        super(pump);
        commentMiner = new BugzillaCommentMiner(pump);
        attachmentMiner = new BugzillaAttachmentMiner(pump);
    }

    @Override
    public void mineItems() {
        Map<String, Object> criteria = new HashMap<>();
        criteria.put(BugzillaPump.PRODUCT_CRITERION, pump.getPi().getName());
        criteria.put(BugzillaPump.LIMIT_CRITERION, 0);
        Iterable<Issue> issues = null;
        try {
            issues = ((BugzillaClient) pump.getRootObject()).getBugClient().findBugs(criteria).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        if (issues == null) return;
        for (Issue issue : issues) {
            mineItem(issue);
        }
    }

    @Override
    protected void mineItem(Issue issue) {
        WorkUnit unit = new WorkUnit();
        unit.setNumber(getNumberAfterLastDash(issue.getId()));
        unit.setExternalId(issue.getId());
        unit.setUrl(issue.getUri());
        unit.setName(issue.getSummary());
        unit.setDescription(issue.getDescription());
        unit.setAuthor(addPerson(((BugzillaPeopleMiner) pump.getPeopleMiner()).generateIdentity(issue.getReporter())));
        unit.setCreated(issue.getCreationTimestamp());
        unit.setStartDate(issue.getCreationTimestamp());
        unit.setAssignee(addPerson(((BugzillaPeopleMiner) pump.getPeopleMiner()).generateIdentity(issue.getAssignee())));
        unit.setStatus(resolveStatus(issue.getStatus()));
        unit.setType(resolveType(issue));
        unit.setPriority(resolvePriority(issue.getPriority()));
        unit.setResolution(resolveResolution(issue.getResolution()));
        unit.setSeverity(resolveSeverity(issue));
        resolveCategories(unit, issue);

        pump.getPi().getProject().addUnit(unit);

        if (issue.getAttachments() != null) {
            attachmentMiner.mineAttachments(unit, issue.getAttachments());
        }
        mineHistory(unit, issue);

        for (Version version : issue.getPlannedVersions()) {
            Iteration iteration = new Iteration();
            iteration.setExternalId(version.getId().toString());
            if (version.getReleaseDate() != null) {
                iteration.setEndDate(version.getReleaseDate());
            }
            if ((unit.getIteration() == null || unit.getIteration().getEndDate() == null) ||
                    (version.getReleaseDate() != null && unit.getIteration().getEndDate().after(version.getReleaseDate()))) {
                unit.setIteration(iteration);
            }
        }
    }

    private Priority resolvePriority(b4j.core.Priority bugzillaPriority) {
        for (Priority priority : pump.getPi().getPriorities()) {
            if (toLetterOnlyLowerCase(priority.getName()).equals(toLetterOnlyLowerCase(bugzillaPriority.getName()))) {
                priority.setName(bugzillaPriority.getName());
                return priority;
            }
        }

        Priority newPriority = new Priority(bugzillaPriority.getName(), priorityDao.findByClass(PriorityClass.UNASSIGNED));
        pump.getPi().getPriorities().add(newPriority);
        return newPriority;
    }

    private Resolution resolveResolution(b4j.core.Resolution bugzillaResolution) {
        for (Resolution resolution : pump.getPi().getResolutions()) {
            if (toLetterOnlyLowerCase(resolution.getName()).equals(toLetterOnlyLowerCase(bugzillaResolution.getName()))) {
                resolution.setName(bugzillaResolution.getName());
                return resolution;
            }
        }

        Resolution newResolution = new Resolution(bugzillaResolution.getName(), resolutionDao.findByClass(ResolutionClass.UNASSIGNED));
        pump.getPi().getResolutions().add(newResolution);
        return newResolution;
    }

    private WorkUnitType resolveType(Issue issue) {
        String bugzillaType = issue.getType().getName();
        if (issue.getSeverity().getName().equals(WorkUnitTypeClass.ENHANCEMENT.name().toLowerCase())) {
            bugzillaType = WorkUnitTypeClass.ENHANCEMENT.name().toLowerCase();
        }
        for (WorkUnitType type : pump.getPi().getWuTypes()) {
            if (toLetterOnlyLowerCase(type.getName()).equals(toLetterOnlyLowerCase(bugzillaType))) {
                type.setName(bugzillaType);
                return type;
            }
        }

        WorkUnitType newType = new WorkUnitType(bugzillaType, typeDao.findByClass(WorkUnitTypeClass.UNASSIGNED));
        pump.getPi().getWuTypes().add(newType);
        return newType;
    }

    private Status resolveStatus(b4j.core.Status bugzillaStatus) {
        for (Status status : pump.getPi().getStatuses()) {
            if (toLetterOnlyLowerCase(status.getName()).equals(toLetterOnlyLowerCase(bugzillaStatus.getName()))) {
                status.setName(bugzillaStatus.getName());
                return status;
            }
        }

        Status newStatus = new Status(bugzillaStatus.getName(), statusDao.findByClass(StatusClass.UNASSIGNED));
        pump.getPi().getStatuses().add(newStatus);
        return newStatus;
    }

    @Override
    protected void resolveCategories(WorkUnit unit, Issue issue) {
        for (Component component : issue.getComponents()) {
            boolean found = false;
            for (Category category : pump.getPi().getCategories()) {
                if (category.getName().equals(component.getName())) {
                    unit.getCategories().add(category);
                    found = true;
                    break;
                }
            }
            if (!found) {
                Category newCategory = new Category();
                newCategory.setName(component.getName());
                newCategory.setDescription(component.getDescription());
                newCategory.setExternalId(component.getId());
                unit.getCategories().add(newCategory);
                pump.getPi().getCategories().add(newCategory);
            }
        }

        Classification classification = issue.getClassification();
        boolean found = false;
        for (Category category : pump.getPi().getCategories()) {
            if (category.getName().equals(classification.getName())) {
                unit.getCategories().add(category);
                found = true;
                break;
            }
        }
        if (!found) {
            Category newCategory = new Category();
            newCategory.setName(classification.getName());
            newCategory.setDescription(classification.getDescription());
            newCategory.setExternalId(classification.getId());
            unit.getCategories().add(newCategory);
            pump.getPi().getCategories().add(newCategory);
        }
    }

    @Override
    protected Severity resolveSeverity(Issue issue) {
        return null;
    }

    @Override
    protected void mineWorklogs(WorkUnit unit, Issue issue) {
        // Bugzilla doesn't track time spent
    }

    @Override
    protected void mineComments(WorkUnit unit, Issue issue) {
        for (Comment comment : issue.getComments()) {
            commentMiner.generateUnitCommentConfig(unit, comment);
        }
    }

    @Override
    protected void mineHistory(WorkUnit unit, Issue issue) {
        super.mineHistory(unit, issue);
        if (issue.isClosed() && issue.getUpdateTimestamp() != null) {
            generateClosureConfig(unit, issue.getUpdateTimestamp());
        }
    }
}
