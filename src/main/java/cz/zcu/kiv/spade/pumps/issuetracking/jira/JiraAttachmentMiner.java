package cz.zcu.kiv.spade.pumps.issuetracking.jira;

import com.atlassian.jira.rest.client.domain.Attachment;
import com.atlassian.jira.rest.client.domain.ChangelogGroup;
import com.atlassian.jira.rest.client.domain.ChangelogItem;
import cz.zcu.kiv.spade.domain.*;
import cz.zcu.kiv.spade.domain.enums.ArtifactClass;
import cz.zcu.kiv.spade.pumps.DataPump;
import cz.zcu.kiv.spade.pumps.issuetracking.AttachmentMiner;

import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

class JiraAttachmentMiner extends AttachmentMiner<Attachment> {

    JiraAttachmentMiner(JiraPump pump) {
        super(pump);
    }

    @Override
    protected void mineAttachments(WorkItem item, Iterable<Attachment> attachments) {}

    void mineAttachments(WorkItem item, ChangelogGroup changelog) {
        List<WorkItemChange> changes = new ArrayList<>();
        Person author = addPerson(((JiraPeopleMiner) pump.getPeopleMiner()).generateIdentity(changelog.getAuthor()));
        Date created = changelog.getCreated().toDate();

        for (ChangelogItem entry : changelog.getItems()) {
            if (!entry.getField().equals(JiraPump.ATTACHMENT_FIELD_NAME)) continue;
            Artifact artifact = new Artifact();
            artifact.setArtifactClass(ArtifactClass.FILE);
            artifact.setName(entry.getToString());
            artifact.setMimeType(URLConnection.guessContentTypeFromName(artifact.getName()));
            if (artifact.getMimeType() == null) {
                artifact.setMimeType(artifact.getName().substring(artifact.getName().lastIndexOf(DataPump.DOT) + 1));
            }
            artifact.setAuthor(author);
            artifact.setCreated(created);

            item.getRelatedItems().add(new WorkItemRelation(artifact, resolveRelation(HAS_ATTACHED)));
            artifact.getRelatedItems().add(new WorkItemRelation(item, resolveRelation(ATTACHED_TO)));

            WorkItemChange attachChange = new WorkItemChange();
            attachChange.setChangedItem(item);
            attachChange.setType(WorkItemChange.Type.MODIFY);
            attachChange.setDescription(ATTACH_CHANGE_NAME);
            changes.add(attachChange);

            WorkItemChange createAttachmentChange = new WorkItemChange();
            createAttachmentChange.setChangedItem(artifact);
            createAttachmentChange.setType(WorkItemChange.Type.ADD);
            changes.add(createAttachmentChange);
        }
        if (!changes.isEmpty()) {
            Configuration configuration = new Configuration();
            configuration.setCreated(created);
            configuration.setAuthor(author);
            configuration.getChanges().addAll(changes);
            pump.getPi().getProject().getConfigurations().add(configuration);
        }
    }
}
