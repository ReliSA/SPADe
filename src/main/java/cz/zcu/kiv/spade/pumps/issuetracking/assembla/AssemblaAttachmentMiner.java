package cz.zcu.kiv.spade.pumps.issuetracking.assembla;

import com.assembla.Document;
import com.assembla.client.AssemblaAPI;
import com.assembla.client.Paging;
import cz.zcu.kiv.spade.domain.Artifact;
import cz.zcu.kiv.spade.domain.WorkItem;
import cz.zcu.kiv.spade.domain.enums.ArtifactClass;
import cz.zcu.kiv.spade.pumps.issuetracking.AttachmentMiner;

import java.util.ArrayList;
import java.util.List;

class AssemblaAttachmentMiner extends AttachmentMiner<Document> {

    AssemblaAttachmentMiner(AssemblaPump pump) {
        super(pump);
    }

    @Override
    protected void mineAttachments(WorkItem item, Iterable<Document> attachments) {
        for (Document attachment : attachments) {
            Artifact artifact = new Artifact();
            artifact.setArtifactClass(ArtifactClass.FILE);
            artifact.setMimeType(attachment.getContentType());
            artifact.setUrl(attachment.getUrl().toString());
            artifact.setAuthor(addPerson(((AssemblaPeopleMiner) pump.getPeopleMiner()).generateIdentity(attachment.getCreatedBy())));
            artifact.setCreated(((AssemblaPump) pump).convertDate(attachment.getCreatedAt()));
            artifact.setExternalId(attachment.getId());
            artifact.setName(attachment.getFilename());
            artifact.setDescription(attachment.getDescription());
            artifact.setSize(attachment.getFilesize());

            generateAttachmentConfig(item, artifact);
        }
    }

    List<Document> collectAttachments(Integer id) {
        List<Document> attachments = new ArrayList<>();
        // TODO explore Paging
        for (Document document : ((AssemblaAPI) pump.getRootObject()).documents(pump.getPi().getExternalId()).getAll(new Paging(0, -1)).asList()) {
            if (document.getTicketId().equals(id)) {
                attachments.add(document);
            }
        }
        return attachments;
    }
}
