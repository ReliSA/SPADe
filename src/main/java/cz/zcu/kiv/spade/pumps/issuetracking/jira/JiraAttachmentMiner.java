package cz.zcu.kiv.spade.pumps.issuetracking.jira;

import com.atlassian.jira.rest.client.domain.Attachment;
import cz.zcu.kiv.spade.domain.Artifact;
import cz.zcu.kiv.spade.domain.WorkItem;
import cz.zcu.kiv.spade.domain.enums.ArtifactClass;
import cz.zcu.kiv.spade.pumps.issuetracking.AttachmentMiner;

class JiraAttachmentMiner extends AttachmentMiner<Attachment> {

    JiraAttachmentMiner(JiraPump pump) {
        super(pump);
    }

    @Override
    protected void mineAttachments(WorkItem item, Iterable<Attachment> attachments) {
        for (Attachment attachment : attachments) {
            Artifact artifact = new Artifact();
            artifact.setArtifactClass(ArtifactClass.FILE);
            artifact.setMimeType(attachment.getMimeType());
            artifact.setUrl(attachment.getContentUri().toString());
            artifact.setAuthor(addPerson(((JiraPeopleMiner) pump.getPeopleMiner()).generateIdentity(attachment.getAuthor())));
            artifact.setCreated(attachment.getCreationDate().toDate());
            artifact.setExternalId(attachment.getSelf().toString());
            artifact.setName(attachment.getFilename());
            artifact.setSize(attachment.getSize());

            generateAttachmentConfig(item, artifact);
        }
    }
}
