package cz.zcu.kiv.spade.pumps.issuetracking.bugzilla;

import b4j.core.Comment;
import cz.zcu.kiv.spade.domain.Configuration;
import cz.zcu.kiv.spade.domain.WorkUnit;
import cz.zcu.kiv.spade.pumps.issuetracking.CommentMiner;

class BugzillaCommentMiner extends CommentMiner<Comment> {

    private static final String ADD_ATTACHMENT_FORMAT = "\n\n%s";
    private static final String ATTACHMENTS_LABEL = "Attachments: ";

    BugzillaCommentMiner(BugzillaPump pump) {
        super(pump);
    }

    @Override
    protected void generateUnitCommentConfig(WorkUnit unit, Comment comment) {
        Configuration configuration = new Configuration();
        configuration.setExternalId(comment.getId());
        configuration.setDescription(comment.getTheText().trim());
        configuration.setAuthor(addPerson(((BugzillaPeopleMiner) pump.getPeopleMiner()).generateIdentity(comment.getAuthor())));
        configuration.setCreated(comment.getCreationTimestamp());
        configuration.getChanges().add(generateCommentChange(unit));

        StringBuilder attachments = new StringBuilder();
        for (String attachment : comment.getAttachments()) {
            attachments.append(String.format(ADD_ATTACHMENT_FORMAT, attachment.trim()));
        }
        if (attachments.length() > 0) {
            configuration.setDescription(configuration.getDescription() + String.format(ADD_ATTACHMENT_FORMAT, ATTACHMENTS_LABEL + attachments));
        }

        pump.getPi().getProject().getConfigurations().add(configuration);
    }
}
