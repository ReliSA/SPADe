package cz.zcu.kiv.spade.pumps.issuetracking.bugzilla;

import b4j.core.Attachment;
import cz.zcu.kiv.spade.domain.Artifact;
import cz.zcu.kiv.spade.domain.WorkItem;
import cz.zcu.kiv.spade.domain.enums.ArtifactClass;
import cz.zcu.kiv.spade.pumps.issuetracking.AttachmentMiner;

class BugzillaAttachmentMiner extends AttachmentMiner<Attachment> {

    BugzillaAttachmentMiner(BugzillaPump pump) {
        super(pump);
    }

    @Override
    protected void mineAttachments(WorkItem item, Iterable<Attachment> attachments) {
        for (Attachment attachment : attachments) {
            Artifact artifact = new Artifact();
            artifact.setArtifactClass(ArtifactClass.FILE);
            artifact.setMimeType(attachment.getType());
            artifact.setUrl(attachment.getUri().toString());
            artifact.setAuthor(item.getAuthor());
            artifact.setCreated(attachment.getDate());
            artifact.setExternalId(attachment.getId());
            artifact.setName(attachment.getFilename());
            artifact.setDescription(attachment.getDescription());

            generateAttachmentConfig(item, artifact);
        }

    }
}
