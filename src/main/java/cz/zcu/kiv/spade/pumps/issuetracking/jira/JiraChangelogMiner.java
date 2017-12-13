package cz.zcu.kiv.spade.pumps.issuetracking.jira;

import com.atlassian.jira.rest.client.domain.ChangelogGroup;
import com.atlassian.jira.rest.client.domain.ChangelogItem;
import cz.zcu.kiv.spade.App;
import cz.zcu.kiv.spade.domain.Configuration;
import cz.zcu.kiv.spade.domain.FieldChange;
import cz.zcu.kiv.spade.domain.WorkItemChange;
import cz.zcu.kiv.spade.domain.WorkUnit;
import cz.zcu.kiv.spade.pumps.issuetracking.ChangelogMiner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

class JiraChangelogMiner extends ChangelogMiner<ChangelogGroup> {

    private static final String HISTORY_FORMAT = "history: %s: %s -> %s";

    JiraChangelogMiner(JiraPump pump) {
        super(pump);
    }

    @Override
    protected void generateModification(WorkUnit unit, ChangelogGroup changelog) {
        Configuration configuration = new Configuration();
        configuration.setAuthor(addPerson(((JiraPeopleMiner) pump.getPeopleMiner()).generateIdentity(changelog.getAuthor())));
        configuration.setCreated(changelog.getCreated().toDate());
        WorkItemChange change = new WorkItemChange();
        change.setChangedItem(unit);
        change.setType(WorkItemChange.Type.MODIFY);
        change.setFieldChanges(mineChanges(changelog));

        pump.getPi().getProject().getConfigurations().add(configuration);
    }

    @Override
    protected Collection<FieldChange> mineChanges(ChangelogGroup changelog) {
        List<FieldChange> changes = new ArrayList<>();
        for (ChangelogItem item : changelog.getItems()) {
            FieldChange change = new FieldChange();
            change.setName(item.getField());
            change.setNewValue(item.getToString());
            change.setOldValue(item.getFromString());
            App.printLogMsg(String.format(HISTORY_FORMAT, change.getName(), change.getOldValue(), change.getNewValue()), false);
            changes.add(change);
        }
        return changes;
    }
}
