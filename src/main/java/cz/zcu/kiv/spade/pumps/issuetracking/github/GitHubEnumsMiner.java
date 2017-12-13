package cz.zcu.kiv.spade.pumps.issuetracking.github;

import cz.zcu.kiv.spade.domain.*;
import cz.zcu.kiv.spade.domain.enums.*;
import cz.zcu.kiv.spade.pumps.issuetracking.EnumsMiner;
import org.kohsuke.github.GHIssueState;
import org.kohsuke.github.GHLabel;
import org.kohsuke.github.GHRepository;

import java.io.IOException;
import java.util.List;

class GitHubEnumsMiner extends EnumsMiner {

    private static final String PRIORITY_FIELD_NAME = "priority";

    GitHubEnumsMiner(GitHubPump pump) {
        super(pump);
    }

    @Override
    protected void mineCategories() {
        List<GHLabel> labels;
        while (true) {
            try {
                labels = ((GHRepository) pump.getRootObject()).listLabels().asList();
                break;
            } catch (IOException e) {
                ((GitHubPump) pump).resetRootObject();
            }
        }

        for (GHLabel label : labels) {
            if (isWUType(label)) continue;
            if (isResolution(label)) continue;
            if (isPriority(label)) continue;
            if (!isSeverity(label)) {
                Category category = new Category();
                category.setExternalId(label.getUrl());
                category.setName(label.getName());
                category.setDescription(label.getColor());
                pump.getPi().getCategories().add(category);
            }
        }
    }

    @Override
    protected void mineRoles() {
        for (GitHubPump.GitHubRole role : GitHubPump.GitHubRole.values()) {
            if (resolveRole(role.name()) == null) {
                if (role == GitHubPump.GitHubRole.owner) {
                    pump.getPi().getRoles().add(new Role(role.name(), roleDao.findByClass(RoleClass.PROJECTMANAGER)));
                } else {
                    pump.getPi().getRoles().add(new Role(role.name(), roleDao.findByClass(RoleClass.TEAMMEMBER)));
                }
            }
        }
    }

    @Override
    protected void mineStatuses() {
        for (GHIssueState state : GHIssueState.values()) {
            if (state == GHIssueState.ALL) continue;

            String name = toLetterOnlyLowerCase(state.name());

            boolean found = false;
            for (Status status : pump.getPi().getStatuses()) {
                if (name.equals(toLetterOnlyLowerCase(status.getName()))) {
                    status.setName(state.name());
                    found = true;
                    break;
                }
            }
            if (!found) {
                Status newStatus = new Status(state.name(), statusDao.findByClass(StatusClass.UNASSIGNED));
                pump.getPi().getStatuses().add(newStatus);
            }
        }
    }


    @Override
    protected void mineSeverities() {
        //handled in mineCategories
    }

    @Override
    protected void mineResolutions() {
        //handled in mineCategories
    }

    @Override
    protected void minePriorities() {
        //handled in mineCategories
    }

    @Override
    protected void mineWUTypes() {
        //handled in mineCategories
    }

    @Override
    protected void mineWURelationTypes() {
        //GitHub does not have ane issue relations
    }

    /**
     * checks if GitHub label name corresponds with any severity value used in the project
     *
     * @param label label to check
     * @return true if label name is severity, else false
     */
    private boolean isSeverity(GHLabel label) {
        String name = toLetterOnlyLowerCase(label.getName());
        boolean createIfNotFound = false;
        if (name.contains(SEVERITY_FIELD_NAME)) {
            name = name.replace(SEVERITY_FIELD_NAME, "");
            createIfNotFound = true;
        }
        for (Severity severity : pump.getPi().getSeverities()) {
            if (name.equals(toLetterOnlyLowerCase(severity.getName()))) {
                severity.setExternalId(label.getUrl());
                severity.setDescription(label.getColor());
                severity.setName(label.getName());
                return true;
            }
        }
        if (createIfNotFound) {
            Severity newSeverity = new Severity(label.getName(), severityDao.findByClass(SeverityClass.UNASSIGNED));
            newSeverity.setExternalId(label.getUrl());
            newSeverity.setDescription(label.getColor());
            pump.getPi().getSeverities().add(newSeverity);
            return true;
        }
        return false;
    }

    /**
     * checks if GitHub label name corresponds with any priority value used in the project
     *
     * @param label label to check
     * @return true if label name is priority, else false
     */
    private boolean isPriority(GHLabel label) {
        String name = toLetterOnlyLowerCase(label.getName());
        boolean createIfNotFound = false;
        if (name.contains(PRIORITY_FIELD_NAME)) {
            name = name.replace(PRIORITY_FIELD_NAME, "");
            createIfNotFound = true;
        }
        for (Priority priority : pump.getPi().getPriorities()) {
            if (name.equals(toLetterOnlyLowerCase(priority.getName()))) {
                priority.setExternalId(label.getUrl());
                priority.setDescription(label.getColor());
                priority.setName(label.getName());
                return true;
            }
        }
        if (createIfNotFound) {
            Priority newPriority = new Priority(label.getName(), priorityDao.findByClass(PriorityClass.UNASSIGNED));
            newPriority.setExternalId(label.getUrl());
            newPriority.setDescription(label.getColor());
            pump.getPi().getPriorities().add(newPriority);
            return true;
        }
        return false;
    }

    /**
     * checks if GitHub label name corresponds with any resolution value used in the project
     *
     * @param label label to check
     * @return true if label name is resolution, else false
     */
    private boolean isResolution(GHLabel label) {
        String name = toLetterOnlyLowerCase(label.getName());
        boolean createIfNotFound = false;
        if (name.contains(RESOLUTION_FIELD_NAME)) {
            name = name.replace(RESOLUTION_FIELD_NAME, "");
            createIfNotFound = true;
        }
        for (Resolution resolution : pump.getPi().getResolutions()) {
            if (name.equals(toLetterOnlyLowerCase(resolution.getName()))) {
                resolution.setExternalId(label.getUrl());
                resolution.setDescription(label.getColor());
                resolution.setName(label.getName());
                return true;
            }
        }
        if (createIfNotFound) {
            Resolution newResolution = new Resolution(label.getName(), resolutionDao.findByClass(ResolutionClass.UNASSIGNED));
            newResolution.setExternalId(label.getUrl());
            newResolution.setDescription(label.getColor());
            pump.getPi().getResolutions().add(newResolution);
            return true;
        }
        return false;
    }

    /**
     * checks if GitHub label name corresponds with any Work Unit type value used in the project
     *
     * @param label label to check
     * @return true if label name is Work Unit type, else false
     */
    private boolean isWUType(GHLabel label) {
        String name = toLetterOnlyLowerCase(label.getName());
        for (WorkUnitType type : pump.getPi().getWuTypes()) {
            if (name.equals(toLetterOnlyLowerCase(type.getName()))) {
                type.setExternalId(label.getUrl());
                type.setDescription(label.getColor());
                type.setName(label.getName());
                return true;
            }
        }
        return false;
    }
}
