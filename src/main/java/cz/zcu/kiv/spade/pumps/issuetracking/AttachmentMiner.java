package cz.zcu.kiv.spade.pumps.issuetracking;

import cz.zcu.kiv.spade.domain.*;
import cz.zcu.kiv.spade.pumps.DataMiner;
import cz.zcu.kiv.spade.pumps.DataPump;

public abstract class AttachmentMiner<AttachmentObject> extends DataMiner {

    public static final String ATTACH_CHANGE_NAME = "attachment added";
    public static final String DETACH_CHANGE_NAME = "attachment deleted";

    protected AttachmentMiner(DataPump pump) {
        super(pump);
    }

    /**
     * mines attachments of an issue or wiki page
     *
     * @param attachments attachments
     * @param item        issue or wiki page
     */
    protected abstract void mineAttachments(WorkItem item, Iterable<AttachmentObject> attachments);

    protected void generateAttachmentConfig(WorkItem item, Artifact artifact) {
        item.getRelatedItems().add(new WorkItemRelation(artifact, resolveRelation(HAS_ATTACHED)));
        artifact.getRelatedItems().add(new WorkItemRelation(item, resolveRelation(ATTACHED_TO)));

        WorkItemChange attachChange = new WorkItemChange();
        attachChange.setChangedItem(item);
        attachChange.setType(WorkItemChange.Type.MODIFY);
        attachChange.setDescription(ATTACH_CHANGE_NAME);

        WorkItemChange createAttachmentChange = new WorkItemChange();
        createAttachmentChange.setChangedItem(artifact);
        createAttachmentChange.setType(WorkItemChange.Type.ADD);

        Configuration configuration = new Configuration();
        configuration.setCreated(artifact.getCreated());
        configuration.setAuthor(artifact.getAuthor());
        configuration.getChanges().add(attachChange);
        configuration.getChanges().add(createAttachmentChange);

        pump.getPi().getProject().getConfigurations().add(configuration);
    }
}
