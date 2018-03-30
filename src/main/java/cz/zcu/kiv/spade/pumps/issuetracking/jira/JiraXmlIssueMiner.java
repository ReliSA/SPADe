package cz.zcu.kiv.spade.pumps.issuetracking.jira;

import cz.zcu.kiv.spade.App;
import cz.zcu.kiv.spade.domain.*;
import cz.zcu.kiv.spade.domain.enums.*;
import cz.zcu.kiv.spade.pumps.DataPump;
import cz.zcu.kiv.spade.pumps.issuetracking.AttachmentMiner;
import cz.zcu.kiv.spade.pumps.issuetracking.IssueMiner;
import cz.zcu.kiv.spade.pumps.issuetracking.WorklogMiner;
import javafx.util.Pair;
import org.apache.commons.lang.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class JiraXmlIssueMiner extends IssueMiner<String>{

    private Map<String, WorkUnit> parents;
    private Map<String, Set<Pair<String, WorkUnit>>> links;
    private int maxNumber;
    private Date lastDownload;

    JiraXmlIssueMiner(JiraPump pump) {
        super(pump);
        parents = new HashMap<>();
        links = new HashMap<>();
        maxNumber = 0;
        lastDownload = new Date();
    }

    @Override
    protected void mineWorklogs(WorkUnit unit, String issueString) {
        unit.setEstimatedTime(secondsToHours(parseString(issueString, JiraXmlRegexes.TIME_ESTIMATE)));
        unit.setSpentTime(secondsToHours(parseString(issueString, JiraXmlRegexes.TIME_SPENT)));
    }

    private double secondsToHours(String seconds) {
        try {
            return Double.parseDouble(seconds) / JiraXmlRegexes.SECONDS_IN_HOUR;
        } catch (NumberFormatException e) {
            return 0;
        }
    }


    @Override
    protected void mineComments(WorkUnit unit, String issueString) {
        String commentsString = cutOutElement(issueString, JiraXmlRegexes.COMMENTS).trim();
        if (commentsString.isEmpty()) return;
        List<String> comments = new ArrayList<>();
        comments.addAll(Arrays.asList(commentsString.split(JiraXmlRegexes.COMMENTS_SPLIT)));
        for (String commentString : comments) {
            if (commentString.trim().isEmpty()) continue;
            String firstLine = commentString.trim().split(DataPump.LINE_BREAK)[0];
            String id = parseString(firstLine, JiraXmlRegexes.INLINE_ID);
            Person author = addPerson(generateIdentity(parseString(firstLine, JiraXmlRegexes.INLINE_AUTHOR)));
            Date created = parseDate(parseString(firstLine, JiraXmlRegexes.INLINE_CREATED));
            String body = commentString.substring(commentString.indexOf(JiraXmlRegexes.GT) + 1, commentString.lastIndexOf(String.format(JiraXmlRegexes.ELEMENT_END, JiraXmlRegexes.COMMENT).trim()));

            Configuration configuration = new Configuration();
            configuration.setExternalId(id);
            configuration.setAuthor(author);
            configuration.setCreated(created);
            configuration.setDescription(body.trim());

            WorkItemChange change = new WorkItemChange();
            change.setType(WorkItemChange.Type.COMMENT);
            change.setChangedItem(unit);

            configuration.getChanges().add(change);

            pump.getPi().getProject().getConfigurations().add(configuration);
        }
    }

    private Identity generateIdentity(String login, String name, String email) {
        Identity identity = new Identity();
        identity.setName(login);
        identity.setDescription(name);
        identity.setEmail(email);
        return identity;
    }

    private Identity generateIdentity(String login) {
        if (login == null || login.isEmpty() || login.equals("-1")) return null;
        Identity identity = new Identity();
        String[] parts = login.split(IssueMiner.PLUS);
        if (parts.length > 1) {
            identity.setName(parts[0]);
            identity.setEmail(login);
            return identity;
        }

        if (login.contains(DataPump.AT) && !login.startsWith(DataPump.AT)) {
            identity.setName(login.split(DataPump.AT)[0]);
            identity.setEmail(login);
        } else {
            identity.setName(login);
        }
        return identity;
    }

    private List<String> getIssues() {
        List<String> issueStrings = new ArrayList<>();

        File folder = new File(String.format(JiraXmlRegexes.FOLDER_NAME_FORMAT, pump.getPi().getName()));
        File[] files = folder.listFiles();
        if (files == null || files.length == 0) return issueStrings;

        for (File file : files) {
            Date modified = new Date(file.lastModified());
            if (modified.before(lastDownload)) lastDownload = new Date(file.lastModified());

            try {
                Scanner scanner = new Scanner(file, StandardCharsets.UTF_8.name()).useDelimiter(JiraXmlRegexes.DELIMITER);
                String fileString = scanner.next();
                scanner.close();

                fileString = StringEscapeUtils.unescapeXml(fileString);
                String[] fileIssueStrings = fileString.split(JiraXmlRegexes.ITEMS_SPLIT);
                for (int i = 1; i < fileIssueStrings.length; i++) {
                    issueStrings.add(fileIssueStrings[i].trim());
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return issueStrings;
    }

    @Override
    public void mineItems() {
        List<String> issues = getIssues();
        int lastMsgCount = 0, issueCount = 0;
        String stat;
        for (String issue : issues) {
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

        for (int i = maxNumber; i > 0; i--) {
            if (!pump.getPi().getProject().containsUnit(i + "")) {
                generateDeletedIssue(i);
            }
        }
    }

    @Override
    protected void mineItem(String issueString) {

        String issueKey = parseString(issueString, JiraXmlRegexes.EXTERNAL_ID);
        int issueNumber = getNumberAfterLastDash(issueKey);
        if (pump.getPi().getProject().getUnit(issueNumber) != null) return;
        if (issueNumber > maxNumber) maxNumber = issueNumber;

        WorkUnit unit = new WorkUnit();
        unit.setExternalId(issueKey);
        unit.setNumber(issueNumber);
        unit.setUrl(parseString(issueString, String.format(JiraXmlRegexes.ELEMENT_VALUE, JiraXmlRegexes.LINK, JiraXmlRegexes.LINK)));
        unit.setName(parseString(issueString, String.format(JiraXmlRegexes.ELEMENT_VALUE, JiraXmlRegexes.SUMMARY, JiraXmlRegexes.SUMMARY)));
        unit.setDescription(cutOutElement(issueString, JiraXmlRegexes.DESCRIPTION));
        unit.setAuthor(addPerson(generateIdentity(issueString, JiraXmlRegexes.REPORTER)));
        unit.setAssignee(addPerson(generateIdentity(issueString, JiraXmlRegexes.ASSIGNEE)));
        unit.setCreated(parseDate(parseString(issueString, String.format(JiraXmlRegexes.DATE, JiraXmlRegexes.CREATED, JiraXmlRegexes.CREATED))));
        unit.setStartDate(unit.getCreated());
        unit.setDueDate(parseDate(parseString(issueString, String.format(JiraXmlRegexes.DATE, JiraXmlRegexes.DUE, JiraXmlRegexes.DUE))));
        unit.setStatus(parseStatus(issueString));
        unit.setType(parseType(issueString));
        unit.setPriority(parsePriority(issueString));
        unit.setResolution(parseResolution(issueString));
        unit.setSeverity(parseSeverity(issueString));
        resolveCategories(unit, issueString);

        pump.getPi().getProject().addUnit(unit);

        mineAttachments(unit, issueString);
        mineHistory(unit, issueString);

        for (String version : parseStrings(issueString, JiraXmlRegexes.VERSIONS)) {
            if (unit.getIteration() == null || version.compareTo(unit.getIteration().getName()) > 0) {
                Iteration iteration = new Iteration();
                iteration.setName(version);
                unit.setIteration(iteration);
            }
        }

        mineRelations(unit, issueString);
    }

    private void mineAttachments(WorkUnit unit, String issueString) {
        Map<String, Map<Person, Set<WorkItemChange>>> changes = new HashMap<>();

        for (String attachmentLine : parseStrings(issueString, String.format(JiraXmlRegexes.INLINE_ELEMENT, JiraXmlRegexes.ATTACHMENT, JiraXmlRegexes.ATTACHMENT))) {
            Person author = addPerson(generateIdentity(parseString(attachmentLine, JiraXmlRegexes.INLINE_AUTHOR)));
            String dateString = parseString(attachmentLine, JiraXmlRegexes.INLINE_CREATED);
            if (!changes.containsKey(dateString)) {
                changes.put(dateString, new HashMap<>());
            }
            if (!changes.get(dateString).containsKey(author)) {
                changes.get(dateString).put(author, new LinkedHashSet<>());
            }
            Date created = parseDate(dateString);

            Artifact artifact = new Artifact();
            artifact.setArtifactClass(ArtifactClass.FILE);
            artifact.setExternalId(parseString(attachmentLine, JiraXmlRegexes.INLINE_ID));
            artifact.setName(parseString(attachmentLine, JiraXmlRegexes.ATTACHMENT_NAME));
            artifact.setMimeType(URLConnection.guessContentTypeFromName(artifact.getName()));
            if (artifact.getMimeType() == null) {
                artifact.setMimeType(artifact.getName().substring(artifact.getName().lastIndexOf(DataPump.DOT) + 1));
            }
            artifact.setSize(Long.parseLong(parseString(attachmentLine, JiraXmlRegexes.ATTACHMENT_SIZE)));
            artifact.setAuthor(author);
            artifact.setCreated(created);

            unit.getRelatedItems().add(new WorkItemRelation(artifact, resolveRelation(HAS_ATTACHED)));
            artifact.getRelatedItems().add(new WorkItemRelation(unit, resolveRelation(ATTACHED_TO)));

            WorkItemChange attachChange = new WorkItemChange();
            attachChange.setChangedItem(unit);
            attachChange.setType(WorkItemChange.Type.MODIFY);
            attachChange.setDescription(AttachmentMiner.ATTACH_CHANGE_NAME);
            changes.get(dateString).get(author).add(attachChange);

            WorkItemChange createAttachmentChange = new WorkItemChange();
            createAttachmentChange.setChangedItem(artifact);
            createAttachmentChange.setType(WorkItemChange.Type.ADD);
            changes.get(dateString).get(author).add(createAttachmentChange);
        }
        for (Map.Entry<String, Map<Person, Set<WorkItemChange>>> dateChanges : changes.entrySet()) {
            for (Map.Entry<Person, Set<WorkItemChange>> personChanges : dateChanges.getValue().entrySet()) {
                Configuration configuration = new Configuration();
                configuration.setCreated(parseDate(dateChanges.getKey()));
                configuration.setAuthor(personChanges.getKey());
                configuration.getChanges().addAll(personChanges.getValue());
                pump.getPi().getProject().getConfigurations().add(configuration);
            }
        }
    }

    private void mineRelations(WorkUnit unit, String issueString) {
        String issueLinksString = cutOutElement(issueString, JiraXmlRegexes.ISSUE_LINKS).trim();
        if (!issueLinksString.isEmpty()) {
            List<String> issueLinkTypes = new ArrayList<>();
            issueLinkTypes.addAll(Arrays.asList(issueLinksString.split(JiraXmlRegexes.ISSUE_LINK_TYPE_SPLIT)));
            issueLinkTypes.remove(0);
            for (String type : issueLinkTypes) {
                parseLinkDirection(unit, type, JiraXmlRegexes.OUTWARD_LINKS);
                parseLinkDirection(unit, type, JiraXmlRegexes.INWARD_LINKS);
            }
        }

        List<String> subTasks = parseStrings(issueString, String.format(JiraXmlRegexes.INLINE_ELEMENT, JiraXmlRegexes.SUBTASK, JiraXmlRegexes.SUBTASK));
        for (String subTaskLine : subTasks) {
            String subTask = parseString(subTaskLine, JiraXmlRegexes.INLINE_ELEMENT_VALUE);
            WorkUnit child = pump.getPi().getProject().getUnit(getNumberAfterLastDash(subTask));
            if (child != null) {
                unit.getRelatedItems().add(new WorkItemRelation(child, resolveRelation(PARENT_OF)));
                child.getRelatedItems().add(new WorkItemRelation(unit, resolveRelation(CHILD_OF)));
            } else {
                parents.put(subTask, unit);
            }
        }
        completePastRelations(unit);
    }

    private void parseLinkDirection(WorkUnit unit, String type, String directionRegex) {
        //String typeName = parseString(type.trim(), String.format(JiraXmlRegexes.ELEMENT_VALUE, JiraXmlRegexes.NAME, JiraXmlRegexes.NAME));
        String direction = parseString(type.trim(), String.format(JiraXmlRegexes.LINES_ELEMENT, directionRegex, directionRegex));
        String description = parseString(direction.trim(), JiraXmlRegexes.INLINE_DESCRIPTION);
        List<String> issueKeys = parseStrings(direction.trim(), String.format(JiraXmlRegexes.ISSUE_KEY, pump.getPi().getName()));
        for (String key : issueKeys) {
            WorkUnit related;
            if ((related = pump.getPi().getProject().getUnit(getNumberAfterLastDash(key))) != null) {
                unit.getRelatedItems().add(new WorkItemRelation(related, resolveRelation(description)));
            } else {
                saveLink(unit, key, description);
            }
        }
    }

    private void completePastRelations(WorkUnit unit) {
        String key = unit.getExternalId();
        if (links.containsKey(key)) {
            for (Pair<String, WorkUnit> relation : links.get(key)) {
                String description = relation.getKey();
                WorkUnit related = relation.getValue();
                related.getRelatedItems().add(new WorkItemRelation(unit, resolveRelation(description)));
            }
            links.remove(key);
        }
        if (parents.containsKey(key)) {
            WorkUnit parent = parents.get(key);
            parent.getRelatedItems().add(new WorkItemRelation(unit, resolveRelation(PARENT_OF)));
            unit.getRelatedItems().add(new WorkItemRelation(parent, resolveRelation(CHILD_OF)));
            parents.remove(key);
        }
    }

    private void saveLink(WorkUnit unit, String key, String description) {
        if (links.containsKey(key)) {
            Pair<String, WorkUnit> relation = new Pair<>(description, unit);
            links.get(key).add(relation);
        } else {
            Set<Pair<String, WorkUnit>> set = new LinkedHashSet<>();
            set.add(new Pair<>(description, unit));
            links.put(key, set);
        }
    }

    private Severity parseSeverity(String issueString) {
        List<String> severities = parseCustomField(issueString, JiraXmlRegexes.SEVERITY);
        if (severities.isEmpty()) return null;
        String name = severities.get(0);

        for (Severity severity : pump.getPi().getSeverities()) {
            if (toLetterOnlyLowerCase(severity.getName()).equals(toLetterOnlyLowerCase(name))) {
                severity.setName(name);
                return severity;
            }
        }

        Severity newSeverity = new Severity(name, new SeverityClassification(SeverityClass.UNASSIGNED));
        pump.getPi().getSeverities().add(newSeverity);
        return newSeverity;
    }

    private Resolution parseResolution(String issueString) {
        String resolutionLine = parseString(issueString, String.format(JiraXmlRegexes.INLINE_ELEMENT, JiraXmlRegexes.RESOLUTION, JiraXmlRegexes.RESOLUTION));
        String id = parseString(resolutionLine, JiraXmlRegexes.INLINE_ID);
        String name = parseString(resolutionLine, JiraXmlRegexes.INLINE_ELEMENT_VALUE);

        for (Resolution resolution : pump.getPi().getResolutions()) {
            if (toLetterOnlyLowerCase(resolution.getName()).equals(toLetterOnlyLowerCase(name))) {
                if (resolution.getExternalId() == null) {
                    resolution.setName(name);
                    resolution.setExternalId(id);
                }
                return resolution;
            }
        }

        Resolution newResolution = new Resolution(name, new ResolutionClassification(ResolutionClass.UNASSIGNED));
        newResolution.setExternalId(id);
        pump.getPi().getResolutions().add(newResolution);
        return newResolution;
    }

    private Priority parsePriority(String issueString) {
        String priorityLine = parseString(issueString, String.format(JiraXmlRegexes.INLINE_ELEMENT, JiraXmlRegexes.PRIORITY, JiraXmlRegexes.PRIORITY));
        String id = parseString(priorityLine, JiraXmlRegexes.INLINE_ID);
        String name = parseString(priorityLine, JiraXmlRegexes.INLINE_ELEMENT_VALUE);

        for (Priority priority : pump.getPi().getPriorities()) {
            if (toLetterOnlyLowerCase(priority.getName()).equals(toLetterOnlyLowerCase(name))) {
                if (priority.getExternalId() == null) {
                    priority.setName(name);
                    priority.setExternalId(id);
                }
                return priority;
            }
        }

        Priority newPriority = new Priority(name, new PriorityClassification(PriorityClass.UNASSIGNED));
        newPriority.setExternalId(id);
        pump.getPi().getPriorities().add(newPriority);
        return newPriority;
    }

    private WorkUnitType parseType(String issueString) {
        String typeLine = parseString(issueString, String.format(JiraXmlRegexes.INLINE_ELEMENT, JiraXmlRegexes.TYPE, JiraXmlRegexes.TYPE));
        String id = parseString(typeLine, JiraXmlRegexes.INLINE_ID);
        String name = parseString(typeLine, JiraXmlRegexes.INLINE_ELEMENT_VALUE);

        for (WorkUnitType type : pump.getPi().getWuTypes()) {
            if (toLetterOnlyLowerCase(type.getName()).equals(toLetterOnlyLowerCase(name))) {
                if (type.getExternalId() == null) {
                    type.setName(name);
                    type.setExternalId(id);
                }
                return type;
            }
        }

        WorkUnitType newType = new WorkUnitType(name, new WorkUnitTypeClassification(WorkUnitTypeClass.UNASSIGNED));
        newType.setExternalId(id);
        pump.getPi().getWuTypes().add(newType);
        return newType;
    }

    private Date parseDate(String dateString) {
        if (dateString.isEmpty()) return null;
        try {
            return JiraXmlRegexes.DATE_FORMAT.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Identity generateIdentity(String issueString, String role) {
        String login = parseString(issueString, String.format(JiraXmlRegexes.PERSON_LOGIN, role));
        Identity identity;
        if ((identity = generateIdentity(login)) == null) return null;
        login = login.replaceAll(IssueMiner.PLUS, JiraXmlRegexes.PLUS_REGEX);
        identity.setDescription(parseString(issueString, String.format(JiraXmlRegexes.PERSON_NAME, role, login, role)));
        return identity;
    }

    private String cutOutElement(String issueString, String element) {
        String elementStart = String.format(JiraXmlRegexes.ELEMENT_START, element);
        if (!issueString.contains(elementStart)) return "";
        int elementBegin = issueString.indexOf(elementStart) + elementStart.length();
        int elementEnd = issueString.lastIndexOf(String.format(JiraXmlRegexes.ELEMENT_END, element));
        return issueString.substring(elementBegin, elementEnd);
    }

    private String parseString(String text, String regex) {
        List<String> finds = parseStrings(text, regex);
        if (!finds.isEmpty()) return finds.get(0);
        return "";
    }

    private List<String> parseStrings(String text, String regex) {
        List<String> finds = new ArrayList<>();
        if (text == null) return finds;

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            finds.add(matcher.group());
            text = text.substring(matcher.end());
            matcher = matcher.reset(text);
        }

        return finds;
    }

    private Status parseStatus(String issueString) {

        String statusLine = parseString(issueString, String.format(JiraXmlRegexes.INLINE_ELEMENT, JiraXmlRegexes.STATUS, JiraXmlRegexes.STATUS));
        String id = parseString(statusLine, JiraXmlRegexes.INLINE_ID);
        String name = parseString(statusLine, JiraXmlRegexes.INLINE_ELEMENT_VALUE);
        String description = parseString(statusLine, JiraXmlRegexes.INLINE_DESCRIPTION);

        for (Status status : pump.getPi().getStatuses()) {
            if (toLetterOnlyLowerCase(status.getName()).equals(toLetterOnlyLowerCase(name))) {
                if (status.getExternalId() == null) {
                    status.setName(name);
                    status.setExternalId(id);
                    status.setDescription(description);
                }
                return status;
            }
        }

        StatusClass aClass;
        switch (JiraEnumsMiner.Status.valueOf(parseString(issueString, JiraXmlRegexes.STATUS_CLASS).toUpperCase())) {
            case NEW:
                aClass = StatusClass.NEW;
                break;
            case DONE:
                aClass = StatusClass.DONE;
                break;
            case INDETERMINATE:
                aClass = StatusClass.INPROGRESS;
                break;
            default:
                aClass = StatusClass.UNASSIGNED;
                break;
        }

        Status newStatus = new Status(name, new StatusClassification(aClass));
        newStatus.setExternalId(id);
        newStatus.setDescription(description);
        pump.getPi().getStatuses().add(newStatus);
        return newStatus;
    }

    @Override
    protected void resolveCategories(WorkUnit unit, String issueString) {
        for (String component : parseStrings(issueString, String.format(JiraXmlRegexes.ELEMENT_VALUE, JiraXmlRegexes.COMPONENT, JiraXmlRegexes.COMPONENT))) {
            addCategory(unit, component);
        }

        for (String label : parseStrings(issueString, String.format(JiraXmlRegexes.ELEMENT_VALUE, JiraXmlRegexes.LABEL, JiraXmlRegexes.LABEL))) {
            addCategory(unit, label);
        }

        for (String tag : parseCustomField(issueString, JiraXmlRegexes.TAG)) {
            addCategory(unit, tag);
        }
    }

    private List<String> parseCustomField(String issueString, String customFieldName) {
        List<String> fieldValues = new ArrayList<>();

        String customFields = cutOutElement(issueString, JiraXmlRegexes.CUSTOM_FIELDS).trim();
        List<String> fields = new ArrayList<>();
        fields.addAll(Arrays.asList(customFields.split(JiraXmlRegexes.CUSTOM_FIELDS_SPLIT)));
        fields.remove(0);
        for (String fieldString : fields) {
            String name = parseString(fieldString, String.format(JiraXmlRegexes.ELEMENT_VALUE, JiraXmlRegexes.CUSTOM_FIELD_NAME, JiraXmlRegexes.CUSTOM_FIELD_NAME));
            if (name.toLowerCase().contains(customFieldName)) {
                String valuesString = cutOutElement(fieldString, JiraXmlRegexes.CUSTOM_FIELD_VALUES);
                if (valuesString.contains(JiraXmlRegexes.CUSTOM_FIELD_VALUE)) {
                    fieldValues.addAll(parseStrings(valuesString, String.format(JiraXmlRegexes.ELEMENT_VALUE, JiraXmlRegexes.CUSTOM_FIELD_VALUE, JiraXmlRegexes.CUSTOM_FIELD_VALUE)));
                } else {
                    fieldValues.addAll(Arrays.asList(valuesString.split(DataPump.SPACE)));
                }
            }
        }

        return fieldValues;
    }

    private void addCategory(WorkUnit unit, String name) {
        Category category;
        if ((category = resolveCategory(name)) != null) {
            unit.getCategories().add(category);
        }
    }

    private Category resolveCategory(String label) {
        if (!label.trim().isEmpty()) {
            for (Category category : pump.getPi().getCategories()) {
                if (toLetterOnlyLowerCase(category.getName()).equals(toLetterOnlyLowerCase(label))) {
                    return category;
                }
            }
            Category newCategory = new Category();
            newCategory.setName(label);
            pump.getPi().getCategories().add(newCategory);
            return newCategory;
        }
        return null;
    }

    @Override
    protected void mineHistory(WorkUnit unit, String issueString) {
        super.mineHistory(unit, issueString);

        Date updated = parseDate(parseString(issueString, String.format(JiraXmlRegexes.DATE, JiraXmlRegexes.UPDATED, JiraXmlRegexes.UPDATED)));
        if (updated == null || !unit.getCreated().before(updated)) return;

        String uri = String.format(JiraXmlRegexes.CHANGELOG_URL_FORMAT, pump.getServer(), parseString(issueString, JiraXmlRegexes.KEY_ID));
        String jsonString = null;
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(new URL(uri).openConnection().getInputStream()));
            jsonString = in.readLine();
        } catch (IOException e) {
            if (e instanceof FileNotFoundException) return;
            e.printStackTrace();
        }
        if (jsonString == null) return;

        JSONArray histories = new JSONObject(jsonString).getJSONObject(JiraXmlRegexes.CHANGELOG).getJSONArray(JiraXmlRegexes.HISTORIES);
        parseChangelog(unit, histories);
    }

    private void parseChangelog(WorkUnit unit, JSONArray histories) {
        for (int i = 0; i < histories.length(); i++) {

            Date created = null;
            try {
                created = JiraXmlRegexes.JSON_DATE_FORMAT.parse(histories.getJSONObject(i).getString(JiraXmlRegexes.CREATED));
                if (created.after(lastDownload)) continue;
            } catch (ParseException e) {
                e.printStackTrace();
            }

            String id = histories.getJSONObject(i).getString(JiraXmlRegexes.ID);

            Person authorPerson;
            try {
                JSONObject author = histories.getJSONObject(i).getJSONObject(JiraXmlRegexes.AUTHOR);
                String name = author.getString(JiraXmlRegexes.NAME);
                String displayName = author.getString(JiraXmlRegexes.DISPLAY_NAME);
                String emailAddress = author.getString(JiraXmlRegexes.EMAIL_ADDRESS).replaceAll(JiraPeopleMiner.AT_STRING, DataPump.AT).replaceAll(JiraPeopleMiner.DOT_STRING, DataPump.DOT);
                authorPerson = addPerson(generateIdentity(name, displayName, emailAddress));
            } catch (JSONException e) {
                authorPerson = null;
            }

            Configuration modification = generateModification(unit, id, authorPerson, created);

            JSONArray items = histories.getJSONObject(i).getJSONArray(JiraXmlRegexes.ITEMS);
            for (int j = 0; j < items.length(); j++) {
                String field = items.getJSONObject(j).get(JiraXmlRegexes.FIELD).toString();
                String fromString = items.getJSONObject(j).get(JiraXmlRegexes.FROM_STRING).toString();
                if (fromString.equals(JiraXmlRegexes.NULL)) fromString = "";
                String toString = items.getJSONObject(j).get(JiraXmlRegexes.TO_STRING).toString();
                if (toString.equals(JiraXmlRegexes.NULL)) toString = "";
                if (field.equals(JiraXmlRegexes.TIMESPENT)) {
                    generateWorklog(unit, id, authorPerson, created, fromString, toString);
                }
                else if (!field.toLowerCase().equals(JiraXmlRegexes.ATTACHMENT)) {
                    modification.getChanges().get(0).getFieldChanges().add(new FieldChange(field, fromString, toString));
                }
            }

            // add only those with field changes (not pure worklogs, which at this point should have no field changes)
            if (modification.getChanges().get(0).getFieldChanges().isEmpty()) {
                pump.getPi().getProject().getConfigurations().add(modification);
            }
        }
    }

    private void generateWorklog(WorkUnit unit, String id, Person author, Date created, String fromString, String toString) {
        WorkItemChange change = new WorkItemChange();
        change.setChangedItem(unit);
        change.setType(WorkItemChange.Type.LOGTIME);

        CommittedConfiguration worklog = new CommittedConfiguration();
        worklog.setExternalId(id);
        worklog.setAuthor(author);
        worklog.setCreated(created);
        worklog.setCommitted(created);
        worklog.getChanges().add(change);

        if (fromString.isEmpty()) fromString = JiraXmlRegexes.ZERO;
        change.getFieldChanges().add(new FieldChange(WorklogMiner.LOGTIME_FIELD_NAME, recalculate(fromString), recalculate(toString)));

        pump.getPi().getProject().getConfigurations().add(worklog);
    }

    private String recalculate(String text) {
        if (text.contains(JiraXmlRegexes.HOUR)) {
            text = text.split(DataPump.SPACE)[0].trim();
        } else if (text.contains(JiraXmlRegexes.MINUTE)) {
            text = text.split(DataPump.SPACE)[0].trim();
            text = Double.toString(Double.parseDouble(text) / MINUTES_IN_HOUR);
        } else {
            text = Double.toString(Double.parseDouble(text) / JiraXmlRegexes.SECONDS_IN_HOUR);
        }
        return text;
    }

    private Configuration generateModification(WorkUnit unit, String id, Person author, Date created) {
        WorkItemChange change = new WorkItemChange();
        change.setChangedItem(unit);
        change.setType(WorkItemChange.Type.MODIFY);

        Configuration modification = new Configuration();
        modification.setExternalId(id);
        modification.setAuthor(author);
        modification.setCreated(created);
        modification.getChanges().add(change);

        return modification;
    }
}
