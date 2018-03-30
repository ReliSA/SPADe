package cz.zcu.kiv.spade.pumps.issuetracking.github;

import cz.zcu.kiv.spade.App;
import cz.zcu.kiv.spade.domain.*;
import cz.zcu.kiv.spade.pumps.issuetracking.CommentMiner;
import cz.zcu.kiv.spade.pumps.vcs.git.GitPump;
import org.kohsuke.github.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.List;

class GitHubCommentMiner extends CommentMiner<GHIssueComment> {

    private static final String COMMIT_COMMENT_SPECIFICS_FORMAT = "File: %s\nLine: %d\n\n";
    private static final String COMMIT_COMMENTS_MINED_FORMAT = "mined %d/%d commit comments";
    private static final int COMMIT_COMMENTS_MSG_BATCH_SIZE = 500;
    private static final int COMMIT_COMMENTS_BATCH_SIZE = 100;

    GitHubCommentMiner(GitHubPump pump) {
        super(pump);
    }

    @Override
    protected void generateUnitCommentConfig(WorkUnit unit, GHIssueComment comment) {
        Configuration configuration = new Configuration();
        configuration.setExternalId(Long.toString(comment.getId()));
        configuration.setDescription(comment.getBody().trim());

        GHUser user;
        Date creation;
        while (true) {
            try {
                user = comment.getUser();
                creation = comment.getCreatedAt();
                break;
            } catch (IOException e) {
                ((GitHubPump) pump).resetRootObject();
            }
        }
        Person commenter = addPerson(((GitHubPeopleMiner) pump.getPeopleMiner()).generateIdentity(user));
        configuration.setAuthor(commenter);
        configuration.setCreated(creation);
        configuration.getChanges().add(generateCommentChange(unit));

        pump.getPi().getProject().getConfigurations().add(configuration);
    }

    private Configuration generateCommitCommentConfig(Commit commit, GHCommitComment comment) {
        WorkItemChange change = new WorkItemChange();
        change.setType(WorkItemChange.Type.COMMENT);
        change.setChangedItem(commit);

        Configuration configuration = new Configuration();
        configuration.setExternalId(Long.toString(comment.getId()));
        configuration.setUrl(comment.getHtmlUrl().toString());
        String specifics = "";
        if (comment.getLine() != -1) {
            specifics += String.format(COMMIT_COMMENT_SPECIFICS_FORMAT, comment.getPath(), comment.getLine());
        }
        configuration.setDescription(specifics + comment.getBody().trim());

        GHUser user;
        Date creation;
        while (true) {
            try {
                user = comment.getUser();
                creation = comment.getCreatedAt();
                break;
            } catch (IOException e) {
                ((GitHubPump) pump).resetRootObject();
            }
        }
        configuration.setAuthor(pump.getPeopleMiner().addPerson(((GitHubPeopleMiner) pump.getPeopleMiner()).generateIdentity(user)));
        configuration.setCreated(creation);
        configuration.getChanges().add(change);

        return configuration;
    }



    /**
     * mines commit comments
     */
    void mineCommitComments() {
        List<GHCommitComment> comments = ((GHRepository) pump.getRootObject()).listCommitComments().asList();
        int count = 1;
        for (GHCommitComment comment : comments) {

            GHCommit ghCommit = null;

            while (true) {
                try {
                    ghCommit = comment.getCommit();
                    break;
                } catch (IOException e) {
                    if (e instanceof FileNotFoundException) break;
                    else ((GitHubPump) pump).resetRootObject();
                }
            }

            if (ghCommit == null) {
                count++;
                continue;
            }

            Commit commit;
            commit = pump.getPi().getProject().getCommit(ghCommit.getSHA1().substring(0, GitPump.SHORT_COMMIT_HASH_LENGTH));

            if (commit != null) {
                pump.getPi().getProject().getConfigurations().add(generateCommitCommentConfig(commit, comment));
            }
            if ((count % COMMIT_COMMENTS_BATCH_SIZE) == 0) {
                if ((count % COMMIT_COMMENTS_MSG_BATCH_SIZE) == 0) {
                    App.printLogMsg(this, String.format(COMMIT_COMMENTS_MINED_FORMAT, count, comments.size()));
                }
                ((GitHubPump) pump).checkRateLimit();
            }
            count++;
        }
    }
}
