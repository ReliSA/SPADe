package cz.zcu.kiv.spade.pumps.issuetracking.redmine;

import com.taskadapter.redmineapi.bean.Journal;
import com.taskadapter.redmineapi.bean.JournalDetail;
import cz.zcu.kiv.spade.App;
import cz.zcu.kiv.spade.domain.*;
import cz.zcu.kiv.spade.domain.enums.ArtifactClass;
import cz.zcu.kiv.spade.pumps.DataPump;
import cz.zcu.kiv.spade.pumps.issuetracking.AttachmentMiner;
import cz.zcu.kiv.spade.pumps.issuetracking.ChangelogMiner;
import cz.zcu.kiv.spade.pumps.vcs.git.GitPump;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.URLConnection;
import java.text.ParseException;
import java.util.*;

class RedmineChangelogMiner extends ChangelogMiner<Journal> {

    private enum Attribute {
        name,
        description,
        priority,
        severity,
        type,
        status,
        resolution,
        categories,
        estimatedTime,
        startDate,
        dueDate,
        progress,
        assignee,
        iteration,
        other,
        ignore,
        attachment,
        relation
    }

    RedmineChangelogMiner(RedminePump pump) {
        super(pump);
    }

    @Override
    protected void generateModification(WorkUnit unit, Journal changelog) {
        Configuration configuration = new Configuration();
        configuration.setAuthor(addPerson(((RedminePeopleMiner) pump.getPeopleMiner()).generateIdentity(changelog.getUser())));
        configuration.setCreated(changelog.getCreatedOn());
        configuration.setExternalId(changelog.getId().toString());
        configuration.setDescription(changelog.getNotes());

        WorkItemChange change = new WorkItemChange();
        change.setChangedItem(unit);
        change.setType(WorkItemChange.Type.MODIFY);
        change.setFieldChanges(mineChanges(changelog));
        configuration.getChanges().add(change);

        if (!configuration.getDescription().isEmpty()) {
            if (change.getFieldChanges().isEmpty()) {
                change.setType(WorkItemChange.Type.COMMENT);
            }
            if (change.getType().equals(WorkItemChange.Type.MODIFY)){
                WorkItemChange comment = new WorkItemChange();
                comment.setChangedItem(unit);
                comment.setType(WorkItemChange.Type.COMMENT);
                configuration.getChanges().add(comment);
            }
        }

        pump.getPi().getProject().getConfigurations().add(configuration);
    }

    @Override
    protected Collection<FieldChange> mineChanges(Journal changelog) {
        List<FieldChange> changes = new ArrayList<>();
        for (JournalDetail detail : changelog.getDetails()) {
            FieldChange change = new FieldChange();
            change.setName(detail.getProperty());
            change.setNewValue(detail.getNewValue());
            change.setOldValue(detail.getOldValue());
            changes.add(change);
        }
        return changes;
    }

    void parseHistory(WorkUnit unit) {
        try {
            Document document = Jsoup.connect(unit.getUrl()).get();

            Elements journals = document.getElementsByAttributeValueContaining(RedmineXmlConstants.ID_ATTR, RedmineXmlConstants.CHANGE_PREFIX);
            Map<String, Set<String>> relationValues = new HashMap<>();
            Map<String, Artifact> attachmentMap = new HashMap<>();
            Set<Artifact> originalAttachments = new LinkedHashSet<>();

            for (Element journal : journals) {

                Configuration configuration = parseConfigurationBase(journal);
                WorkItemChange change = configuration.getChanges().get(0);
                change.setChangedItem(unit);

                Set<FieldChange> details = parseDetails(journal, configuration, relationValues, attachmentMap, originalAttachments);
                change.getFieldChanges().addAll(details);
                configuration.setDescription(parseComment(journal));

                // comment exists
                if (!configuration.getDescription().isEmpty()) {
                    // pure comment = no attribute changes, no attachment changes (no change description)
                    if (change.getFieldChanges().isEmpty() && change.getDescription().isEmpty()) {
                        change.setType(WorkItemChange.Type.COMMENT);
                    // changes exist and are commented
                    } else {
                        WorkItemChange comment = new WorkItemChange();
                        comment.setChangedItem(unit);
                        comment.setType(WorkItemChange.Type.COMMENT);
                        configuration.getChanges().add(comment);
                    }
                }

                pump.getPi().getProject().getConfigurations().add(configuration);
            }

            // create relations for final/current attachments
            for (Artifact current : attachmentMap.values()) {
                unit.getRelatedItems().add(new WorkItemRelation(current, resolveRelation(HAS_ATTACHED)));
                current.getRelatedItems().add(new WorkItemRelation(unit, resolveRelation(ATTACHED_TO)));
            }

            // add date and author to original attachments
            Configuration creation = findCreation(unit);
            for (Artifact original : originalAttachments) {
                original.setAuthor(unit.getAuthor());
                original.setCreated(unit.getCreated());
                if (creation != null) {
                    creation.getChanges().add(generateAttachmentChange(original));
                }
            }

            parseRevisions(unit, document);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Configuration findCreation(WorkUnit unit) {
        for (Configuration creation : pump.getPi().getProject().getConfigurations()) {
            for (WorkItemChange change : creation.getChanges()) {
                if (change.getChangedItem().equals(unit) && change.getType().equals(WorkItemChange.Type.ADD)) {
                    return creation;
                }
            }
        }
        return null;
    }

    private Set<FieldChange> parseDetails(Element journal, Configuration configuration,
                                          Map<String, Set<String>> relationValues,
                                          Map<String, Artifact> attachmentMap,
                                          Set<Artifact> originalAttachments) {
        Set<FieldChange> fChanges = new LinkedHashSet<>();

        Elements details = journal.child(0).getElementsByClass(RedmineXmlConstants.DETAILS);
        if (details != null && !details.isEmpty()) {

            Elements changes = details.first().getElementsByTag(RedmineXmlConstants.LI_ELEMENT);
            for (Element detail : changes) {

                String field = detail.getElementsByTag(RedmineXmlConstants.STRONG_ELEMENT).first().text();
                Attribute attribute = resolveAttribute(field);

                if (attribute.equals(Attribute.ignore)) continue;

                if (attribute.equals(Attribute.attachment)) {
                    if (detail.text().contains(RedmineXmlConstants.ADDED)) {
                        Artifact attachment = parseAttachment(configuration, detail);
                        attachmentMap.put(attachment.getName(), attachment);
                        continue;
                    } else if (detail.text().contains(RedmineXmlConstants.DELETED)) {
                        String name = detail.getElementsByTag(RedmineXmlConstants.DEL_ELEMENT).first().text().trim();
                        if (attachmentMap.remove(name) == null) {
                            originalAttachments.add(parseAttachment(configuration, name));
                        }
                        continue;
                    } else {
                        // TODO check and delete
                        App.log.println(configuration.getChanges().get(0).getChangedItem().getExternalId());
                        App.log.println(detail.text());
                    }
                }

                if (attribute.equals(Attribute.description)) {
                    FieldChange descChange;
                    if ((descChange = parseDescriptionChange(detail)) != null) {
                        fChanges.add(descChange);
                    }
                    continue;
                }

                fChanges.add(parseAttrChanges(relationValues, detail, field, attribute));
            }
        }
        return fChanges;
    }

    private FieldChange parseAttrChanges(Map<String, Set<String>> relationValues, Element detail, String field, Attribute attribute) {
        int valuesCount = detail.getElementsByTag(RedmineXmlConstants.I_ELEMENT).size();
        String from = "";
        String to = "";

        // value change
        if (valuesCount == 2) {
            from = detail.getElementsByTag(RedmineXmlConstants.I_ELEMENT).get(0).text();
            to = detail.getElementsByTag(RedmineXmlConstants.I_ELEMENT).get(1).text();
        //value set for the first time, added or deleted
        } else if (valuesCount == 1){
            String currTo;

            if (!relationValues.containsKey(field)) {
                relationValues.put(field, new LinkedHashSet<>());
            } else {
                from = relationValues.get(field).toString().replace(RedmineXmlConstants.LEFT_BRACKET, "").replace(RedmineXmlConstants.RIGHT_BRACKET, "").trim();
            }

            currTo = detail.getElementsByTag(RedmineXmlConstants.I_ELEMENT).get(0).text().trim();
            if (attribute.equals(Attribute.relation)) {
                currTo = currTo.substring(currTo.indexOf(RedmineXmlConstants.HASH) + 1);
                if (currTo.contains(DataPump.COLON)) {
                    currTo = currTo.substring(0, currTo.indexOf(DataPump.COLON));
                }
            }

            if (detail.text().contains(RedmineXmlConstants.ADDED)) {
                relationValues.get(field).add(currTo);
            } else if (detail.text().contains(RedmineXmlConstants.DELETED)) {
                relationValues.get(field).remove(currTo);
            } else if (detail.text().contains(RedmineXmlConstants.SET_TO)) {
                relationValues.get(field).clear();
                relationValues.get(field).add(currTo);
            }

            to = relationValues.get(field).toString().replace(RedmineXmlConstants.LEFT_BRACKET, "").replace(RedmineXmlConstants.RIGHT_BRACKET, "").trim();
        }

        String fieldName = attribute.equals(Attribute.other) || attribute.equals(Attribute.relation) ? field : attribute.name();
        return new FieldChange(fieldName, from, to);
    }

    private String parseComment(Element journal) {
        Elements notes = journal.child(0).getElementsByClass(RedmineXmlConstants.WIKI);
        if (notes != null && !notes.isEmpty()) {
            return notes.text();
        }
        return "";
    }

    private Configuration parseConfigurationBase(Element journal) {
        Configuration configuration = new Configuration();
        configuration.setExternalId(journal.attr(RedmineXmlConstants.ID_ATTR));

        Elements h4Children = journal.child(0).getElementsByTag(RedmineXmlConstants.H4_ELEMENT).first().getElementsByTag(RedmineXmlConstants.A_ELEMENT);
        for (Element h4Child : h4Children) {
            String href = h4Child.attr(RedmineXmlConstants.HREF_ATTR);
            if (href != null && href.startsWith(RedmineXmlConstants.USERS_PREFIX)) {
                String login = href.replace(RedmineXmlConstants.USERS_PREFIX, "");
                String name = h4Child.text();
                configuration.setAuthor(addPerson(((RedminePeopleMiner) pump.getPeopleMiner()).generateIdentity(login, name)));
            }
            String title = h4Child.attr(RedmineXmlConstants.TITLE_ATTR);
            if (title != null && !title.isEmpty()) {
                try {
                    configuration.setCreated(RedmineXmlConstants.DATE_FORMAT.parse(title));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }

        WorkItemChange change = new WorkItemChange();
        configuration.getChanges().add(change);
        change.setType(WorkItemChange.Type.MODIFY);

        return configuration;
    }

    private FieldChange parseDescriptionChange(Element detail) {
        StringBuilder from = new StringBuilder("");
        StringBuilder to = new StringBuilder("");

        String server = pump.getPi().getUrl().substring(0, pump.getPi().getUrl().indexOf(RedmineXmlConstants.PROJECTS_RELATIVE_URL));
        String link = server + detail.getElementsByTag(RedmineXmlConstants.A_ELEMENT).first().attr(RedmineXmlConstants.HREF_ATTR);
        Document descriptionPage = null;
        try {
            descriptionPage = Jsoup.connect(link).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (descriptionPage == null) return null;

        Element diff = descriptionPage.getElementsByClass(RedmineXmlConstants.TEXT_DIFF).first();
        for (Node node : diff.childNodes()) {
            if (node instanceof Element) {
                Element element = (Element) node;
                // added part
                if (element.attr(RedmineXmlConstants.CLASS_ATTR).equals(RedmineXmlConstants.DIFF_IN)) {
                    to.append(element.text());
                }
                // replaced/deleted part
                if (element.attr(RedmineXmlConstants.CLASS_ATTR).equals(RedmineXmlConstants.DIFF_OUT)) {
                    from.append(element.text());
                }
            }
            // stable part
            if (node instanceof TextNode) {
                from.append(node.toString());
                to.append(node.toString());
            }
        }
        return new FieldChange(Attribute.description.name(), from.toString(), to.toString());
    }

    private Artifact parseAttachment(Configuration configuration, String name) {
        Artifact artifact = parseArtifactBase(name);
        configuration.getChanges().get(0).setDescription(AttachmentMiner.DETACH_CHANGE_NAME);
        configuration.getChanges().add(generateDetachmentChange(artifact));
        return artifact;
    }

    private Artifact parseArtifactBase(String name) {
        Artifact artifact = new Artifact();
        artifact.setArtifactClass(ArtifactClass.FILE);
        artifact.setName(name);
        artifact.setMimeType(URLConnection.guessContentTypeFromName(artifact.getName()));
        if (artifact.getMimeType() == null) {
            artifact.setMimeType(artifact.getName().substring(artifact.getName().lastIndexOf(DataPump.DOT) + 1));
        }
        return artifact;
    }

    private Artifact parseAttachment(Configuration configuration, Element detail) {
        boolean link = true;
        Element element = detail.getElementsByTag(RedmineXmlConstants.A_ELEMENT).first();

        // file later deleted
        if (element == null) {
            element = detail.getElementsByTag(RedmineXmlConstants.I_ELEMENT).first();
            link = false;
        }
        Artifact artifact = parseArtifactBase(element.text());

        if (link) {
            String server = pump.getPi().getUrl().substring(0, pump.getPi().getUrl().indexOf(RedmineXmlConstants.PROJECTS_RELATIVE_URL));
            String url = server + element.attr(RedmineXmlConstants.HREF_ATTR);
            artifact.setUrl(url);
            artifact.setExternalId(url.substring(url.indexOf(RedmineXmlConstants.DOWNLOAD_PREFIX), url.lastIndexOf(DataPump.SLASH)));
            Element first = detail.ownerDocument().getElementsByAttributeValue(RedmineXmlConstants.HREF_ATTR, element.attr(RedmineXmlConstants.HREF_ATTR)).first();
            if (first.parent().tagName().equals(RedmineXmlConstants.P_ELEMENT)) {
                String sizeString = first.parent().getElementsByClass(RedmineXmlConstants.SIZE).first().text();
                String displayName = first.parent().ownText().substring(first.parent().ownText().indexOf(DASH) + 1).trim();
                artifact.setDescription(RedmineXmlConstants.DISPLAY_NAME_LABEL + displayName);
                artifact.setSize(((RedminePump) pump).parseSize(sizeString));
            }
        }

        artifact.setAuthor(configuration.getAuthor());
        artifact.setCreated(configuration.getCreated());
        configuration.getChanges().get(0).setDescription(AttachmentMiner.ATTACH_CHANGE_NAME);
        configuration.getChanges().add(generateAttachmentChange(artifact));

        return artifact;
    }

    private void parseRevisions(WorkUnit unit, Document document) {
        Elements revisions = document.getElementsByAttributeValueStarting(RedmineXmlConstants.TITLE_ATTR, RedmineXmlConstants.REVISION_PREFIX);
        for (Element revision : revisions) {
            String hash = revision.attr(RedmineXmlConstants.TITLE_ATTR).split(DataPump.SPACE)[1];
            Commit commit = pump.getPi().getProject().getCommit(hash.substring(0, GitPump.SHORT_COMMIT_HASH_LENGTH));
            if (commit != null) {
                unit.getRelatedItems().add(new WorkItemRelation(commit, resolveRelation(RedmineXmlConstants.RELATED)));
                commit.getRelatedItems().add(new WorkItemRelation(unit, resolveRelation(RedmineXmlConstants.RELATED)));
            }
        }
    }

    private Attribute resolveAttribute(String field) {

        // read properties file
        Properties fieldSettings = new Properties();
        try {
            FileInputStream fis = new FileInputStream(RedmineXmlConstants.FIELDS_FILE);
            InputStreamReader isr = new InputStreamReader(fis, RedmineXmlConstants.CSN);
            fieldSettings.load(isr);
            isr.close();
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String fieldPropform = field.replace(DataPump.SPACE, RedmineXmlConstants.UNDERSCORE);

        // attribute not yet saved
        if (fieldSettings.getProperty(fieldPropform) == null) {

            for (Attribute attr : Attribute.values()) {
                // field name matches attribute
                if (toLetterOnlyLowerCase(field).equals(attr.name().toLowerCase())) {
                    return attr;
                }
            }

            int choice = App.promptUserSelection(Arrays.stream(Attribute.values()).map(Enum::name).toArray(String[]::new), RedmineXmlConstants.ATTRIBUTE, field);
            fieldSettings.put(fieldPropform, Attribute.values()[choice].name());

            // save to properties file
            PrintWriter pw = null;
            try {
                pw = new PrintWriter(RedmineXmlConstants.FIELDS_FILE, RedmineXmlConstants.CSN);
                fieldSettings.store(pw, "");
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (pw != null) {
                pw.close();
            }

            return Attribute.values()[choice];

        // attribute already saved
        } else {
            return Attribute.valueOf(fieldSettings.getProperty(fieldPropform));
        }
    }

    private WorkItemChange generateAttachmentChange(Artifact artifact) {

        WorkItemChange createAttachmentChange = new WorkItemChange();
        createAttachmentChange.setChangedItem(artifact);
        createAttachmentChange.setType(WorkItemChange.Type.ADD);

        return createAttachmentChange;
    }

    private WorkItemChange generateDetachmentChange(Artifact artifact) {

        WorkItemChange deleteAttachmentChange = new WorkItemChange();
        deleteAttachmentChange.setChangedItem(artifact);
        deleteAttachmentChange.setType(WorkItemChange.Type.DELETE);

        return deleteAttachmentChange;
    }
}
