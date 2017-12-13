package cz.zcu.kiv.spade.pumps.issuetracking.assembla;

import com.assembla.*;
import com.assembla.client.AssemblaAPI;
import cz.zcu.kiv.spade.domain.*;
import cz.zcu.kiv.spade.domain.enums.*;
import cz.zcu.kiv.spade.pumps.issuetracking.EnumsMiner;

class AssemblaEnumsMiner extends EnumsMiner {

    AssemblaEnumsMiner(AssemblaPump pump) {
        super(pump);
    }

    @Override
    protected void minePriorities() {
        for (Ticket.Priority ticketPriority : Ticket.Priority.values()) {
            boolean found = false;
            for (Priority priority : pump.getPi().getPriorities()) {
                if (toLetterOnlyLowerCase(priority.getName()).equals(toLetterOnlyLowerCase(ticketPriority.name()))) {
                    priority.setName(ticketPriority.name());
                    priority.setExternalId(ticketPriority.getValue().toString());
                    found = true;
                    break;
                }
            }
            if (!found) {
                PriorityClass priorityClass;
                switch (ticketPriority) {
                    case HIGHEST:
                        priorityClass = PriorityClass.HIGHEST;
                        break;
                    case HIGH:
                        priorityClass = PriorityClass.HIGH;
                        break;
                    case NORMAL:
                        priorityClass = PriorityClass.NORMAL;
                        break;
                    case LOW:
                        priorityClass = PriorityClass.LOW;
                        break;
                    case LOWEST:
                        priorityClass = PriorityClass.LOWEST;
                        break;
                    default:
                        priorityClass = PriorityClass.UNASSIGNED;
                        break;
                }
                Priority newPriority = new Priority(ticketPriority.name(), priorityDao.findByClass(priorityClass));
                newPriority.setExternalId(ticketPriority.getValue().toString());
                pump.getPi().getPriorities().add(newPriority);
            }
        }
    }

    @Override
    protected void mineWUTypes() {
        for (Ticket.HeirarchyType ticketType : Ticket.HeirarchyType.values()) {
            boolean found = false;
            for (WorkUnitType type : pump.getPi().getWuTypes()) {
                if (toLetterOnlyLowerCase(type.getName()).equals(toLetterOnlyLowerCase(ticketType.name()))) {
                    type.setName(ticketType.name());
                    type.setExternalId(ticketType.getValue().toString());
                    found = true;
                    break;
                }
            }
            if (!found) {
                WorkUnitTypeClass typeClass;
                switch (ticketType) {
                    case EPIC:
                        typeClass = WorkUnitTypeClass.FEATURE;
                        break;
                    case STORY:
                    case SUB_TASK:
                        typeClass = WorkUnitTypeClass.TASK;
                        break;
                    case NO_PLAN:
                    default:
                        typeClass = WorkUnitTypeClass.UNASSIGNED;
                        break;
                }
                WorkUnitType newType = new WorkUnitType(ticketType.name(), typeDao.findByClass(typeClass));
                newType.setExternalId(ticketType.getValue().toString());
                pump.getPi().getWuTypes().add(newType);
            }
        }

    }

    @Override
    protected void mineResolutions() {
        // handled through custom fields of which one cannot get values here, therefore handled in issue mining (mineItems)
    }

    @Override
    protected void mineWURelationTypes() {
        for (TicketAssociation.TicketRelationship ticketRelationship : TicketAssociation.TicketRelationship.values()) {
            boolean found = false;
            for (Relation relation : pump.getPi().getRelations()) {
                if (toLetterOnlyLowerCase(relation.getName()).equals(toLetterOnlyLowerCase(ticketRelationship.name()))) {
                    relation.setName(ticketRelationship.name());
                    relation.setExternalId(ticketRelationship.getValue().toString());
                    found = true;
                    break;
                }
            }
            if (!found) {
                RelationClass relationClass;
                switch (ticketRelationship) {
                    case BLOCK:
                        relationClass = RelationClass.BLOCKS;
                        break;
                    case DEPENDANT:
                        relationClass = RelationClass.BLOCKEDBY;
                        break;
                    case PARENT:
                    case STORY:
                        relationClass = RelationClass.PARENTOF;
                        break;
                    case SUBTASK:
                    case CHILD:
                        relationClass = RelationClass.CHILDOF;
                        break;
                    case RELATED:
                    case SIBLING:
                        relationClass = RelationClass.RELATESTO;
                        break;
                    case DUPLICATE:
                        relationClass = RelationClass.DUPLICATES;
                        break;
                    default:
                        relationClass = RelationClass.UNASSIGNED;
                        break;
                }
                Relation newRelation = new Relation(ticketRelationship.name(), relationDao.findByClass(relationClass));
                newRelation.setExternalId(ticketRelationship.getValue().toString());
                pump.getPi().getRelations().add(newRelation);
            }
        }
    }

    @Override
    protected void mineStatuses() {
        for (TicketStatus ticketStatus : ((AssemblaAPI) pump.getRootObject()).ticketStatuses(pump.getPi().getExternalId()).getAll()) {
            boolean found = false;
            for (Status status : pump.getPi().getStatuses()) {
                if (toLetterOnlyLowerCase(status.getName()).equals(toLetterOnlyLowerCase(ticketStatus.getName()))) {
                    status.setName(ticketStatus.getName());
                    status.setExternalId(ticketStatus.getId().toString());
                    found = true;
                    break;
                }
            }
            if (!found) {
                StatusClass statusClass;
                switch (ticketStatus.getState()) {
                    case OPEN:
                        statusClass = StatusClass.OPEN;
                        break;
                    case CLOSED:
                        statusClass = StatusClass.CLOSED;
                        break;
                    default:
                        statusClass = StatusClass.UNASSIGNED;
                        break;
                }
                Status newStatus = new Status(ticketStatus.getName(), statusDao.findByClass(statusClass));
                newStatus.setExternalId(ticketStatus.getId().toString());
                pump.getPi().getStatuses().add(newStatus);
            }
        }
    }

    @Override
    protected void mineSeverities() {
        // handled through custom fields of which one cannot get values here, therefore handled in issue mining (mineItems)
    }

    @Override
    protected void mineCategories() {
        for (Tag tag : ((AssemblaAPI) pump.getRootObject()).tags(pump.getPi().getExternalId()).getAll().asList()) {
            Category category = new Category();
            category.setExternalId(tag.getId().toString());
            category.setName(tag.getName());
            category.setDescription(tag.getState().name());
            pump.getPi().getCategories().add(category);
        }
    }

    @Override
    protected void mineRoles() {
        for (UserRole userRole : ((AssemblaAPI) pump.getRootObject()).roles(pump.getPi().getExternalId()).getAll()) {
            boolean found = false;
            for (Role role : pump.getPi().getRoles()) {
                if (toLetterOnlyLowerCase(role.getName()).equals(toLetterOnlyLowerCase(userRole.getTitle()))) {
                    role.setName(userRole.getTitle());
                    role.setExternalId(userRole.getId().toString());
                    found = true;
                    break;
                }
                // TODO review
                if (toLetterOnlyLowerCase(role.getName()).equals(toLetterOnlyLowerCase(userRole.getRole()))) {
                    role.setName(userRole.getTitle());
                    role.setExternalId(userRole.getId().toString());
                    found = true;
                    break;
                }
            }
            if (!found) {
                Role newRole = new Role(userRole.getTitle(), roleDao.findByClass(RoleClass.UNASSIGNED));
                newRole.setExternalId(userRole.getId().toString());
                pump.getPi().getRoles().add(newRole);
            }
        }
    }
}
