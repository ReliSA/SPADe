package cz.zcu.kiv.spade.pumps.issuetracking;

import cz.zcu.kiv.spade.domain.*;
import cz.zcu.kiv.spade.domain.enums.*;
import cz.zcu.kiv.spade.pumps.DataMiner;
import cz.zcu.kiv.spade.pumps.DataPump;

public abstract class EnumsMiner extends DataMiner {

    protected static final String ROLES_PERMISSION_ERR_MSG = "Insufficient permissions for roles";
    protected static final String RESOLUTION_FIELD_NAME = "resolution";
    private static final String DEFAULT_ROLE_NAME = "member";
    private static final String DEFAULT_ENUM_NAME = "unassigned";
    private static final String DELETED_STATUS_NAME = "deleted";

    protected EnumsMiner(DataPump pump) {
        super(pump);
    }

    /**
     * mines all the enumeration values used in the project
     */
    void mineEnums() {
        mineRoles();
        mineCategories();
        mineWUTypes();
        minePriorities();
        mineResolutions();
        mineWURelationTypes();
        mineStatuses();
        mineSeverities();
    }

    /**
     * mines all the priority values used in the project
     */
    protected abstract void minePriorities();

    /**
     * mines all the issue type values used in the project
     */
    protected abstract void mineWUTypes();

    protected abstract void mineResolutions();

    protected abstract void mineWURelationTypes();

    /**
     * mines all the status values used in the project
     */
    protected abstract void mineStatuses();

    /**
     * mines all the severity values used in the project (if there are any)
     */
    protected abstract void mineSeverities();

    /**
     * mines custom issue categories (components, tags, labels) in project instance
     */
    protected abstract void mineCategories();

    /**
     * mines all the roles used in the project
     */
    protected abstract void mineRoles();

    /**
     * assigns default sets of enumeration values (priorities, severities, statuses, resolutions and Work Unit types) to the Project Instance
     */
    void assignDefaultEnums() {
        for (WorkUnit unit : pump.getPi().getProject().getUnits()) {
            assignDefaultPriority(unit);
            assignDefaultSeverity(unit);
            assignDefaultStatus(unit);
            assignDefaultResolution(unit);
            assignDefaultWuType(unit);
        }
        for (Person person : pump.getPi().getProject().getPeople()) {
            assignDefaultRole(person);
        }
    }

    private void assignDefaultRole(Person person) {
        if (person.getRoles().isEmpty()) {
            for (Role role : pump.getPi().getRoles()) {
                if (role.getName().equals(DEFAULT_ROLE_NAME)) {
                    person.getRoles().add(role);
                    return;
                }
            }
            Role defaultRole = new Role(DEFAULT_ROLE_NAME, new RoleClassification(RoleClass.TEAMMEMBER));
            pump.getPi().getRoles().add(defaultRole);
            person.getRoles().add(defaultRole);
        }
    }

    /**
     * assigns a default priority ("unassigned") to the given Work Unit instance;
     * creates and adds this priority to the Project if necessary
     *
     * @param unit given Work Unit instance
     */
    private void assignDefaultPriority(WorkUnit unit) {
        if (unit.getPriority() == null) {
            for (Priority priority : pump.getPi().getPriorities()) {
                if (priority.getName().equals(DEFAULT_ENUM_NAME)) {
                    unit.setPriority(priority);
                    return;
                }
            }
            Priority defaultPriority = new Priority(DEFAULT_ENUM_NAME, new PriorityClassification(PriorityClass.UNASSIGNED));
            pump.getPi().getPriorities().add(defaultPriority);
            unit.setPriority(defaultPriority);
        }
    }

    /**
     * assigns a default severity ("unassigned") to the given Work Unit instance;
     * creates and adds this severity to the Project if necessary
     *
     * @param unit given Work Unit instance
     */
    private void assignDefaultSeverity(WorkUnit unit) {
        if (unit.getSeverity() == null) {
            for (Severity severity : pump.getPi().getSeverities()) {
                if (severity.getName().equals(DEFAULT_ENUM_NAME)) {
                    unit.setSeverity(severity);
                    return;
                }
            }
            Severity defaultSeverity = new Severity(DEFAULT_ENUM_NAME, new SeverityClassification(SeverityClass.UNASSIGNED));
            pump.getPi().getSeverities().add(defaultSeverity);
            unit.setSeverity(defaultSeverity);
        }
    }

    /**
     * assigns a default status ("unassigned") to the given Work Unit instance;
     * creates and adds this status to the Project if necessary
     *
     * @param unit given Work Unit instance
     */
    private void assignDefaultStatus(WorkUnit unit) {
        if (unit.getStatus() == null) {
            for (Status status : pump.getPi().getStatuses()) {
                if (status.getName().equals(DEFAULT_ENUM_NAME)) {
                    unit.setStatus(status);
                    return;
                }
            }
            Status defaultStatus = new Status(DEFAULT_ENUM_NAME, new StatusClassification(StatusClass.UNASSIGNED));
            pump.getPi().getStatuses().add(defaultStatus);
            unit.setStatus(defaultStatus);
        }
    }

    /**
     * assigns a resolution to the given Work Unit instance - "invalid" if the Status class is INVALID,
     * default ("unassigned") otherwise if Status class is CLOSED;
     * creates and adds the default resolution to the Project if necessary
     *
     * @param unit given Work Unit instance
     */
    private void assignDefaultResolution(WorkUnit unit) {
        if (unit.getStatus().getClassification().getaClass().equals(StatusClass.INVALID)) {
            for (Resolution resolution : pump.getPi().getResolutions()) {
                if (resolution.getClassification().getaClass().equals(ResolutionClass.INVALID)) {
                    unit.setResolution(resolution);
                    return;
                }
            }
        }
        if (unit.getStatus().getClassification().getSuperClass().equals(StatusSuperClass.OPEN)) {
            return;
        }
        if (unit.getResolution() == null) {
            for (Resolution resolution : pump.getPi().getResolutions()) {
                if (resolution.getName().equals(DEFAULT_ENUM_NAME)) {
                    unit.setResolution(resolution);
                    return;
                }
            }
            Resolution defaultResolution = new Resolution(DEFAULT_ENUM_NAME, new ResolutionClassification(ResolutionClass.UNASSIGNED));
            pump.getPi().getResolutions().add(defaultResolution);
            unit.setResolution(defaultResolution);
        }
    }

    /**
     * assigns a default Work Unit type ("unassigned") to the given Work Unit instance;
     * creates and adds this status to the Project if necessary
     *
     * @param unit given Work Unit instance
     */
    private void assignDefaultWuType(WorkUnit unit) {
        if (unit.getType() == null) {
            for (WorkUnitType type : pump.getPi().getWuTypes()) {
                if (type.getName().equals(DEFAULT_ENUM_NAME)) {
                    unit.setType(type);
                    return;
                }
            }
            WorkUnitType defaultType = new WorkUnitType(DEFAULT_ENUM_NAME, new WorkUnitTypeClassification(WorkUnitTypeClass.UNASSIGNED));
            pump.getPi().getWuTypes().add(defaultType);
            unit.setType(defaultType);
        }
    }

    /**
     * adds a new Status ("deleted") to the Work Units which have apparently been deleted from the project
     * (a mention of them exist, but URL can't be found); if at least one such unit exists,
     * it adds the new status to the Project instance as well
     */
    void addDeletedStatus() {
        Status delStatus = new Status(DELETED_STATUS_NAME, new StatusClassification(StatusClass.DELETED));
        boolean add = true;
        for (WorkUnit unit : pump.getPi().getProject().getUnits()) {
            if (unit.getUrl().isEmpty()) {
                if (add) {
                    pump.getPi().getStatuses().add(delStatus);
                    add = false;
                }
                unit.setStatus(delStatus);
            }
        }
    }
}
