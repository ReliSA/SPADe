package cz.zcu.kiv.spade.pumps.issuetracking.redmine;

import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.bean.*;
import com.taskadapter.redmineapi.bean.Project;
import cz.zcu.kiv.spade.App;
import cz.zcu.kiv.spade.domain.*;
import cz.zcu.kiv.spade.domain.Role;
import cz.zcu.kiv.spade.domain.enums.*;
import cz.zcu.kiv.spade.pumps.issuetracking.EnumsMiner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;

class RedmineEnumsMiner extends EnumsMiner {

    private static final String CUSTOM_FIELDS_PERMISSION_ERR_MSG = "Insufficient permissions for custom fields";
    private static final String STATUSES_PERMISSION_ERR_MSG = "Insufficient permissions for statuses";
    private static final String PRIORITIES_PERMISSION_ERR_MSG = "Insufficient permissions for priorities";
    private static final String CATEGORIES_PERMISSION_ERR_MSG = "Insufficient permissions for categories";

    RedmineEnumsMiner(RedminePump pump) {
        super(pump);
    }

    @Override
    protected void mineSeverities() {

        List<CustomFieldDefinition> definitions = new ArrayList<>();
        try {
            definitions = ((RedmineManager) pump.getRootObject()).getCustomFieldManager().getCustomFieldDefinitions();
        } catch (RedmineException e) {
            App.printLogMsg(this, CUSTOM_FIELDS_PERMISSION_ERR_MSG);
        }
        for (CustomFieldDefinition def : definitions) {
            if (def.getName().toLowerCase().equals(SEVERITY_FIELD_NAME)) {

                for (String issueSeverity : def.getPossibleValues()) {
                    boolean found = false;
                    for (Severity severity : pump.getPi().getSeverities()) {
                        if (toLetterOnlyLowerCase(severity.getName()).equals(toLetterOnlyLowerCase(issueSeverity))) {
                            severity.setName(issueSeverity);
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        Severity newSeverity = new Severity(issueSeverity, new SeverityClassification(SeverityClass.UNASSIGNED));
                        pump.getPi().getSeverities().add(newSeverity);
                    }
                }
                break;
            }
        }
    }

    @Override
    protected void mineRoles() {

        List<com.taskadapter.redmineapi.bean.Role> roles = new ArrayList<>();
        try {
            roles = ((RedmineManager) pump.getRootObject()).getUserManager().getRoles();
        } catch (RedmineException e) {
            App.printLogMsg(this, ROLES_PERMISSION_ERR_MSG);
        }
        for (com.taskadapter.redmineapi.bean.Role redmineRole : roles) {
            boolean found = false;
            for (Role role : pump.getPi().getRoles()) {
                if (toLetterOnlyLowerCase(role.getName()).equals(toLetterOnlyLowerCase(redmineRole.getName()))) {
                    role.setName(redmineRole.getName());
                    role.setExternalId(redmineRole.getId().toString());
                    found = true;
                    break;
                }
            }
            if (!found) {
                Role newRole = new Role(redmineRole.getName(), new RoleClassification(RoleClass.UNASSIGNED));
                newRole.setExternalId(redmineRole.getId().toString());
                pump.getPi().getRoles().add(newRole);
            }
        }
    }

    @Override
    protected void mineStatuses() {

        List<IssueStatus> statuses = new ArrayList<>();
        try {
            statuses = ((RedmineManager) pump.getRootObject()).getIssueManager().getStatuses();
        } catch (RedmineException e) {
            App.printLogMsg(this, STATUSES_PERMISSION_ERR_MSG);
        }
        for (IssueStatus issueStatus : statuses) {
            boolean found = false;
            for (Status status : pump.getPi().getStatuses()) {
                if (toLetterOnlyLowerCase(status.getName()).equals(toLetterOnlyLowerCase(issueStatus.getName()))) {
                    status.setName(issueStatus.getName());
                    status.setExternalId(issueStatus.getId().toString());
                    found = true;
                    break;
                }
            }
            if (!found) {
                Status newStatus = new Status(issueStatus.getName(), new StatusClassification(StatusClass.UNASSIGNED));
                newStatus.setExternalId(issueStatus.getId().toString());
                pump.getPi().getStatuses().add(newStatus);
            }
        }
    }

    @Override
    protected void minePriorities() {

        List<IssuePriority> priorities = new ArrayList<>();
        try {
            priorities = ((RedmineManager) pump.getRootObject()).getIssueManager().getIssuePriorities();
        } catch (RedmineException e) {
            App.printLogMsg(this, PRIORITIES_PERMISSION_ERR_MSG);
        }
        for (IssuePriority issuePriority : priorities) {
            boolean found = false;
            for (Priority priority : pump.getPi().getPriorities()) {
                if (toLetterOnlyLowerCase(priority.getName()).equals(toLetterOnlyLowerCase(issuePriority.getName()))) {
                    priority.setName(issuePriority.getName());
                    priority.setExternalId(issuePriority.getId().toString());
                    found = true;
                    break;
                }
            }
            if (!found) {
                Priority newPriority = new Priority(issuePriority.getName(), new PriorityClassification(PriorityClass.UNASSIGNED));
                newPriority.setExternalId(issuePriority.getId().toString());
                pump.getPi().getPriorities().add(newPriority);
            }
        }
    }

    @Override
    protected void mineWUTypes() {

        for (Tracker tracker : ((Project) pump.getSecondaryObject()).getTrackers()) {
            boolean found = false;
            for (WorkUnitType type : pump.getPi().getWuTypes()) {
                if (toLetterOnlyLowerCase(type.getName()).equals(toLetterOnlyLowerCase(tracker.getName()))) {
                    type.setName(tracker.getName());
                    type.setExternalId(tracker.getId().toString());
                    found = true;
                    break;
                }
            }
            if (!found) {
                WorkUnitType newType = new WorkUnitType(tracker.getName(), new WorkUnitTypeClassification(WorkUnitTypeClass.UNASSIGNED));
                newType.setExternalId(tracker.getId().toString());
                pump.getPi().getWuTypes().add(newType);
            }
        }
    }

    @Override
    protected void mineWURelationTypes() {
        resolveRelation(PARENT_OF);
        resolveRelation(CHILD_OF);
    }

    @Override
    protected void mineCategories() {
        Collection<IssueCategory> issueCategories = new LinkedHashSet<>();

        try {
            issueCategories = ((RedmineManager) pump.getRootObject()).getIssueManager().getCategories(((Project) pump.getSecondaryObject()).getId());
        } catch (RedmineException e) {
            App.printLogMsg(this, CATEGORIES_PERMISSION_ERR_MSG);
        }

        for (IssueCategory issueCategory : issueCategories) {
            Category category = new Category();
            category.setExternalId(issueCategory.getId().toString());
            category.setName(issueCategory.getName());
            pump.getPi().getCategories().add(category);
        }
    }

    @Override
    protected void mineResolutions() {
        List<CustomFieldDefinition> definitions = new ArrayList<>();
        try {
            definitions = ((RedmineManager) pump.getRootObject()).getCustomFieldManager().getCustomFieldDefinitions();
        } catch (RedmineException e) {
            App.printLogMsg(this, CUSTOM_FIELDS_PERMISSION_ERR_MSG);
        }
        for (CustomFieldDefinition def : definitions) {
            if (def.getName().toLowerCase().equals(RESOLUTION_FIELD_NAME)) {

                for (String issueResolution : def.getPossibleValues()) {
                    boolean found = false;
                    for (Resolution resolution : pump.getPi().getResolutions()) {
                        if (toLetterOnlyLowerCase(resolution.getName()).equals(toLetterOnlyLowerCase(issueResolution))) {
                            resolution.setName(issueResolution);
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        Resolution newResolution = new Resolution(issueResolution, new ResolutionClassification(ResolutionClass.UNASSIGNED));
                        pump.getPi().getResolutions().add(newResolution);
                    }
                }
                break;
            }
        }
    }
}
