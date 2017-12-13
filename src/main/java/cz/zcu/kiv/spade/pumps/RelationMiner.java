package cz.zcu.kiv.spade.pumps;

import cz.zcu.kiv.spade.App;
import cz.zcu.kiv.spade.domain.Commit;
import cz.zcu.kiv.spade.domain.WorkItem;
import cz.zcu.kiv.spade.domain.WorkUnit;
import cz.zcu.kiv.spade.domain.enums.Tool;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class RelationMiner extends DataMiner {

    // TODO unify relation miners, decide on regex by tool(s), search mentions by url
    private static final String DEFAULT_ISSUE_MENTION_REGEX = "(?<=^#|\\W#|_#|$#)\\d{1,4}(?=\\W|_|^|$)";
    private static final String JIRA_ISSUE_MENTION_REGEX = "(-\\d{1,4})(?=[^0-9])";
    /**
     * regular expression for SVN revision marker
     */
    private static final String SVN_REVISION_REGEX = "(?<=^r|\\Wr|_r|$r)\\d{1,4}(?=\\W|_|^|$)";
    /**
     * regular expression for git commit hash
     */
    private static final String GIT_COMMIT_REGEX = "(?<=^r|\\Wr|_r|$r)\\[a-f0-9]{7}(?=\\W|_|^|$)";

    protected static final String PARENT_CHILD_FORMAT = "parent -> child: %d -> %d";

    protected RelationMiner(DataPump pump) {
        super(pump);
    }

    /**
     * gets substrings fitting given regular expression in a string
     *
     * @param text  text to look into
     * @param regex regular expression for searched substrings
     * @return set of substring fitting the expression
     */
    private Set<String> mineMentions(String text, String regex) {
        Set<String> mentions = new HashSet<>();
        if (text == null) return mentions;

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);

        while (matcher.find()) {
            mentions.add(matcher.group());
            text = text.substring(matcher.end());
            matcher = matcher.reset(text);
        }
        return mentions;
    }

    /**
     * mines all mentions of other Work Items in a given items's description
     * and links mentioned items to the given one
     *
     * @param item given Work Item instance
     */
    protected void mineAllMentionedItems(WorkItem item) {
        mineAllMentionedItems(item, item.getDescription());
    }

    /**
     * mines all mentions of Work Items in a given string
     * and links mentioned items to the given one
     *
     * @param item given Work Item instance
     * @param text text to search mentions in
     */
    protected void mineAllMentionedItems(WorkItem item, String text) {
        mineMentionedUnits(item, text);
        mineAllMentionedCommits(item, text);
    }

    /**
     * mines all mentions of other Work Items (possibly to be found in git repository data}
     * in a given items's description and links mentioned items to the given one
     *
     * @param item given Work Item instance
     */
    protected void mineAllMentionedItemsGit(WorkItem item) {
        mineAllMentionedItemsGit(item, item.getDescription());
    }

    /**
     * mines all mentions of Work Items (possibly to be found in git repository data}
     * in a given string and links mentioned items to the given one
     *
     * @param item given Work Item instance
     * @param text text to search mentions in
     */
    protected void mineAllMentionedItemsGit(WorkItem item, String text) {
        mineMentionedUnits(item, text);
        mineMentionedGitCommits(item, text);
    }

    /*void mineAllMentionedItemsSvn(WorkItem item) {
        mineAllMentionedItemsSvn(item, item.getDescription());
    }

    void mineAllMentionedItemsSvn(WorkItem item, String text){
        mineMentionedUnits(item, text);
        mineMentionedSvnCommits(item, text);
    }

    void mineMentionedUnits(WorkItem item) {
        mineMentionedUnits(item, item.getDescription());
    }*/

    /**
     * mines all mentions of Work Unit in a given string and links mentioned units to the given Work Item
     *
     * @param item given Work Item instance
     * @param text text to search mentions in
     */
    protected void mineMentionedUnits(WorkItem item, String text) {
        // TODO check Bugzilla, Assembla regex
        String regex;
        switch (pump.tool) {
            case JIRA:
                regex = pump.pi.getName() + JIRA_ISSUE_MENTION_REGEX;
                break;
            default:
                regex = DEFAULT_ISSUE_MENTION_REGEX;
                break;
        }
        for (String mention : mineMentions(text, regex)) {
            if (pump.tool == Tool.JIRA) {
                App.printLogMsg(mention + "\n" + text + "\n\n", false);
                mention = Integer.toString(getNumberAfterLastDash(mention));
            }
            if (pump.pi.getProject().containsUnit(mention)) {
                WorkUnit mentioned = pump.pi.getProject().getUnit(mention);
                generateMentionRelation(item, mentioned);
            } else {
                generateDeletedIssue(Integer.parseInt(mention));
            }
        }
    }

    /*void mineMentionedCommits(WorkItem item) {
        mineMentionedGitCommits(item, item.getDescription());
        mineMentionedSvnCommits(item, item.getDescription());
    }*/

    /**
     * mines all mentions of git or SVN commits in a given string and links mentioned commits to the given Work Item
     *
     * @param item given Work Item instance
     * @param text text to search mentions in
     */
    private void mineAllMentionedCommits(WorkItem item, String text) {
        mineMentionedGitCommits(item, text);
        mineMentionedSvnCommits(item, text);
    }

    /**
     * mines all mentions of git commits in a given string and links mentioned commits to the given Work Item
     *
     * @param item given Work Item instance
     * @param text text to search mentions in
     */
    protected void mineMentionedGitCommits(WorkItem item, String text) {
        mineMentionedItemsCommit(item, text, GIT_COMMIT_REGEX);
    }

    /**
     * mines all mentions of SVN commits in a given string and links mentioned commits to the given Work Item
     *
     * @param item given Work Item instance
     * @param text text to search mentions in
     */
    protected void mineMentionedSvnCommits(WorkItem item, String text) {
        mineMentionedItemsCommit(item, text, SVN_REVISION_REGEX);
    }

    /**
     * mines all mentions of git or SVN (base on the given regular expression) commits
     * in a given string and links mentioned commits to the given Work Item
     *
     * @param item  given Work Item instance
     * @param text  text to search mentions in
     * @param regex regular expression for mentions to look for
     */
    private void mineMentionedItemsCommit(WorkItem item, String text, String regex) {
        for (String mention : mineMentions(text, regex)) {
            if (pump.pi.getProject().containsCommit(mention)) {
                Commit mentioned = pump.pi.getProject().getCommit(mention);
                generateMentionRelation(item, mentioned);
            }
        }
    }

    public void mineAllRelations() {
        mineMentions();
    }

    /**
     * mines all the mentions of WorkItems from other WorkItem's data and creates links where necessary
     */
    protected abstract void mineMentions();
}
