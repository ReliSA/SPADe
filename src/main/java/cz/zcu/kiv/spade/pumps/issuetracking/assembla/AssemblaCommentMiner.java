package cz.zcu.kiv.spade.pumps.issuetracking.assembla;

import com.assembla.TicketComment;
import cz.zcu.kiv.spade.App;
import cz.zcu.kiv.spade.domain.Configuration;
import cz.zcu.kiv.spade.domain.WorkItemChange;
import cz.zcu.kiv.spade.domain.WorkUnit;
import cz.zcu.kiv.spade.pumps.issuetracking.CommentMiner;

class AssemblaCommentMiner extends CommentMiner<TicketComment> {

    AssemblaCommentMiner(AssemblaPump pump) {
        super(pump);
    }

    @Override
    protected void generateUnitCommentConfig(WorkUnit unit, TicketComment comment) {
        Configuration configuration = new Configuration();
        configuration.setExternalId(comment.getId().toString());
        configuration.setDescription(comment.getComment().trim());
        configuration.setAuthor(addPerson(((AssemblaPeopleMiner) pump.getPeopleMiner()).generateIdentity(comment.getUserId())));
        configuration.setCreated(((AssemblaPump) pump).convertDate(comment.getCreatedOn()));

        // TODO parse ticket changes
        App.printLogMsg(comment.getTicketChanges(), false);

        WorkItemChange change = generateCommentChange(unit);
        if (!change.getFieldChanges().isEmpty()) {
            change.setType(WorkItemChange.Type.MODIFY);
        }
        configuration.getChanges().add(change);

        pump.getPi().getProject().getConfigurations().add(configuration);
    }
}
