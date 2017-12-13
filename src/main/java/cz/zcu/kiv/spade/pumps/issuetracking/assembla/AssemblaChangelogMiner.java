package cz.zcu.kiv.spade.pumps.issuetracking.assembla;

import com.assembla.Event;
import cz.zcu.kiv.spade.App;
import cz.zcu.kiv.spade.domain.Configuration;
import cz.zcu.kiv.spade.domain.FieldChange;
import cz.zcu.kiv.spade.domain.WorkItemChange;
import cz.zcu.kiv.spade.domain.WorkUnit;
import cz.zcu.kiv.spade.pumps.issuetracking.ChangelogMiner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

class AssemblaChangelogMiner extends ChangelogMiner<Event> {

    AssemblaChangelogMiner(AssemblaPump pump) {
        super(pump);
    }

    @Override
    protected void generateModification(WorkUnit unit, Event changelog) {
        Configuration configuration = new Configuration();
        configuration.setAuthor(addPerson(((AssemblaPeopleMiner) pump.getPeopleMiner()).generateIdentity(changelog.getAuthorId())));
        configuration.setCreated(((AssemblaPump) pump).convertDate(changelog.getDate()));
        configuration.setDescription(changelog.getCommentOrDescription());

        WorkItemChange change = new WorkItemChange();
        change.setChangedItem(unit);
        change.setType(WorkItemChange.Type.MODIFY);
        change.setFieldChanges(mineChanges(changelog));

        pump.getPi().getProject().getConfigurations().add(configuration);
    }

    @Override
    protected Collection<FieldChange> mineChanges(Event changelog) {
        List<FieldChange> changes = new ArrayList<>();
        FieldChange change = new FieldChange();

        // TODO check
        change.setName(
            changelog.getOperation() + "\n" +
            changelog.getTicketOperation() + "\n" +
            changelog.getTitle() + "\n" +
            changelog.getWhatchanged()
        );
        App.printLogMsg(change.getName(), false);

        changes.add(change);
        return changes;
    }
}
