package cz.zcu.kiv.spade.pumps.issuetracking.redmine;

import com.taskadapter.redmineapi.bean.Journal;
import com.taskadapter.redmineapi.bean.JournalDetail;
import cz.zcu.kiv.spade.domain.Configuration;
import cz.zcu.kiv.spade.domain.FieldChange;
import cz.zcu.kiv.spade.domain.WorkItemChange;
import cz.zcu.kiv.spade.domain.WorkUnit;
import cz.zcu.kiv.spade.pumps.issuetracking.ChangelogMiner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

class RedmineChangelogMiner extends ChangelogMiner<Journal> {

    RedmineChangelogMiner(RedminePump pump) {
        super(pump);
    }

    @Override
    protected void generateModification(WorkUnit unit, Journal changelog) {
        Configuration configuration = new Configuration();
        configuration.setAuthor(addPerson(((RedminePeopleMiner) pump.getPeopleMiner()).generateIdentity(changelog.getUser())));
        configuration.setCreated(changelog.getCreatedOn());
        configuration.setExternalId(changelog.getId().toString());
        configuration.setDescription(changelog.getNotes());
        WorkItemChange change = new WorkItemChange();
        change.setChangedItem(unit);
        change.setFieldChanges(mineChanges(changelog));
        change.setType(WorkItemChange.Type.MODIFY);
        if (change.getFieldChanges().isEmpty()) {
            change.setType(WorkItemChange.Type.COMMENT);
        }

        pump.getPi().getProject().getConfigurations().add(configuration);
    }

    @Override
    protected Collection<FieldChange> mineChanges(Journal changelog) {
        List<FieldChange> changes = new ArrayList<>();
        for (JournalDetail detail : changelog.getDetails()) {
            FieldChange change = new FieldChange();
            change.setName(detail.getProperty());
            change.setNewValue(detail.getNewValue());
            change.setOldValue(detail.getOldValue());
            changes.add(change);
        }
        return changes;
    }
}
