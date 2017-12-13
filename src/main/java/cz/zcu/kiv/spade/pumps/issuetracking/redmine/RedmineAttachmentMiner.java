package cz.zcu.kiv.spade.pumps.issuetracking.redmine;

import com.taskadapter.redmineapi.bean.Attachment;
import cz.zcu.kiv.spade.domain.Artifact;
import cz.zcu.kiv.spade.domain.WorkItem;
import cz.zcu.kiv.spade.domain.enums.ArtifactClass;
import cz.zcu.kiv.spade.pumps.issuetracking.AttachmentMiner;

class RedmineAttachmentMiner extends AttachmentMiner<Attachment> {

    RedmineAttachmentMiner(RedminePump pump) {
        super(pump);
    }

    @Override
    protected void mineAttachments(WorkItem item, Iterable<Attachment> attachments) {
        for (Attachment attachment : attachments) {
            Artifact artifact = new Artifact();
            artifact.setArtifactClass(ArtifactClass.FILE);
            artifact.setMimeType(attachment.getContentType());
            artifact.setUrl(attachment.getContentURL());
            artifact.setAuthor(addPerson(((RedminePeopleMiner) pump.getPeopleMiner()).generateIdentity(attachment.getAuthor())));
            artifact.setCreated(attachment.getCreatedOn());
            artifact.setDescription(attachment.getDescription());
            artifact.setExternalId(attachment.getId().toString());
            artifact.setName(attachment.getFileName());
            artifact.setSize(attachment.getFileSize());

            generateAttachmentConfig(item, artifact);
        }
    }
}
