package cz.zcu.kiv.spade.pumps.impl;

import com.atlassian.jira.rest.client.JiraRestClient;
import com.atlassian.jira.rest.client.domain.*;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import cz.zcu.kiv.spade.domain.*;
import cz.zcu.kiv.spade.domain.Priority;
import cz.zcu.kiv.spade.domain.Project;
import cz.zcu.kiv.spade.domain.Resolution;
import cz.zcu.kiv.spade.domain.Status;
import cz.zcu.kiv.spade.domain.abstracts.ProjectSegment;
import cz.zcu.kiv.spade.domain.enums.*;
import cz.zcu.kiv.spade.load.DBInitializer;
import cz.zcu.kiv.spade.pumps.abstracts.IssueTrackingPump;

import javax.persistence.EntityManager;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.ExecutionException;

public class JiraPump extends IssueTrackingPump<JiraRestClient> {

    /**  a representation of a Jira project from jira-api */
    private com.atlassian.jira.rest.client.domain.Project jiraProject;

    /**
     * constructor, sets projects URL and login credentials
     *
     * @param projectHandle URL of the project instance
     * @param privateKeyLoc private key location for authenticated login
     * @param username      username for authenticated login
     * @param password      password for authenticated login
     */
    public JiraPump(String projectHandle, String privateKeyLoc, String username, String password) {
        super(projectHandle, privateKeyLoc, username, password);
        this.tool = Tool.JIRA;
    }

    @Override
    public ProjectInstance mineData(EntityManager em) {

        pi = super.mineData(em);

        setToolInstance();

        try {
            jiraProject = rootObject.getProjectClient().getProject(pi.getName()).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        Project project = new Project();
        project.setName(jiraProject.getName());
        project.setDescription(jiraProject.getDescription());

        pi.setExternalId(jiraProject.getSelf().toString());
        pi.setProject(project);

        new DBInitializer(em).setDefaultEnums(pi);
        mineEnums();
        mineCategories();
        minePeople();
        Collection<ProjectSegment> iterations = mineIterations();

        mineTickets();

        for (WorkUnit unit : pi.getProject().getUnits()) {
            for (ProjectSegment iteration : iterations) {
                if (unit.getIteration() != null &&
                        unit.getIteration().getExternalId().equals(iteration.getExternalId())) {
                    if (iteration instanceof Iteration) {
                        Iteration i = (Iteration) iteration;
                        unit.setIteration(i);
                        if (unit.getDueDate() == null) unit.setDueDate(iteration.getEndDate());
                    }
                    if (iteration instanceof Phase) {
                        Phase phase = (Phase) iteration;
                        unit.setPhase(phase);
                    }
                    if (iteration instanceof  Activity) {
                        Activity activity = (Activity) iteration;
                        unit.setActivity(activity);
                    }
                }
            }
        }

        mineAllRelations();
        finalTouches();

        return pi;
    }

    private void mineAllRelations() {
        for (WorkUnit unit : pi.getProject().getUnits()) {
            Issue issue = null;
            try {
                issue = rootObject.getIssueClient().getIssue(unit.getExternalId()).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                System.out.println("issue " + unit.getExternalId() + " does not exist");
                continue;
            }
            if (issue != null) mineRelations(unit, issue);
        }
    }

    private void mineCategories() {
        Iterable<BasicComponent> components = jiraProject.getComponents();
        for (BasicComponent component : components) {
            Category category = new Category();
            category.setExternalId(component.getId() + "");
            category.setName(component.getName());
            category.setDescription(component.getDescription());
            pi.getCategories().add(category);
        }
    }

    @Override
    public void mineTickets() {

        int index = 0;
        String query = "project = " + pi.getName();
        Iterable<BasicIssue> issues = null;
        try {
            issues = rootObject.getSearchClient().searchJql(query, 1, 0).get().getIssues();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        if (issues == null) return;

        for (BasicIssue latestIssue : issues) {
            index = getNumberFromKey(latestIssue.getKey());
            if (index > 0) break;
        }

        index += 1;
        while (true) {
            Issue issue = null;
            index--;
            try {
                issue = rootObject.getIssueClient().getIssue(pi.getName() + "-" + index).get();
                if (index <= 0) break;
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                System.out.println("issue " + pi.getName() + "-" + index + " does not exist");
                continue;
            }
            mineTicket(issue);
        }
    }

    private int getNumberFromKey(String key) {
        return Integer.parseInt(key.substring(key.lastIndexOf("-") + 1));
    }

    private void mineTicket(Issue issue) {
        WorkUnit unit = new WorkUnit();
        unit.setNumber(getNumberFromKey(issue.getKey()));
        System.out.println(unit.getNumber());
        unit.setExternalId(issue.getKey());
        unit.setUrl("https://" + getServer() + "/jira/browse/" + issue.getKey());
        unit.setName(issue.getSummary());
        unit.setDescription(issue.getDescription());
        unit.setAuthor(addPerson(generateIdentity(issue.getReporter())));
        unit.setCreated(issue.getCreationDate().toDate());
        unit.setStartDate(issue.getCreationDate().toDate());
        if (issue.getDueDate() != null) unit.setDueDate(issue.getDueDate().toDate());
        unit.setAssignee(addPerson(generateIdentity(issue.getAssignee())));
        unit.setStatus(resolveStatus(issue.getStatus()));
        unit.setType(resolveType(issue.getIssueType().getName()));
        if (issue.getPriority() != null) unit.setPriority(resolvePriorty(issue.getPriority().getName()));
        if (issue.getResolution() != null) unit.setResolution(resolveResolution(issue.getResolution().getName()));
        unit.getCategories().addAll(resolveCategories(issue));
        unit.setSeverity(resolveSeverity(issue));

        pi.getProject().addUnit(unit);

        mineTimeTracking(unit, issue);
        if (issue.getAttachments() != null) mineAttachments(unit, issue.getAttachments());
        if (issue.getChangelog() != null) mineHistory(unit, issue.getChangelog());
        mineWorklogs(unit, issue.getWorklogs());
        mineComments(unit, issue.getComments());

        if (issue.getFixVersions() != null) {
            for (Version version : issue.getFixVersions()) {
                Iteration iteration = new Iteration();
                iteration.setExternalId(version.getId() + "");
                if (version.getReleaseDate() != null) {
                    iteration.setEndDate(version.getReleaseDate().toDate());
                }
                if ((unit.getIteration() == null || unit.getIteration().getEndDate() == null) ||
                        (version.getReleaseDate() != null && unit.getIteration().getEndDate().after(version.getReleaseDate().toDate()))){
                    unit.setIteration(iteration);
                }
            }
        }

        generateCreationConfig(unit);
        mineTransitions(unit, issue);

        issue.getExpandos();
    }

    private void mineTransitions(WorkUnit unit, Issue issue) {
        Iterable<Transition> transitions = new ArrayList<>();
        try {
            transitions = rootObject.getIssueClient().getTransitions(issue).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        for (Transition transition : transitions) {
            generateTransitionConfig(unit, transition);
        }
    }

    private void generateTransitionConfig(WorkUnit unit, Transition transition) {
        WorkItemChange change = new WorkItemChange();
        change.setName("MODIFY");
        change.setDescription("transition");
        change.setChangedItem(unit);

        System.out.println("transition: " + transition.toString());
        //change.getFieldChanges().add(new FieldChange("status", , ));

        Configuration configuration = new Configuration();
        //configuration.setCreated();
        configuration.getChanges().add(change);

        pi.getProject().getConfigurations().add(configuration);
    }

    private void generateCreationConfig(WorkUnit unit) {
        WorkItemChange change = new WorkItemChange();
        change.setName("ADD");
        change.setDescription("issue added");
        change.setChangedItem(unit);

        Configuration creation = new Configuration();
        creation.setCreated(unit.getCreated());
        creation.setAuthor(unit.getAuthor());
        creation.getChanges().add(change);

        pi.getProject().getConfigurations().add(creation);
    }

    private void mineRelations(WorkUnit unit, Issue issue) {
        if (issue.getIssueLinks() != null) {
            for (IssueLink link : issue.getIssueLinks()) {
                WorkUnit related = pi.getProject().getUnit(link.getTargetIssueKey());
                unit.getRelatedItems().add(new WorkItemRelation(related, resolveRelation(link.getIssueLinkType().getName())));
            }
        }
        if (issue.getSubtasks() != null) {
            for (Subtask subtask : issue.getSubtasks()) {
                WorkUnit child = pi.getProject().getUnit(getNumberFromKey(subtask.getIssueKey()));
                System.out.println("parent-child: " + unit.getNumber() + " " + child.getNumber());
                unit.getRelatedItems().add(new WorkItemRelation(child, resolveRelation("parent of")));
                child.getRelatedItems().add(new WorkItemRelation(unit, resolveRelation("child of")));
            }
        }
    }

    private void mineComments(WorkUnit unit, Iterable<Comment> comments) {
        for (Comment comment : comments) {
            pi.getProject().getConfigurations().add(generateUnitCommentConfig(unit, comment));
        }
    }

    private Configuration generateUnitCommentConfig(WorkUnit unit, Comment comment) {
        WorkItemChange change = new WorkItemChange();
        change.setName("COMMENT");
        change.setDescription("comment added");
        change.setChangedItem(unit);

        Configuration configuration = new Configuration();
        configuration.setExternalId(comment.getId() + "");
        configuration.setDescription(comment.getBody().trim());
        configuration.setAuthor(addPerson(generateIdentity(comment.getAuthor())));
        configuration.setCreated(comment.getCreationDate().toDate());
        configuration.getChanges().add(change);

        configuration.getRelatedItems().addAll(unit.getRelatedItems());

        return configuration;
    }

    private void mineWorklogs(WorkUnit unit, Iterable<Worklog> worklogs) {
        double spentTime = 0;
        for (Worklog worklog : worklogs) {
            generateLogTimeConfiguration(unit, spentTime, worklog);
            spentTime += minutesToHours(worklog.getMinutesSpent());
        }
    }

    private void generateLogTimeConfiguration(WorkUnit unit, double spentTimeBefore, Worklog worklog) {
        CommittedConfiguration configuration = new CommittedConfiguration();
        configuration.setExternalId(worklog.getSelf().toString());
        configuration.setAuthor(addPerson(generateIdentity(worklog.getAuthor())));
        configuration.setDescription(worklog.getComment());
        configuration.setCommitted(worklog.getCreationDate().toDate());
        configuration.setCreated(worklog.getStartDate().toDate());

        WorkItemChange change = new WorkItemChange();
        change.setChangedItem(unit);
        change.setName("LOGTIME");
        change.setDescription("spent time reported");

        FieldChange fieldChange = new FieldChange();
        fieldChange.setName("spentTime");
        fieldChange.setOldValue(spentTimeBefore + "");
        fieldChange.setNewValue(Double.toString(spentTimeBefore + minutesToHours(worklog.getMinutesSpent())));

        change.getFieldChanges().add(fieldChange);
        configuration.getChanges().add(change);

        pi.getProject().getConfigurations().add(configuration);
    }

    private void mineHistory(WorkUnit unit, Iterable<ChangelogGroup> changelogs) {
        Collection<Configuration> configurations = new LinkedHashSet<>();
        for (ChangelogGroup changelog : changelogs) {
            Configuration configuration = new Configuration();
            configuration.setAuthor(addPerson(generateIdentity(changelog.getAuthor())));
            configuration.setCreated(changelog.getCreated().toDate());
            WorkItemChange change = new WorkItemChange();
            change.setChangedItem(unit);
            change.setName("MODIFY");
            change.setFieldChanges(mineChanges(changelog.getItems()));

            configurations.add(configuration);
        }
        pi.getProject().getConfigurations().addAll(configurations);
    }

    private Collection<FieldChange> mineChanges(Iterable<ChangelogItem> items) {
        List<FieldChange> changes = new ArrayList<>();
        for (ChangelogItem item : items) {
            FieldChange change = new FieldChange();
            change.setName(item.getField());
            change.setNewValue(item.getToString());
            change.setOldValue(item.getFromString());
            System.out.println("history: " + change.getName() + ": " + change.getOldValue() + " -> " + change.getNewValue());
            changes.add(change);
        }
        return changes;
    }

    private void mineAttachments(WorkItem item, Iterable<Attachment> attachments) {
        for (Attachment attachment : attachments) {
            Artifact artifact = new Artifact();
            artifact.setArtifactClass(ArtifactClass.FILE);
            artifact.setMimeType(attachment.getMimeType());
            artifact.setUrl(attachment.getContentUri().toString());
            artifact.setAuthor(addPerson(generateIdentity(attachment.getAuthor())));
            artifact.setCreated(attachment.getCreationDate().toDate());
            artifact.setExternalId(attachment.getSelf().toString());
            artifact.setName(attachment.getFilename());
            artifact.setSize(attachment.getSize());

            WorkItemChange change = new WorkItemChange();
            change.setChangedItem(artifact);
            change.setName("ADD");
            change.setDescription("attachment added");

            Configuration configuration = new Configuration();
            configuration.setCreated(attachment.getCreationDate().toDate());
            configuration.setAuthor(artifact.getAuthor());
            configuration.getChanges().add(change);

            item.getRelatedItems().add(new WorkItemRelation(artifact, resolveRelation("has attached")));
            artifact.getRelatedItems().add(new WorkItemRelation(item, resolveRelation("attached to")));

            pi.getProject().getConfigurations().add(configuration);
        }
    }

    private Severity resolveSeverity(Issue issue) {
        Field field = issue.getFieldByName("Severity");
        if (field != null && field.getValue() != null) {
            String value = field.getValue().toString().trim();
            if (!value.isEmpty()) {
                for (Severity severity : pi.getSeverities()) {
                    if (toLetterOnlyLowerCase(value).equals(toLetterOnlyLowerCase(severity.getName()))) {
                        return severity;
                    }
                }
                return new Severity(value, severityDao.findByClass(SeverityClass.UNASSIGNED));
            }
        }
        return null;
    }

    private Collection<Category> resolveCategories(Issue issue) {
        Collection<Category> categories = new LinkedHashSet<>();

        for (BasicComponent component : issue.getComponents()) {
            for (Category category : pi.getCategories()) {
                if (category.getName().equals(component.getName())) {
                    categories.add(category);
                    break;
                }
            }
        }

        for (String label : issue.getLabels()) {
            Category fromLabel = resolveCategory(label);
            if (fromLabel != null) categories.add(fromLabel);
        }

        Field tags = issue.getFieldByName("Tags");
        if (tags != null) {
            for (String tag : tags.getValue().toString().split(" ")) {
                Category fromTag = resolveCategory(tag);
                System.out.println(tag);
                if (fromTag != null) categories.add(fromTag);
            }
        }

        return categories;
    }

    private Category resolveCategory(String label) {
        if (!label.trim().isEmpty()) {
            for (Category category : pi.getCategories()) {
                if (category.getName().equals(label)) {
                    return category;
                }
            }
            Category newCategory = new Category();
            newCategory.setName(label);
            return newCategory;
        }
        return null;
    }

    private void mineTimeTracking(WorkUnit unit, Issue issue) {
        if (issue.getTimeTracking() != null) {
            if (issue.getTimeTracking().getOriginalEstimateMinutes() != null) {
                unit.setEstimatedTime(minutesToHours(issue.getTimeTracking().getOriginalEstimateMinutes()));
            }
            if (issue.getTimeTracking().getTimeSpentMinutes() != null) {
                unit.setSpentTime(minutesToHours(issue.getTimeTracking().getTimeSpentMinutes()));
            }
            if (unit.getEstimatedTime() > 0) {
                int percentage = (int) (unit.getEstimatedTime() / unit.getSpentTime()) * 100;
                unit.setProgress(Math.min(percentage, 100));
            }
        }
    }

    private double minutesToHours(Integer minutes) {
        return minutes / 60.0;
    }

    private Resolution resolveResolution(String name) {
        for (Resolution resolution : pi.getResolutions()) {
            if (name.equals(resolution.getName())) {
                return resolution;
            }
        }
        return null;
    }

    private Priority resolvePriorty(String name) {
        for (Priority priority : pi.getPriorities()) {
            if (name.equals(priority.getName())) {
                return priority;
            }
        }
        return null;
    }

    private WorkUnitType resolveType(String name) {
        for (WorkUnitType type : pi.getWuTypes()) {
            if (name.equals(type.getName())) {
                return type;
            }
        }
        return null;
    }

    @Override
    public Collection<ProjectSegment> mineIterations() {
        Collection<ProjectSegment> iterations = new LinkedHashSet<>();
            for (Version version : jiraProject.getVersions()) {
                Iteration iteration = new Iteration();
                iteration.setProject(pi.getProject());
                iteration.setExternalId(version.getId() + "");
                iteration.setName(version.getName());
                iteration.setDescription(version.getDescription());
                if (version.getReleaseDate() != null) iteration.setEndDate(version.getReleaseDate().toDate());

                Phase phase = new Phase();
                phase.setProject(iteration.getProject());
                phase.setExternalId(iteration.getExternalId());
                phase.setName(iteration.getName());
                phase.setDescription(iteration.getDescription());
                phase.setEndDate(iteration.getEndDate());

                Activity activity = new Activity();
                activity.setProject(iteration.getProject());
                activity.setExternalId(iteration.getExternalId());
                activity.setName(iteration.getName());
                activity.setDescription(iteration.getDescription());
                activity.setEndDate(iteration.getEndDate());

                iterations.add(phase);
                iterations.add(activity);
                iterations.add(iteration);
            }
        return iterations;
    }

    @Override
    public void mineEnums() {
        mineWUTypes();
        minePriorities();
        mineResolutions();
        mineWURelationTypes();
        mineRoles();
    }

    private Status resolveStatus(BasicStatus basicStatus) {
        com.atlassian.jira.rest.client.domain.Status jiraStatus = null;
        try {
            jiraStatus = rootObject.getMetadataClient().getStatus(basicStatus.getSelf()).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        if (jiraStatus == null) return null;

        for (Status status : pi.getStatuses()) {
            if (toLetterOnlyLowerCase(status.getName()).equals(toLetterOnlyLowerCase(jiraStatus.getName()))) {
                if (status.getExternalId() == null) {
                    status.setName(jiraStatus.getName());
                    status.setExternalId(jiraStatus.getSelf().toString());
                    status.setDescription(jiraStatus.getDescription());
                }
                return status;
            }
        }

        Status newStatus = new Status(jiraStatus.getName(), statusDao.findByClass(StatusClass.UNASSIGNED));
        newStatus.setExternalId(jiraStatus.getSelf().toString());
        newStatus.setDescription(jiraStatus.getDescription());
        pi.getStatuses().add(newStatus);
        return newStatus;
    }

    private void mineWURelationTypes() {
        Iterable<IssuelinksType> types = new ArrayList<>();
        try {
            types = rootObject.getMetadataClient().getIssueLinkTypes().get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        for (IssuelinksType linksType : types) {
            boolean found = false;
            for (Relation relation : pi.getRelations()) {
                if (toLetterOnlyLowerCase(relation.getName()).equals(toLetterOnlyLowerCase(linksType.getName()))) {
                    relation.setName(linksType.getName());
                    relation.setExternalId(linksType.getId() + "");
                    relation.setDescription(linksType.getInward() + " / " + linksType.getOutward());
                    found = true;
                    break;
                }
            }
            if (!found) {
                Relation newRelation = new Relation(linksType.getName(), relationDao.findByClass(RelationClass.UNASSIGNED));
                newRelation.setExternalId(linksType.getId() + "");
                newRelation.setDescription(linksType.getInward() + " / " + linksType.getOutward());
                pi.getRelations().add(newRelation);
            }
        }
    }

    private void mineResolutions() {
        Iterable<com.atlassian.jira.rest.client.domain.Resolution> resolutions = new ArrayList<>();
        try {
            resolutions = rootObject.getMetadataClient().getResolutions().get();

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        for (com.atlassian.jira.rest.client.domain.Resolution jiraResolution : resolutions) {
            boolean found = false;
            for (Resolution resolution : pi.getResolutions()) {
                if (toLetterOnlyLowerCase(resolution.getName()).equals(toLetterOnlyLowerCase(jiraResolution.getName()))) {
                    resolution.setName(jiraResolution.getName());
                    resolution.setExternalId(jiraResolution.getSelf().toString());
                    resolution.setDescription(jiraResolution.getDescription());
                    found = true;
                    break;
                }
            }
            if (!found) {
                Resolution newResolution = new Resolution(jiraResolution.getName(), resolutionDao.findByClass(ResolutionClass.UNASSIGNED));
                newResolution.setExternalId(jiraResolution.getSelf().toString());
                newResolution.setDescription(jiraResolution.getDescription());
                pi.getResolutions().add(newResolution);
            }
        }
    }

    private void mineRoles() {
        /*Iterable<BasicProjectRole> roles = jiraProject.getProjectRoles();

        for (BasicProjectRole projectRole : roles) {
            boolean found = false;
            for (Role role : pi.getRoles()) {
                if (toLetterOnlyLowerCase(role.getName()).equals(toLetterOnlyLowerCase(projectRole.getName()))) {
                    role.setName(projectRole.getName());
                    //role.setExternalId(projectRole.getId() + "");
                    //role.setDescription(projectRole.getDescription());
                    found = true;
                    break;
                }
            }
            if (!found) {
                Role newRole = new Role(projectRole.getName(), roleDao.findByClass(RoleClass.UNASSIGNED));
                //newRole.setExternalId(projectRole.getId() + "");
                //newRole.setDescription(projectRole.getDescription());
                pi.getRoles().add(newRole);
            }
        }*/
    }

    private void minePriorities() {
        Iterable<com.atlassian.jira.rest.client.domain.Priority> priorities = new ArrayList<>();
        try {
            priorities = rootObject.getMetadataClient().getPriorities().get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        for (com.atlassian.jira.rest.client.domain.Priority issuePriority : priorities) {
            boolean found = false;
            for (Priority priority : pi.getPriorities()) {
                if (toLetterOnlyLowerCase(priority.getName()).equals(toLetterOnlyLowerCase(issuePriority.getName()))) {
                    priority.setName(issuePriority.getName());
                    priority.setExternalId(issuePriority.getId() + "");
                    priority.setDescription(issuePriority.getDescription() + "\nStatus color: " + issuePriority.getStatusColor());
                    found = true;
                    break;
                }
            }
            if (!found) {
                Priority newPriority = new Priority(issuePriority.getName(), priorityDao.findByClass(PriorityClass.UNASSIGNED));
                newPriority.setExternalId(issuePriority.getId() + "");
                newPriority.setDescription(issuePriority.getDescription() + "\nStatus color: " + issuePriority.getStatusColor());
                pi.getPriorities().add(newPriority);
            }
        }
    }

    private void mineWUTypes() {
        for (IssueType issueType : jiraProject.getIssueTypes()) {
            boolean found = false;
            for (WorkUnitType type : pi.getWuTypes()) {
                if (toLetterOnlyLowerCase(type.getName()).equals(toLetterOnlyLowerCase(issueType.getName()))) {
                    type.setName(issueType.getName());
                    type.setExternalId(issueType.getId() + "");
                    type.setDescription(issueType.getDescription());
                    found = true;
                    break;
                }
            }
            if (!found) {
                WorkUnitType newType = new WorkUnitType(issueType.getName(), typeDao.findByClass(WorkUnitTypeClass.UNASSIGNED));
                newType.setExternalId(issueType.getId() + "");
                newType.setDescription(issueType.getDescription());
                pi.getWuTypes().add(newType);
            }
        }
    }

    @Override
    protected JiraRestClient init() {
        URI jiraServerUri = null;
        try {
            jiraServerUri = new URI("https://" + getServer() + "/" + Tool.JIRA.name().toLowerCase());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        if (jiraServerUri != null) {
            return new AsynchronousJiraRestClientFactory().createWithBasicHttpAuthentication(jiraServerUri, username, password);
        }

        return null;
    }

    @Override
    protected void mineMentions() {
        //TODO particular regex for Jira issues
        // from work unit descriptions
        for (WorkUnit unit : pi.getProject().getUnits()) {
            mineAllMentionedItems(unit);
        }

        for (Configuration configuration : pi.getProject().getConfigurations()) {
            if (!(configuration instanceof Commit || configuration instanceof CommittedConfiguration)) {
                for (WorkItemChange change : configuration.getChanges()) {
                    // from work unit commnets and logtimes
                    if (change.getChangedItem() instanceof WorkUnit &&
                            (change.getName().equals("COMMENT") || change.getName().equals("LOGTIME"))) {
                        mineAllMentionedItems(change.getChangedItem(), configuration.getDescription());
                    }
                }
            }
        }
    }

    private Identity generateIdentity(BasicUser basicUser) {
        Identity identity = new Identity();
        if (basicUser == null) {
            identity.setName("unknown");
            return identity;
        }
        try {
            User user = rootObject.getUserClient().getUser(basicUser.getName()).get();
            identity.setExternalId(user.getSelf().toString());
            identity.setName(user.getName());
            identity.setDescription(user.getDisplayName());

            String email = user.getEmailAddress().replace(" at ", "@");
            email = email.replace(" dot ", ".");
            identity.setEmail(email);

        } catch (InterruptedException | ExecutionException e) {
            System.out.println("\tInsufficient permissions for user: " + basicUser.getName());
            identity.setExternalId(basicUser.getSelf().toString());
            identity.setName(basicUser.getName());
            identity.setDescription(basicUser.getDisplayName());
        }
        return identity;
    }

    private void minePeople() {
        Person person = addPerson(generateIdentity(jiraProject.getLead()));
        try {
            User user = rootObject.getUserClient().getUser(jiraProject.getLead().getName()).get();
            if (user.getGroups().getItems() != null) {
                for (String name : user.getGroups().getItems()) {
                    Group group = new Group();
                    group.setName(name);
                    group.getMembers().add(person);
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            System.out.println("\tInsufficient permissions for user: " + jiraProject.getLead().getName());
        }
        person.getRoles().add(getProjectLeadRole());
    }

    private Role getProjectLeadRole() {
        Role projectLeadRole = resolveRole();
        if (projectLeadRole == null) {
            projectLeadRole = new Role("project lead", roleDao.findByClass(RoleClass.PROJECTMANAGER));
            pi.getRoles().add(projectLeadRole);
        }
        return projectLeadRole;
    }

    private Role resolveRole() {
        for (Role role : pi.getRoles()) {
            if (role.getName().equals("project lead")) {
                return role;
            }
        }
        return null;
    }
}
