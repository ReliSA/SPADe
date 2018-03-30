package cz.zcu.kiv.spade.pumps.issuetracking.jira;

import com.atlassian.jira.rest.client.JiraRestClient;
import com.atlassian.jira.rest.client.domain.*;
import com.atlassian.jira.rest.client.domain.Project;
import cz.zcu.kiv.spade.App;
import cz.zcu.kiv.spade.domain.*;
import cz.zcu.kiv.spade.domain.Priority;
import cz.zcu.kiv.spade.domain.Resolution;
import cz.zcu.kiv.spade.domain.enums.*;
import cz.zcu.kiv.spade.pumps.issuetracking.EnumsMiner;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

class JiraEnumsMiner extends EnumsMiner {

    private static final String PRIORITY_DESCRIPTION_FORMAT = "%s\nStatus color: %s";

    public enum Status {
        NEW,
        DONE,
        INDETERMINATE,
    }

    JiraEnumsMiner(JiraPump pump) {
        super(pump);
    }

    @Override
    protected void mineCategories() {
        Iterable<BasicComponent> components = ((Project) pump.getSecondaryObject()).getComponents();
        for (BasicComponent component : components) {
            Category category = new Category();
            if(component.getId() != null) category.setExternalId(component.getId().toString());
            category.setName(component.getName());
            category.setDescription(component.getDescription());
            pump.getPi().getCategories().add(category);
        }
    }

    @Override
    protected void mineWURelationTypes() {
        Iterable<IssuelinksType> types = new ArrayList<>();
        try {
            types = ((JiraRestClient) pump.getRootObject()).getMetadataClient().getIssueLinkTypes().get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        for (IssuelinksType linksType : types) {
            mineWURelationType(linksType.getInward(), linksType.getName(), linksType.getId());
            mineWURelationType(linksType.getOutward(), linksType.getName(), linksType.getId());
        }
    }

    private void mineWURelationType(String name, String sClass, String id){
        boolean found = false;
        for (Relation relation : pump.getPi().getRelations()) {
            if (toLetterOnlyLowerCase(relation.getName()).equals(toLetterOnlyLowerCase(name))) {
                relation.setName(name);
                relation.setExternalId(name);
                found = true;
                break;
            }
        }
        if (!found) {
            RelationClass aClass = RelationClass.UNASSIGNED;
            for (Relation relation : pump.getPi().getRelations()) {
                if (relation.getSuperClass().name().toLowerCase().equals(toLetterOnlyLowerCase(sClass))) {
                    aClass = relation.getaClass();
                    break;
                }
            }
            Relation newRelation = new Relation(name, new RelationClassification(aClass));
            newRelation.setExternalId(id);
            pump.getPi().getRelations().add(newRelation);
        }
    }

    @Override
    protected void mineResolutions() {
        Iterable<com.atlassian.jira.rest.client.domain.Resolution> resolutions = new ArrayList<>();
        try {
            resolutions = ((JiraRestClient) pump.getRootObject()).getMetadataClient().getResolutions().get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        for (com.atlassian.jira.rest.client.domain.Resolution jiraResolution : resolutions) {
            boolean found = false;
            for (Resolution resolution : pump.getPi().getResolutions()) {
                if (toLetterOnlyLowerCase(resolution.getName()).equals(toLetterOnlyLowerCase(jiraResolution.getName()))) {
                    resolution.setName(jiraResolution.getName());
                    resolution.setExternalId(jiraResolution.getSelf().toString());
                    resolution.setDescription(jiraResolution.getDescription());
                    found = true;
                    break;
                }
            }
            if (!found) {
                Resolution newResolution = new Resolution(jiraResolution.getName(), new ResolutionClassification(ResolutionClass.UNASSIGNED));
                newResolution.setExternalId(jiraResolution.getSelf().toString());
                newResolution.setDescription(jiraResolution.getDescription());
                pump.getPi().getResolutions().add(newResolution);
            }
        }
    }

    @Override
    protected void mineRoles() {
        Iterable<BasicProjectRole> roles = ((Project) pump.getSecondaryObject()).getProjectRoles();

        for (BasicProjectRole basicRole : roles) {
            boolean found = false;
            for (Role role : pump.getPi().getRoles()) {
                if (toLetterOnlyLowerCase(role.getName()).equals(toLetterOnlyLowerCase(basicRole.getName()))) {
                    role.setName(basicRole.getName());
                    Iterable<ProjectRole> projectRoles = null;
                    try {
                        projectRoles = ((JiraPump) pump).getRootObject().getProjectRolesRestClient().getRoles(basicRole.getSelf()).claim();
                    } catch (Exception e) {
                        App.printLogMsg(this, ROLES_PERMISSION_ERR_MSG);
                    }
                    if (projectRoles != null) {
                        for (ProjectRole projectRole : projectRoles) {
                            App.printLogMsg(this, projectRole.getName());
                            //role.setExternalId(projectRole.getId().toString());
                            //role.setDescription(projectRole.getDescription());
                        }
                    }
                    found = true;
                    break;
                }
            }
            if (!found) {
                Role newRole = new Role(basicRole.getName(), new RoleClassification(RoleClass.UNASSIGNED));
                //newRole.setExternalId(projectRole.getId().toString());
                //newRole.setDescription(projectRole.getDescription());
                pump.getPi().getRoles().add(newRole);
            }
        }
    }

    @Override
    protected void minePriorities() {
        Iterable<com.atlassian.jira.rest.client.domain.Priority> priorities = new ArrayList<>();
        try {
            priorities = ((JiraRestClient) pump.getRootObject()).getMetadataClient().getPriorities().get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        for (com.atlassian.jira.rest.client.domain.Priority issuePriority : priorities) {
            boolean found = false;
            for (Priority priority : pump.getPi().getPriorities()) {
                if (toLetterOnlyLowerCase(priority.getName()).equals(toLetterOnlyLowerCase(issuePriority.getName()))) {
                    priority.setName(issuePriority.getName());
                    if (issuePriority.getId() != null) {
                        priority.setExternalId(issuePriority.getId().toString());
                    }
                    priority.setDescription(String.format(PRIORITY_DESCRIPTION_FORMAT, issuePriority.getDescription(), issuePriority.getStatusColor()));
                    found = true;
                    break;
                }
            }
            if (!found) {
                Priority newPriority = new Priority(issuePriority.getName(), new PriorityClassification(PriorityClass.UNASSIGNED));
                if (issuePriority.getId() != null) {
                    newPriority.setExternalId(issuePriority.getId().toString());
                }
                newPriority.setDescription(String.format(PRIORITY_DESCRIPTION_FORMAT, issuePriority.getDescription(), issuePriority.getStatusColor()));
                pump.getPi().getPriorities().add(newPriority);
            }
        }
    }

    @Override
    protected void mineWUTypes() {
        for (IssueType issueType : ((Project) pump.getSecondaryObject()).getIssueTypes()) {
            boolean found = false;
            for (WorkUnitType type : pump.getPi().getWuTypes()) {
                if (toLetterOnlyLowerCase(type.getName()).equals(toLetterOnlyLowerCase(issueType.getName()))) {
                    type.setName(issueType.getName());
                    if (issueType.getId() != null) {
                        type.setExternalId(issueType.getId().toString());
                    }
                    type.setDescription(issueType.getDescription());
                    found = true;
                    break;
                }
            }
            if (!found) {
                WorkUnitType newType = new WorkUnitType(issueType.getName(), new WorkUnitTypeClassification(WorkUnitTypeClass.UNASSIGNED));
                if (issueType.getId() != null) {
                    newType.setExternalId(issueType.getId().toString());
                }
                newType.setDescription(issueType.getDescription());
                pump.getPi().getWuTypes().add(newType);
            }
        }
    }

    @Override
    protected void mineStatuses() {
        // API doesn't list statuses in project, therefore handled while mining issues (mineItems)
    }

    @Override
    protected void mineSeverities() {
        // API doesn't list custom fields in project, therefore handled while mining issues (mineItems)
    }
}
