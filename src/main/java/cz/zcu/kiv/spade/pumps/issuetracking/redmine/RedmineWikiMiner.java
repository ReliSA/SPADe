package cz.zcu.kiv.spade.pumps.issuetracking.redmine;

import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.WikiManager;
import com.taskadapter.redmineapi.bean.Project;
import com.taskadapter.redmineapi.bean.WikiPage;
import com.taskadapter.redmineapi.bean.WikiPageDetail;
import cz.zcu.kiv.spade.App;
import cz.zcu.kiv.spade.domain.Artifact;
import cz.zcu.kiv.spade.domain.Configuration;
import cz.zcu.kiv.spade.domain.WorkItemChange;
import cz.zcu.kiv.spade.domain.WorkItemRelation;
import cz.zcu.kiv.spade.domain.enums.ArtifactClass;
import cz.zcu.kiv.spade.pumps.issuetracking.WikiMiner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class RedmineWikiMiner extends WikiMiner {

    private static final String WIKI_PERMISSION_ERR_MSG = "Insufficient permissions for wiki";
    private static final String WIKI_DETAIL_PERMISSION_ERR_FORMAT = WIKI_PERMISSION_ERR_MSG + " detail: %s";
    private static final String DESC_WITH_COMMENTS_FORMAT = "%s\n\nComments: %s";

    private final RedmineAttachmentMiner attachmentMiner;

    RedmineWikiMiner(RedminePump pump) {
        super(pump);
        attachmentMiner = new RedmineAttachmentMiner(pump);
    }

    @Override
    public void mineWiki() {
        Map<String, Artifact> wikies = new HashMap<>();

        WikiManager wikiMgr = ((RedmineManager) pump.getRootObject()).getWikiManager();
        List<WikiPage> pages = new ArrayList<>();
        try {
            pages = wikiMgr.getWikiPagesByProject(((Project) pump.getSecondaryObject()).getIdentifier());
        } catch (RedmineException e) {
            App.printLogMsg(WIKI_PERMISSION_ERR_MSG, false);
        }
        for (WikiPage page : pages) {
            Artifact wikiPage = mineWikiPage(page);
            wikies.put(wikiPage.getName(), wikiPage);
        }

        linkWikiPages(wikies);
    }

    /**
     * links parent and children wiki pages
     *
     * @param wikies map of wiki pages
     */
    private void linkWikiPages(Map<String, Artifact> wikies) {
        WikiManager wikiMgr = ((RedmineManager) pump.getRootObject()).getWikiManager();
        List<WikiPage> pages = new ArrayList<>();
        try {
            pages = wikiMgr.getWikiPagesByProject(((Project) pump.getSecondaryObject()).getIdentifier());
        } catch (RedmineException e) {
            App.printLogMsg(WIKI_PERMISSION_ERR_MSG, false);
        }

        for (WikiPage page : pages) {

            WikiPageDetail childDetail = null;
            try {
                childDetail = wikiMgr.getWikiPageDetailByProjectAndTitle(page.getTitle(), ((Project) pump.getSecondaryObject()).getIdentifier());
            } catch (RedmineException e) {
                App.printLogMsg(String.format(WIKI_DETAIL_PERMISSION_ERR_FORMAT, page.getTitle()), false);
            }
            if (childDetail == null) continue;

            Artifact parent = wikies.get(childDetail.getParent().getTitle());
            Artifact child = wikies.get(page.getTitle());

            parent.getRelatedItems().add(new WorkItemRelation(child, resolveRelation(PARENT_OF)));
            child.getRelatedItems().add(new WorkItemRelation(parent, resolveRelation(CHILD_OF)));
        }
    }

    /**
     * mine a singular wiki page
     */
    private Artifact mineWikiPage(WikiPage page) {
        WikiManager wikiMgr = ((RedmineManager) pump.getRootObject()).getWikiManager();

        Artifact artifact = new Artifact();
        artifact.setArtifactClass(ArtifactClass.WIKIPAGE);
        artifact.setCreated(page.getCreatedOn());
        artifact.setName(page.getTitle());

        WikiPageDetail detail = null;
        try {
            detail = wikiMgr.getWikiPageDetailByProjectAndTitle(((Project) pump.getSecondaryObject()).getIdentifier(), page.getTitle());
        } catch (RedmineException e) {
            App.printLogMsg(String.format(WIKI_DETAIL_PERMISSION_ERR_FORMAT, page.getTitle()), false);
        }

        if (detail != null) {
            artifact.setDescription(String.format(DESC_WITH_COMMENTS_FORMAT, detail.getText(), detail.getComments()));
            artifact.setAuthor(addPerson(((RedminePeopleMiner) pump.getPeopleMiner()).generateIdentity(detail.getUser())));
            attachmentMiner.mineAttachments(artifact, detail.getAttachments());
        }

        WorkItemChange change = new WorkItemChange();
        change.setType(WorkItemChange.Type.ADD);
        change.setChangedItem(artifact);

        Configuration configuration = new Configuration();
        configuration.setCreated(artifact.getCreated());
        configuration.setAuthor(artifact.getAuthor());
        configuration.getChanges().add(change);
        pump.getPi().getProject().getConfigurations().add(configuration);

        return artifact;
    }
}
