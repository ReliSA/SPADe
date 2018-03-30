package cz.zcu.kiv.spade.pumps.issuetracking.redmine;

import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.WikiManager;
import com.taskadapter.redmineapi.bean.Project;
import com.taskadapter.redmineapi.bean.WikiPage;
import com.taskadapter.redmineapi.bean.WikiPageDetail;
import cz.zcu.kiv.spade.App;
import cz.zcu.kiv.spade.domain.*;
import cz.zcu.kiv.spade.domain.enums.ArtifactClass;
import cz.zcu.kiv.spade.pumps.DataPump;
import cz.zcu.kiv.spade.pumps.issuetracking.AttachmentMiner;
import cz.zcu.kiv.spade.pumps.issuetracking.WikiMiner;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URLConnection;
import java.text.ParseException;
import java.util.*;

class RedmineWikiMiner extends WikiMiner {

    private static final String WIKI_PERMISSION_ERR_MSG = "Insufficient permissions for wiki";
    private static final String WIKI_DETAIL_PERMISSION_ERR_FORMAT = WIKI_PERMISSION_ERR_MSG + " detail: %s";
    private static final String WIKIES_MINED_FORMAT = "%s wiki pages mined";
    private static final int WIKIES_BATCH_SIZE = 50;
    private static final String PAGES_COUNT_FORMAT = "%s wiki pages to mine";
    private static final String HISTORY_URL_FORMAT = "%s/wiki/%s/history?per_page=100";

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
            App.printLogMsg(this, WIKI_PERMISSION_ERR_MSG);
        }


        App.printLogMsg(this, String.format(PAGES_COUNT_FORMAT, pages.size()));
        for (int i = 0; i < pages.size();) {

            Document history = null;
            try {
                history = Jsoup.connect(String.format(HISTORY_URL_FORMAT, pump.getPi().getUrl(), pages.get(i).getTitle())).get();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Artifact wikiPage = mineWikiPage(pages.get(i), history);

            if (history != null) {
                parsePageHistory(pages.get(i), wikiPage, history);
            }
            wikies.put(wikiPage.getName(), wikiPage);
            i++;
            if (i % WIKIES_BATCH_SIZE == 0 || i == pages.size())
            App.printLogMsg(this, String.format(WIKIES_MINED_FORMAT, i + DataPump.SLASH + pages.size()));
        }

        linkWikiPages(wikies);
    }

    private void parsePageHistory(WikiPage page, Artifact artifact, Document history) {
        try {
            Elements rows = history.getElementsByTag(RedmineXmlConstants.TBODY_ELEMENT).first().getElementsByTag(RedmineXmlConstants.TR_ELEMENT);

            Element updateElement = history.getElementsByClass(RedmineXmlConstants.UPDATED_ON).first();
            String updated = updateElement.text();
            Date created = RedmineXmlConstants.DATE_FORMAT.parse(updated);

            Element authorElement = updateElement.nextElementSibling();
            String name = authorElement.text();
            String login = "";
            if (!authorElement.getElementsByTag(RedmineXmlConstants.A_ELEMENT).isEmpty()) {
                login = authorElement.getElementsByTag(RedmineXmlConstants.A_ELEMENT).first().attr(RedmineXmlConstants.HREF_ATTR);
                login = login.replace(RedmineXmlConstants.USERS_PREFIX, "");
            }
            Person author = addPerson(((RedminePeopleMiner) pump.getPeopleMiner()).generateIdentity(login, name));

            String comment = authorElement.nextElementSibling().text();

            int version = page.getVersion() - 1;
            if (artifact.getDescription().isEmpty()) {
                Document current = Jsoup.connect(artifact.getUrl()).get();
                String desc = current.getElementsByClass(RedmineXmlConstants.WIKI_PAGE).first().wholeText().trim();
                desc = desc.replace(RedmineXmlConstants.WIKI_MARK, "");
                artifact.setDescription(desc);

                parseAttachments(artifact, current);
            }

            String newDesc = artifact.getDescription();
            String oldDesc;

            while (version > 0) {
                Document previous = Jsoup.connect(artifact.getUrl() + DataPump.SLASH + version).get();
                oldDesc = previous.getElementsByClass(RedmineXmlConstants.WIKI_PAGE).first().wholeText().trim();
                oldDesc = oldDesc.replace(RedmineXmlConstants.WIKI_MARK, "");

                WorkItemChange change = new WorkItemChange();
                change.setChangedItem(artifact);
                change.setType(WorkItemChange.Type.MODIFY);
                change.getFieldChanges().add(new FieldChange(DESC_FIELD_NAME, oldDesc, newDesc));

                Configuration modification = new Configuration();
                modification.setCreated(created);
                modification.setAuthor(author);
                modification.setDescription(comment);
                modification.getChanges().add(change);

                pump.getPi().getProject().getConfigurations().add(modification);

                newDesc = oldDesc;
                updateElement = previous.getElementsByTag(RedmineXmlConstants.EM_ELEMENT).first();
                updated = updateElement.text();
                updated = updated.split(COMMA)[1].trim();
                created = RedmineXmlConstants.DATE_FORMAT.parse(updated);
                name = updated.split(COMMA)[0].trim();
                login = "";
                if (!updateElement.getElementsByTag(RedmineXmlConstants.A_ELEMENT).isEmpty()) {
                    login = updateElement.getElementsByTag(RedmineXmlConstants.A_ELEMENT).first().attr(RedmineXmlConstants.HREF_ATTR);
                    login = login.replace(RedmineXmlConstants.USERS_PREFIX, "");
                }
                author = addPerson(((RedminePeopleMiner) pump.getPeopleMiner()).generateIdentity(login, name));

                comment = rows.get(rows.size() - version).getElementsByClass(RedmineXmlConstants.COMMENTS).first().text();

                if (version == 1 && artifact.getAuthor() == null) {
                    artifact.setAuthor(author);
                }
                version--;
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    private void parseAttachments(Artifact artifact, Document current) throws ParseException {
        Element attachmentsSection = current.getElementsByClass(RedmineXmlConstants.ATTACHMENTS).first();
        if (attachmentsSection != null) {
            String server = pump.getPi().getUrl().substring(0, pump.getPi().getUrl().indexOf(RedmineXmlConstants.PROJECTS_RELATIVE_URL));

            for (Element element : attachmentsSection.getElementsByTag(RedmineXmlConstants.P_ELEMENT)) {
                Element link = element.getElementsByTag(RedmineXmlConstants.A_ELEMENT).first();
                String url = server + link.attr(RedmineXmlConstants.HREF_ATTR).trim();
                String sizeString = element.getElementsByClass(RedmineXmlConstants.SIZE).first().text().trim();
                String fileAuthor = element.getElementsByClass(RedmineXmlConstants.AUTHOR).first().text().split(COMMA + DataPump.SPACE)[0].trim();
                String fileCreated = element.getElementsByClass(RedmineXmlConstants.AUTHOR).first().text().split(COMMA + DataPump.SPACE)[1].trim();

                Artifact attachment = new Artifact();
                attachment.setArtifactClass(ArtifactClass.FILE);
                attachment.setUrl(url);
                attachment.setName(link.text().trim());
                attachment.setSize(((RedminePump) pump).parseSize(sizeString));
                attachment.setAuthor(addPerson(((RedminePeopleMiner) pump.getPeopleMiner()).generateIdentity("", fileAuthor)));
                attachment.setCreated(RedmineXmlConstants.DATE_FORMAT.parse(fileCreated));
                attachment.setMimeType(URLConnection.guessContentTypeFromName(attachment.getName()));
                if (attachment.getMimeType() == null) {
                    attachment.setMimeType(attachment.getName().substring(attachment.getName().lastIndexOf(DataPump.DOT) + 1));
                }

                WorkItemChange attachChange = new WorkItemChange();
                attachChange.setType(WorkItemChange.Type.MODIFY);
                attachChange.setChangedItem(artifact);
                attachChange.setDescription(AttachmentMiner.ATTACH_CHANGE_NAME);

                WorkItemChange createAttachmentChange = new WorkItemChange();
                createAttachmentChange.setType(WorkItemChange.Type.ADD);
                createAttachmentChange.setChangedItem(attachment);

                Configuration configuration = new Configuration();
                configuration.setAuthor(attachment.getAuthor());
                configuration.setCreated(attachment.getCreated());
                configuration.getChanges().add(attachChange);
                configuration.getChanges().add(createAttachmentChange);

                pump.getPi().getProject().getConfigurations().add(configuration);

                artifact.getRelatedItems().add(new WorkItemRelation(attachment, resolveRelation(HAS_ATTACHED)));
                attachment.getRelatedItems().add(new WorkItemRelation(artifact, resolveRelation(ATTACHED_TO)));

            }
        }
    }

    /**
     * links parent and children wiki pages
     *
     * @param wikies map of wiki pages
     */
    private void linkWikiPages(Map<String, Artifact> wikies) {
        WikiManager wikiMgr = ((RedmineManager) pump.getRootObject()).getWikiManager();

        Document hierarchy = null;
        try {
            hierarchy = Jsoup.connect(pump.getPi().getUrl() + RedmineXmlConstants.WIKI_INDEX_SUFFIX).get();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            List<WikiPage> pages = wikiMgr.getWikiPagesByProject(((Project) pump.getSecondaryObject()).getIdentifier());
            for (WikiPage page : pages) {

                Artifact parent = null;
                Artifact child = null;
                try {
                    WikiPageDetail childDetail = wikiMgr.getWikiPageDetailByProjectAndTitle(((Project) pump.getSecondaryObject()).getIdentifier(), page.getTitle());
                    if (childDetail.getParent() == null) continue;
                    parent = wikies.get(childDetail.getParent().getTitle());
                    child = wikies.get(page.getTitle());
                } catch (RedmineException e) {
                    if (hierarchy != null) {
                        Elements nodes = hierarchy.getElementsByClass(RedmineXmlConstants.PAGES_HIERARCHY);
                        if (!nodes.isEmpty()) {
                            for (Element pageLink : nodes.first().getElementsByTag(RedmineXmlConstants.A_ELEMENT)) {
                                child = wikies.get(pageLink.ownText().trim().replace(DataPump.SPACE, RedmineXmlConstants.UNDERSCORE));
                                Element levelUp = pageLink.parent().parent().previousElementSibling();
                                if (levelUp.tagName().equals(RedmineXmlConstants.A_ELEMENT) && levelUp.attr(RedmineXmlConstants.HREF_ATTR).contains(RedmineXmlConstants.WIKI_PREFIX)) {
                                    parent = wikies.get(levelUp.ownText().trim().replace(DataPump.SPACE, RedmineXmlConstants.UNDERSCORE));
                                }
                            }
                        }
                    }
                    else {
                        App.printLogMsg(this, String.format(WIKI_DETAIL_PERMISSION_ERR_FORMAT, page.getTitle()));
                    }
                }
                if (parent == null || child == null) continue;

                parent.getRelatedItems().add(new WorkItemRelation(child, resolveRelation(PARENT_OF)));
                child.getRelatedItems().add(new WorkItemRelation(parent, resolveRelation(CHILD_OF)));
            }
        } catch (RedmineException e) {
            App.printLogMsg(this, WIKI_PERMISSION_ERR_MSG);
        }
    }

    /**
     * mine a singular wiki page
     */
    private Artifact mineWikiPage(WikiPage page, Document history) {
        WikiManager wikiMgr = ((RedmineManager) pump.getRootObject()).getWikiManager();

        Artifact artifact = new Artifact();
        artifact.setArtifactClass(ArtifactClass.WIKIPAGE);
        artifact.setCreated(page.getCreatedOn());
        artifact.setName(page.getTitle());
        artifact.setUrl(pump.getPi().getUrl() + RedmineXmlConstants.WIKI_PREFIX + page.getTitle());

        WikiPageDetail detail = null;
        try {
            detail = wikiMgr.getWikiPageDetailByProjectAndTitle(((Project) pump.getSecondaryObject()).getIdentifier(), page.getTitle());
        } catch (RedmineException ignore) {}

        if (detail != null) {
            artifact.setDescription(detail.getText());
            artifact.setAuthor(addPerson(((RedminePeopleMiner) pump.getPeopleMiner()).generateIdentity(detail.getUser())));
            attachmentMiner.mineAttachments(artifact, detail.getAttachments());
        }


        WorkItemChange change = new WorkItemChange();
        change.setType(WorkItemChange.Type.ADD);
        change.setChangedItem(artifact);

        Configuration configuration = new Configuration();
        configuration.setCreated(artifact.getCreated());
        configuration.setAuthor(artifact.getAuthor());
        if (history != null) {
            Elements rows = history.getElementsByTag(RedmineXmlConstants.TBODY_ELEMENT).first().getElementsByTag(RedmineXmlConstants.TR_ELEMENT);
            String creationComment = rows.get(rows.size() - 1).getElementsByClass(RedmineXmlConstants.COMMENTS).first().text();
            configuration.setDescription(creationComment);
        }
        configuration.getChanges().add(change);

        pump.getPi().getProject().getConfigurations().add(configuration);

        return artifact;
    }
}
