package cz.zcu.kiv.spade.pumps.issuetracking.jira;

import com.atlassian.jira.rest.client.domain.Comment;
import cz.zcu.kiv.spade.domain.Configuration;
import cz.zcu.kiv.spade.domain.WorkUnit;
import cz.zcu.kiv.spade.pumps.issuetracking.CommentMiner;

class JiraCommentMiner extends CommentMiner<Comment> {

    JiraCommentMiner(JiraPump pump) {
        super(pump);
    }

    @Override
    protected void generateUnitCommentConfig(WorkUnit unit, Comment comment) {
        Configuration configuration = new Configuration();
        if (comment.getId() != null) {
            configuration.setExternalId(comment.getId().toString());
        }
        configuration.setDescription(comment.getBody().trim());
        configuration.setAuthor(addPerson(((JiraPeopleMiner) pump.getPeopleMiner()).generateIdentity(comment.getAuthor())));
        configuration.setCreated(comment.getCreationDate().toDate());
        configuration.getChanges().add(generateCommentChange(unit));

        pump.getPi().getProject().getConfigurations().add(configuration);
    }
}
