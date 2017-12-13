package cz.zcu.kiv.spade.pumps.issuetracking.assembla;

import com.assembla.Ticket;
import com.assembla.TicketAssociation;
import com.assembla.client.AssemblaAPI;
import cz.zcu.kiv.spade.domain.*;
import cz.zcu.kiv.spade.pumps.issuetracking.IssueTrackingRelationMiner;

class AssemblaRelationMiner extends IssueTrackingRelationMiner {

    AssemblaRelationMiner(AssemblaPump pump) {
        super(pump);
    }

    @Override
    protected void mineMentions() {
        // from work unit descriptions
        for (WorkUnit unit : pump.getPi().getProject().getUnits()) {
            mineAllMentionedItems(unit);
        }

        for (Configuration configuration : pump.getPi().getProject().getConfigurations()) {
            if (!(configuration instanceof Commit)) {
                for (WorkItemChange change : configuration.getChanges()) {
                    // from work unit comments and time logs
                    if (change.getChangedItem() instanceof WorkUnit &&
                            (change.getType().equals(WorkItemChange.Type.COMMENT) ||
                                    change.getType().equals(WorkItemChange.Type.LOGTIME))) {
                        mineAllMentionedItems(change.getChangedItem(), configuration.getDescription());
                    }
                }
            }
        }
    }

    @Override
    protected void mineRelations() {
        for (WorkUnit unit : pump.getPi().getProject().getUnits()) {
            Ticket ticket = ((AssemblaAPI) pump.getRootObject()).tickets(pump.getPi().getExternalId()).getById(unit.getExternalId());
            for (TicketAssociation association : ((AssemblaAPI) pump.getRootObject()).ticketAssociations(pump.getPi().getExternalId()).get(ticket)) {
                Integer relatedId;
                if (ticket.getId().equals(association.ticket1Id)) {
                    relatedId = association.getTicket2Id();
                } else {
                    relatedId = association.getTicket1Id();
                }

                WorkUnit related = pump.getPi().getProject().getUnit(relatedId);

                unit.getRelatedItems().add(new WorkItemRelation(related, resolveRelation(association.getRelationship().name())));
            }
        }
    }
}
