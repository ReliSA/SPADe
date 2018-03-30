package cz.zcu.kiv.spade.pumps.issuetracking.bugzilla;

import cz.zcu.kiv.spade.App;
import cz.zcu.kiv.spade.domain.*;
import cz.zcu.kiv.spade.domain.enums.*;
import cz.zcu.kiv.spade.pumps.DataMiner;
import cz.zcu.kiv.spade.pumps.DataPump;
import cz.zcu.kiv.spade.pumps.issuetracking.AttachmentMiner;
import cz.zcu.kiv.spade.pumps.issuetracking.IssueMiner;
import cz.zcu.kiv.spade.pumps.issuetracking.WorklogMiner;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.*;

public class BugzillaXmlIssueMiner extends IssueMiner<Element> {

    private Map<String, WorkUnit> duplicates;
    private Date lastDownload;

    BugzillaXmlIssueMiner(BugzillaPump pump) {
        super(pump);
        duplicates = new HashMap<>();
        lastDownload = new Date();
    }

    @Override
    protected void resolveCategories(WorkUnit unit, Element bug) {
        parseCategory(unit, getFirstElementValueByName(bug, BugzillaXmlConstants.COMPONENT));
        parseCategory(unit, getFirstElementValueByName(bug, BugzillaXmlConstants.CLASSIFICATION), getFirstElementValueByName(bug, BugzillaXmlConstants.CLASSIFICATION_ID));
        String keywords = getFirstElementValueByName(bug, BugzillaXmlConstants.KEYWORDS);
        if (keywords == null) return;
        for (String keyword : keywords.split(COMMA)) {
            parseCategory(unit, keyword.trim());
        }
    }

    @Override
    protected Severity resolveSeverity(Element bug) {
        String name = getFirstElementValueByName(bug, BugzillaXmlConstants.BUG_SEVERITY);
        if (name == null || name.toUpperCase().equals(WorkUnitTypeClass.ENHANCEMENT.name())) return null;

        for (Severity severity : pump.getPi().getSeverities()) {
            if (toLetterOnlyLowerCase(severity.getName()).equals(toLetterOnlyLowerCase(name))) {
                severity.setName(name);
                return severity;
            }
        }

        SeverityClass aClass = SeverityClass.UNASSIGNED;
        // LLVM specific
        if (pump.getServer().equals("bugs.llvm.org")) {
            if (name.equals("normal")) aClass = SeverityClass.NORMAL;
            else if (name.equals("release blocker")) aClass = SeverityClass.CRITICAL;
        }

        Severity newSeverity = new Severity(name, new SeverityClassification(aClass));
        pump.getPi().getSeverities().add(newSeverity);
        return newSeverity;
    }

    private void parseCategory(WorkUnit unit, String name, String id) {
        Category category = parseCategory(unit, name);
        category.setExternalId(id);
    }

    private Category parseCategory(WorkUnit unit, String name) {
        for (Category category : pump.getPi().getCategories()) {
            if (category.getName().equals(name)) {
                unit.getCategories().add(category);
                return category;
            }
        }
        Category newCategory = new Category();
        newCategory.setName(name);
        unit.getCategories().add(newCategory);
        pump.getPi().getCategories().add(newCategory);
        return newCategory;
    }

    private Priority parsePriority(String name) {
        // priority not used
        if (name.equals(BugzillaXmlConstants.UNUSED_PRIORITY_MARKER)) return null;

        for (Priority priority : pump.getPi().getPriorities()) {
            if (toLetterOnlyLowerCase(priority.getName()).equals(toLetterOnlyLowerCase(name))) {
                priority.setName(name);
                return priority;
            }
        }

        PriorityClass aClass;
        switch (BugzillaEnumsMiner.Priority.valueOf(name)) {
            case P1: aClass = PriorityClass.HIGHEST; break;
            case P2: aClass = PriorityClass.HIGH; break;
            case P3: aClass = PriorityClass.NORMAL; break;
            case P4: aClass = PriorityClass.LOW; break;
            case P5: aClass = PriorityClass.LOWEST; break;
            default: aClass = PriorityClass.UNASSIGNED; break;
        }

        Priority newPriority = new Priority(name, new PriorityClassification(aClass));
        pump.getPi().getPriorities().add(newPriority);
        return newPriority;
    }

    @Override
    protected void mineWorklogs(WorkUnit unit, Element bug) {

    }

    @Override
    protected void mineComments(WorkUnit unit, Element bug) {
        NodeList longDescs = bug.getElementsByTagName(BugzillaXmlConstants.LONG_DESC);
        for (int i = 0; i < longDescs.getLength(); i++) {
            Element comment = (Element) longDescs.item(i);

            String description = getFirstElementValueByName(comment, BugzillaXmlConstants.THETEXT);
            if (description == null) description = "";
            description = description.trim();

            Date created = parseDate(getFirstElementValueByName(comment, BugzillaXmlConstants.BUG_WHEN));

            if (created != null && created.compareTo(unit.getCreated()) == 0) {
                unit.setDescription(description);
                continue;
            }

            Configuration configuration = new Configuration();
            configuration.setExternalId(getFirstElementValueByName(comment, BugzillaXmlConstants.COMMENT_ID));
            configuration.setAuthor(addPerson(generateIdentity(comment, BugzillaXmlConstants.WHO)));
            configuration.setCreated(created);
            configuration.setDescription(description);

            WorkItemChange change = new WorkItemChange();
            change.setType(WorkItemChange.Type.COMMENT);
            change.setChangedItem(unit);

            configuration.getChanges().add(change);

            pump.getPi().getProject().getConfigurations().add(configuration);
        }
    }

    private List<Element> getIssues() {
        List<Element> bugList = new ArrayList<>();

        File folder = new File(String.format(BugzillaXmlConstants.FOLDER_NAME_FORMAT, pump.getPi().getName()));
        File[] files = folder.listFiles();
        if (files == null || files.length == 0) return bugList;

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            return bugList;
        }

        for (File file : files) {
            if (file.isDirectory()) continue;

            if (file.getName().contains("00")) lastDownload = new Date(file.lastModified());

            Document xml;
            try {
                xml = builder.parse(file);
            } catch (SAXException | IOException e) {
                continue;
            }
            xml.getDocumentElement().normalize();
            NodeList bugs = xml.getElementsByTagName(BugzillaXmlConstants.BUG);
            for (int i = 0; i < bugs.getLength(); i++) {
                if (bugs.item(i).getNodeType() == Node.ELEMENT_NODE) {
                    bugList.add((Element) bugs.item(i));
                }
            }
        }
        return bugList;
    }

    @Override
    public void mineItems() {
        List<Element> issues = getIssues();
        int lastMsgCount = 0, issueCount = 0;
        String stat;
        for (Element issue : issues) {
            mineItem(issue);
            issueCount = pump.getPi().getProject().getUnits().size();
            if (issueCount % ISSUES_BATCH_SIZE == 0 && issueCount > lastMsgCount) {
                lastMsgCount = issueCount;
                stat = issueCount + DataPump.SLASH + issues.size();
                App.printLogMsg(this, String.format(ISSUES_MINED_FORMAT, stat));
            }
        }
        stat = issueCount + DataPump.SLASH + issues.size();
        App.printLogMsg(this, String.format(ISSUES_MINED_FORMAT, stat));
    }

    @Override
    protected void mineItem(Element bug) {
        String externalId = getFirstElementValueByName(bug, BugzillaXmlConstants.BUG_ID);
        if (pump.getPi().getProject().containsUnit(externalId)) return;

        WorkUnit unit = new WorkUnit();
        unit.setType(parseType(bug));
        unit.setExternalId(externalId);
        if (externalId != null) unit.setNumber(Integer.parseInt(externalId));
        unit.setUrl(String.format(BugzillaXmlConstants.BUG_URL_FORMAT, pump.getServer()) + externalId);
        unit.setCreated(parseDate(getFirstElementValueByName(bug, BugzillaXmlConstants.CREATION_TS)));
        unit.setName(getFirstElementValueByName(bug, BugzillaXmlConstants.SHORT_DESC));
        unit.setStatus(parseStatus(bug));
        unit.setResolution(parseResolution(bug));
        unit.setPriority(parsePriority(getFirstElementValueByName(bug, BugzillaXmlConstants.PRIORITY)));
        unit.setSeverity(resolveSeverity(bug));
        // TODO target_milestone, apikey
        unit.setAuthor(addPerson(generateIdentity(bug, BugzillaXmlConstants.REPORTER)));
        unit.setAssignee(addPerson(generateIdentity(bug, BugzillaXmlConstants.ASSIGNED_TO)));
        unit.setDueDate(parseDate(getFirstElementValueByName(bug, BugzillaXmlConstants.DEADLINE), true));

        String estimate = getFirstElementValueByName(bug, BugzillaXmlConstants.ESTIMATED_TIME);
        if (estimate != null) unit.setEstimatedTime(Double.parseDouble(estimate));
        String spent = getFirstElementValueByName(bug, BugzillaXmlConstants.ACTUAL_TIME);
        if (spent != null) unit.setSpentTime(Double.parseDouble(spent));

        pump.getPi().getProject().addUnit(unit);

        resolveCategories(unit, bug);

        mineAttachments(unit, bug);
        mineHistory(unit, bug);
        String version = getFirstElementValueByName(bug, BugzillaXmlConstants.VERSION);
        if (version != null && !version.isEmpty() && !version.equals(BugzillaXmlConstants.UNSPECIFIED)) {
            Iteration iteration = new Iteration();
            iteration.setName(version);
            unit.setIteration(iteration);
        }

        mineRelations(unit, bug);
    }

    private void mineAttachments(WorkUnit unit, Element bug) {
        NodeList attachments = bug.getElementsByTagName(BugzillaXmlConstants.ATTACHMENT);

        for (int i = 0; i < attachments.getLength(); i++) {
            Date created = parseDate(getFirstElementValueByName((Element) attachments.item(i), BugzillaXmlConstants.DATE));
            Person author = addPerson(generateIdentity(((Element) attachments.item(i)), BugzillaXmlConstants.ATTACHER));
            String sizeString = getFirstElementValueByName(((Element) attachments.item(i)), BugzillaXmlConstants.SIZE);

            Artifact artifact = new Artifact();
            artifact.setArtifactClass(ArtifactClass.FILE);
            artifact.setExternalId(getFirstElementValueByName((Element) attachments.item(i), BugzillaXmlConstants.ATTACHID));
            artifact.setName(getFirstElementValueByName(((Element) attachments.item(i)), BugzillaXmlConstants.FILENAME));
            artifact.setDescription(getFirstElementValueByName(((Element) attachments.item(i)), BugzillaXmlConstants.DESC));
            artifact.setMimeType(getFirstElementValueByName(((Element) attachments.item(i)), BugzillaXmlConstants.TYPE));
            artifact.setAuthor(author);
            artifact.setCreated(created);
            if (sizeString != null) artifact.setSize(Long.parseLong(sizeString));

            unit.getRelatedItems().add(new WorkItemRelation(artifact, resolveRelation(HAS_ATTACHED)));
            artifact.getRelatedItems().add(new WorkItemRelation(unit, resolveRelation(ATTACHED_TO)));

            WorkItemChange attachChange = new WorkItemChange();
            attachChange.setChangedItem(unit);
            attachChange.setType(WorkItemChange.Type.MODIFY);
            attachChange.setDescription(AttachmentMiner.ATTACH_CHANGE_NAME);

            WorkItemChange createAttachmentChange = new WorkItemChange();
            createAttachmentChange.setChangedItem(artifact);
            createAttachmentChange.setType(WorkItemChange.Type.ADD);

            Configuration configuration = new Configuration();
            configuration.setCreated(created);
            configuration.setAuthor(author);
            configuration.getChanges().add(attachChange);
            configuration.getChanges().add(createAttachmentChange);
            pump.getPi().getProject().getConfigurations().add(configuration);
        }
    }

    private void mineRelations(WorkUnit unit, Element bug) {
        if (unit.getResolution() != null && unit.getResolution().getaClass().equals(ResolutionClass.DUPLICATE)) {
            parseDuplicate(unit, bug);
        }
        NodeList blockers = bug.getElementsByTagName(BugzillaXmlConstants.DEPENDSON);
        for (int i = 0; i < blockers.getLength(); i ++) {
            String blockerId = blockers.item(i).getTextContent();
            if (pump.getPi().getProject().containsUnit(blockerId)) {
                WorkUnit blocker = pump.getPi().getProject().getUnit(blockerId);
                unit.getRelatedItems().add(new WorkItemRelation(blocker, resolveRelation(RelationClass.BLOCKEDBY.name().toLowerCase())));
            }
        }
        NodeList dependents = bug.getElementsByTagName(BugzillaXmlConstants.BLOCKED);
        for (int i = 0; i < dependents.getLength(); i ++) {
            String dependentId = dependents.item(i).getTextContent();
            if (pump.getPi().getProject().containsUnit(dependentId)) {
                WorkUnit dependent = pump.getPi().getProject().getUnit(dependentId);
                unit.getRelatedItems().add(new WorkItemRelation(dependent, resolveRelation(RelationClass.BLOCKS.name().toLowerCase())));
            }
        }
        NodeList relatedList = bug.getElementsByTagName(BugzillaXmlConstants.SEE_ALSO);
        for (int i = 0; i < relatedList.getLength(); i ++) {
            String relatedUrl = relatedList.item(i).getTextContent();
            if (!relatedUrl.startsWith(String.format(BugzillaXmlConstants.BUG_URL_FORMAT, pump.getServer()))) return;
            String relatedId = relatedUrl.replace(String.format(BugzillaXmlConstants.BUG_URL_FORMAT, pump.getServer()), "");
            if (pump.getPi().getProject().containsUnit(relatedId)) {
                WorkUnit related = pump.getPi().getProject().getUnit(relatedId);
                unit.getRelatedItems().add(new WorkItemRelation(related, resolveRelation(RelationClass.RELATESTO.name().toLowerCase())));
                related.getRelatedItems().add(new WorkItemRelation(unit, resolveRelation(RelationClass.RELATESTO.name().toLowerCase())));
            }
        }

        completePastRelations(unit);

        parseCommits(unit, bug);
    }

    private void parseCommits(WorkUnit unit, Element bug) {
        String commits = getFirstElementValueByName(bug, BugzillaXmlConstants.CF_FIXED_BY_COMMITS);
        if (commits == null || commits.isEmpty()) return;
        commits = commits
                    .replaceAll(DataMiner.COMMA, DataPump.SPACE)
                    .replaceAll(DataPump.SPACE + DataPump.SPACE, DataPump.SPACE);
        for (String commitString : commits.split(DataPump.SPACE)) {
            commitString = commitString.trim();
            int startIndex = commitString.length() - 1;
            while (true) {
                if (!Character.isDigit(commitString.charAt(startIndex))) {
                    startIndex++;
                    break;
                }
                if (startIndex == 0) break;
                startIndex--;
            }

            for (Commit commit : pump.getPi().getProject().getCommits()) {
                if (commit.getDescription().contains(DataPump.AT + commitString.substring(startIndex))) {
                    generateMentionRelation(commit, unit);
                }
            }
        }
    }

    private Identity generateIdentity(Element element, String role) {
        String name = element.getAttribute(BugzillaXmlConstants.NAME);
        String email = getFirstElementValueByName(element, role);
        if (email == null) email = "";
        String login = email.split(DataPump.AT)[0];

        Identity identity;
        if ((identity = generateIdentity(login)) == null) return null;
        identity.setEmail(email);
        identity.setDescription(name);
        return identity;
    }

    private Identity generateIdentity(String login) {
        if (login.startsWith(BugzillaXmlConstants.UNASSIGNED)) return null;
        if (login.contains(PLUS)) login = login.split(PLUS)[0];
        Identity identity = new Identity();
        identity.setName(login);
        return identity;
    }

    private WorkUnitType parseType(Element bug) {
        String type = getFirstElementValueByName(bug, BugzillaXmlConstants.BUG_SEVERITY);
        if (type == null) return null;
        if (type.toUpperCase().equals(WorkUnitTypeClass.ENHANCEMENT.name())) {
            return resolveType(WorkUnitTypeClass.ENHANCEMENT.name().toLowerCase());
        }
        return resolveType(WorkUnitTypeClass.BUG.name().toLowerCase());
    }

    private Date parseDate(String dateString) {
        return parseDate(dateString, false);
    }

    private Date parseDate(String dateString, boolean deadline) {
        if (dateString == null || dateString.isEmpty()) return null;
        try {
            if (deadline) return BugzillaXmlConstants.DEADLINE_FORMAT.parse(dateString);
            return BugzillaXmlConstants.DATE_FORMAT.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getFirstElementValueByName(Element bug, String name){
        if (bug.getElementsByTagName(name).getLength() == 0) return null;
        return bug.getElementsByTagName(name).item(0).getTextContent();
    }

    private Status parseStatus(Element bug) {
        String name = getFirstElementValueByName(bug, BugzillaXmlConstants.BUG_STATUS);

        for (Status status : pump.getPi().getStatuses()) {
            if (toLetterOnlyLowerCase(status.getName()).equals(toLetterOnlyLowerCase(name))) {
                if (status.getExternalId() == null) {
                    status.setName(name);
                }
                return status;
            }
        }

        StatusClass aClass;
        switch (BugzillaEnumsMiner.Status.valueOf(name)) {
            case NEW: aClass = StatusClass.NEW; break;
            case ASSIGNED: aClass = StatusClass.ACCEPTED; break;
            case RESOLVED: aClass = StatusClass.RESOLVED; break;
            case VERIFIED: aClass = StatusClass.VERIFIED; break;
            case CLOSED: aClass = StatusClass.CLOSED; break;
            case REOPENED: aClass = StatusClass.OPEN; break;
            default: aClass = StatusClass.UNASSIGNED; break;
        }

        Status newStatus = new Status(name, new StatusClassification(aClass));
        pump.getPi().getStatuses().add(newStatus);
        return newStatus;
    }

    private Resolution parseResolution(Element bug) {
        String name = getFirstElementValueByName(bug, BugzillaXmlConstants.RESOLUTION);
        if (name == null || name.isEmpty()) return null;

        for (Resolution resolution : pump.getPi().getResolutions()) {
            if (toLetterOnlyLowerCase(resolution.getName()).equals(toLetterOnlyLowerCase(name))) {
                if (resolution.getExternalId() == null) {
                    resolution.setName(name);
                }
                return resolution;
            }
        }

        ResolutionClass aClass;
        switch (BugzillaEnumsMiner.Resolution.valueOf(name)) {
            case FIXED: aClass = ResolutionClass.FIXED; break;
            case INVALID: aClass = ResolutionClass.INVALID; break;
            case DUPLICATE: aClass = ResolutionClass.DUPLICATE; break;
            case WONTFIX: aClass = ResolutionClass.WONTFIX; break;
            case WORKSFORME: aClass = ResolutionClass.WORKSFORME; break;
            case REMIND: aClass = ResolutionClass.UNFINISHED; break;
            case LATER: aClass = ResolutionClass.UNFINISHED; break;
            default: aClass = ResolutionClass.UNASSIGNED; break;
        }

        Resolution newResolution = new Resolution(name, new ResolutionClassification(aClass));
        pump.getPi().getResolutions().add(newResolution);
        return newResolution;
    }

    private void parseDuplicate(WorkUnit unit, Element bug) {
        String duplicatedId = getFirstElementValueByName(bug, BugzillaXmlConstants.DUP_ID);
        WorkUnit duplicated = pump.getPi().getProject().getUnit(unit.getNumber());
        if (duplicated != null) {
            unit.getRelatedItems().add(new WorkItemRelation(duplicated, resolveRelation(RelationClass.DUPLICATES.name().toLowerCase())));
            duplicated.getRelatedItems().add(new WorkItemRelation(unit, resolveRelation(RelationClass.DUPLICATEDBY.name().toLowerCase())));
        } else {
            duplicates.put(duplicatedId, unit);
        }
    }

    private void completePastRelations(WorkUnit unit) {
        String key = unit.getExternalId();
        if (duplicates.containsKey(key)) {
            WorkUnit duplicate = duplicates.get(key);
            duplicate.getRelatedItems().add(new WorkItemRelation(unit, resolveRelation(RelationClass.DUPLICATES.name().toLowerCase())));
            unit.getRelatedItems().add(new WorkItemRelation(duplicate, resolveRelation(RelationClass.DUPLICATEDBY.name().toLowerCase())));
            duplicates.remove(key);
        }
    }

    @Override
    protected void mineHistory(WorkUnit unit, Element bug) {
        super.mineHistory(unit, bug);

        Date updated = parseDate(getFirstElementValueByName(bug, BugzillaXmlConstants.DELTA_TS));
        // there's no changelog
        if (!unit.getCreated().before(updated)) return;

        org.jsoup.nodes.Document document;
        String url = String.format(BugzillaXmlConstants.ACTIVITY_URL_FORMAT, pump.getServer(), unit.getExternalId());
        File changelog = new File(String.format(BugzillaXmlConstants.CHANGELOG_FILE_FORMAT, pump.getPi().getName(), unit.getExternalId()));

        try {
            if (changelog.exists() && new Date(changelog.lastModified()).after(updated)) {
                document = Jsoup.parse(changelog, StandardCharsets.UTF_8.name(), "");
                parseChangelog(unit, document);
            } else {
                // get html document
                document = Jsoup.connect(url).get();
                parseChangelog(unit, document);
                if (!changelog.getParentFile().exists() && !changelog.getParentFile().mkdirs()) throw new IOException("Failed to create changelog directory!");
                Writer writer = new OutputStreamWriter(new FileOutputStream(new File(changelog.getPath())));
                writer.write(document.toString());
                writer.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void parseChangelog(WorkUnit unit, org.jsoup.nodes.Document document) {
        Elements lines = document.getElementsByTag(BugzillaXmlConstants.TR);

        // fields for table cells
        Person author;
        Date created = new Date();
        String field;
        List<String> removed, added;

        // worklog variable
        double spentTime = 0;

        // to keep previous values and convert the removed/added change schema to old/new value
        Map<String, Set<String>> oldValues = new HashMap<>();

        for (int i = 1; i < lines.size();) {
            org.jsoup.nodes.Element line = lines.get(i);
            int rowSpan = Integer.parseInt(line.children().get(0).attr("rowspan"));

            try {
                created = BugzillaXmlConstants.HTML_DATE_FORMAT.parse(line.children().get(1).text().trim());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            // change occurred after current data (XML files) were obtained
            if (!created.after(lastDownload)) {
                author = addPerson(generateIdentity(line.children().get(0).text().trim()));
                Configuration modification = new Configuration();
                modification.setCreated(created);
                modification.setAuthor(author);
                modification.setUrl(String.format(BugzillaXmlConstants.ACTIVITY_URL_FORMAT, pump.getServer(), unit.getExternalId()));

                List<FieldChange> unitChanges = new ArrayList<>();
                Artifact attachment = null;
                List<FieldChange> attachmentChanges = new ArrayList<>();
                List<FieldChange> worklogChanges = new ArrayList<>();
                for (int j = 0; j < rowSpan; j++) {
                    line = lines.get(i+j);

                    // cells for field change
                    int fieldIndex = 0, removedIndex = 1, addedIndex = 2;
                    if (j == 0) {
                        fieldIndex += 2; removedIndex += 2; addedIndex += 2;
                    }
                    field = line.children().get(fieldIndex).text().trim();
                    removed = Arrays.asList(line.children().get(removedIndex).text().trim().split(DataMiner.COMMA + DataPump.SPACE));
                    added = Arrays.asList(line.children().get(addedIndex).text().trim().split(DataMiner.COMMA + DataPump.SPACE));

                    if (field.equals(BugzillaXmlConstants.ASSIGNEE) && removed.size() == 1 && removed.get(0).startsWith(BugzillaXmlConstants.UNASSIGNED)) {
                        removed = new ArrayList<>();
                    }

                    oldValues.computeIfAbsent(field, k -> new LinkedHashSet<>());
                    // if there's no previous value but some has been removed, then the removed one was the previous (old) value
                    if (oldValues.get(field).isEmpty() && !removed.isEmpty()) {
                        oldValues.get(field).addAll(removed);
                    }
                    // new value = old - removed + added
                    Set<String> newValues = new LinkedHashSet<>();
                    newValues.addAll(oldValues.get(field));
                    newValues.removeAll(removed);
                    newValues.addAll(added);

                    String oldValue = oldValues.get(field).toString().replace(BugzillaXmlConstants.LEFT_BRACKET, "").replace(BugzillaXmlConstants.RIGHT_BRACKET, "");
                    String newValue = newValues.toString().replace(BugzillaXmlConstants.LEFT_BRACKET, "").replace(BugzillaXmlConstants.RIGHT_BRACKET, "");

                    // worklog
                    if (field.equals(BugzillaXmlConstants.HOURS_WORKED)) {
                        oldValue = Double.toString(spentTime);
                        spentTime += Double.parseDouble(newValue);
                        newValue = Double.toString(spentTime);
                        worklogChanges.add(new FieldChange(WorklogMiner.LOGTIME_FIELD_NAME, oldValue, newValue));
                    // attachment changes
                    } else if (field.toLowerCase().startsWith(BugzillaXmlConstants.ATTACHMENT)) {
                        String attachmentId = field.substring(field.indexOf(BugzillaXmlConstants.HASH));
                        attachmentId = attachmentId.substring(0, attachmentId.indexOf(DataPump.SPACE));
                        boolean found = false;
                        for (Configuration configuration : pump.getPi().getProject().getConfigurations()) {
                            for (WorkItemChange change : configuration.getChanges()) {
                                WorkItem item = change.getChangedItem();
                                if (item instanceof Artifact && item.getExternalId().equals(attachmentId)) {
                                    attachment = (Artifact) item;
                                    found = true;
                                    break;
                                }
                            }
                            if (found) break;
                        }
                        String attachmentField = field.substring(field.toLowerCase().lastIndexOf(BugzillaXmlConstants.ATTACHMENT + DataPump.SPACE));
                        attachmentChanges.add(new FieldChange(attachmentField, oldValue, newValue));
                    // proper issue changes
                    } else {
                        unitChanges.add(new FieldChange(field, oldValue, newValue));
                    }

                    // previous values consistency
                    oldValues.get(field).clear();
                    oldValues.get(field).addAll(newValues);
                }
                if (!worklogChanges.isEmpty()) {
                    CommittedConfiguration worklog = new CommittedConfiguration();
                    worklog.setAuthor(author);
                    worklog.setCreated(created);
                    worklog.setCommitted(created);
                    worklog.setUrl(modification.getUrl());

                    WorkItemChange worklogChange = new WorkItemChange();
                    worklogChange.setType(WorkItemChange.Type.LOGTIME);
                    worklogChange.setChangedItem(unit);
                    worklogChange.getFieldChanges().addAll(worklogChanges);
                    worklog.getChanges().add(worklogChange);
                    pump.getPi().getProject().getConfigurations().add(worklog);
                }
                if (!attachmentChanges.isEmpty()) {
                    WorkItemChange attachmentChange = new WorkItemChange();
                    attachmentChange.setType(WorkItemChange.Type.MODIFY);
                    attachmentChange.setChangedItem(attachment);
                    attachmentChange.getFieldChanges().addAll(attachmentChanges);
                    modification.getChanges().add(attachmentChange);
                }
                if (!unitChanges.isEmpty()) {
                    WorkItemChange unitChange = new WorkItemChange();
                    unitChange.setType(WorkItemChange.Type.MODIFY);
                    unitChange.setChangedItem(unit);
                    unitChange.getFieldChanges().addAll(unitChanges);
                    modification.getChanges().add(unitChange);
                }
                if (!modification.getChanges().isEmpty()) {
                    pump.getPi().getProject().getConfigurations().add(modification);
                }
            }
            i+=rowSpan;
        }
    }
}
