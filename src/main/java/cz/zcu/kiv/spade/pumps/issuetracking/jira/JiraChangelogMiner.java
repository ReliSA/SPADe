package cz.zcu.kiv.spade.pumps.issuetracking.jira;

import com.atlassian.jira.rest.client.domain.ChangelogGroup;
import com.atlassian.jira.rest.client.domain.ChangelogItem;
import cz.zcu.kiv.spade.domain.*;
import cz.zcu.kiv.spade.pumps.issuetracking.ChangelogMiner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

class JiraChangelogMiner extends ChangelogMiner<ChangelogGroup> {

    private JiraAttachmentMiner attachmentMiner;

    JiraChangelogMiner(JiraPump pump) {
        super(pump);
        attachmentMiner = new JiraAttachmentMiner(pump);
    }

    @Override
    protected void generateModification(WorkUnit unit, ChangelogGroup changelog) {
        attachmentMiner.mineAttachments(unit, changelog);
        Collection<FieldChange> fChanges = mineChanges(changelog);

        if (!fChanges.isEmpty()) {
            Configuration configuration = new Configuration();
            configuration.setAuthor(addPerson(((JiraPeopleMiner) pump.getPeopleMiner()).generateIdentity(changelog.getAuthor())));
            configuration.setCreated(changelog.getCreated().toDate());
            WorkItemChange change = new WorkItemChange();
            change.setChangedItem(unit);
            change.setType(WorkItemChange.Type.MODIFY);
            change.setFieldChanges(fChanges);

            pump.getPi().getProject().getConfigurations().add(configuration);
        }
    }

    @Override
    protected Collection<FieldChange> mineChanges(ChangelogGroup changelog) {
        List<FieldChange> changes = new ArrayList<>();
        for (ChangelogItem item : changelog.getItems()) {
            if (item.getField().equals(JiraPump.ATTACHMENT_FIELD_NAME)) continue;
            FieldChange change = new FieldChange();
            change.setName(item.getField());
            change.setNewValue(item.getToString());
            change.setOldValue(item.getFromString());
            changes.add(change);
        }
        return changes;
    }
}
