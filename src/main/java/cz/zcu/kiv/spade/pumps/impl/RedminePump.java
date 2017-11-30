package cz.zcu.kiv.spade.pumps.impl;

import com.taskadapter.redmineapi.*;
import com.taskadapter.redmineapi.bean.*;
import cz.zcu.kiv.spade.domain.*;
import cz.zcu.kiv.spade.domain.Group;
import cz.zcu.kiv.spade.domain.Project;
import cz.zcu.kiv.spade.domain.Role;
import cz.zcu.kiv.spade.domain.abstracts.ProjectSegment;
import cz.zcu.kiv.spade.domain.enums.*;
import cz.zcu.kiv.spade.pumps.abstracts.IssueTrackingPump;

import javax.persistence.EntityManager;
import java.util.*;

/**
 * a pump for mining Redmine data
 *
 * @author Petr Pícha
 */
public class RedminePump extends IssueTrackingPump<RedmineManager> {

    /**  a representation of a Redmine project from taskadapter.redmineapi */
    private com.taskadapter.redmineapi.bean.Project redmineProject;

    /**
     * constructor, sets projects URL and login credentials
     *
     * @param projectHandle URL of the project instance
     * @param privateKeyLoc private key location for authenticated login
     * @param username      username for authenticated login
     * @param password      password for authenticated login
     */
    public RedminePump(String projectHandle, String privateKeyLoc, String username, String password) {
        super(projectHandle, privateKeyLoc, username, password);
        this.tool = Tool.REDMINE;
    }

    @Override
    public ProjectInstance mineData(EntityManager em) {

        pi = super.mineData(em);

        setToolInstance();

        try {
            for (com.taskadapter.redmineapi.bean.Project prj : rootObject.getProjectManager().getProjects()) {
                if (prj.getIdentifier().equals(pi.getName())) {
                    redmineProject = prj;
                }
            }
        } catch (RedmineException e) {
            System.out.println("\tInsufficient permissions for projects");
        }

        Project project = new Project();
        project.setName(redmineProject.getName());
        project.setDescription(redmineProject.getDescription());
        project.setStartDate(redmineProject.getCreatedOn());

        pi.setExternalId(redmineProject.getId().toString());
        pi.setProject(project);

        mineWiki();

        mineContent();

        mineAllRelations();
        finalTouches();

        return pi;
    }

    @Override
    public void mineAllRelations() {
        for (WorkUnit unit : pi.getProject().getUnits()) {
            Issue issue = null;
            try {
                issue = rootObject.getIssueManager().getIssueById(unit.getNumber(), Include.relations);
            } catch (RedmineException e) {
                e.printStackTrace();
            }
            if (issue != null) mineRelations(unit, issue);
        }
    }

    @Override
    protected void mineMentions() {
        // from work unit descriptions
        for (WorkUnit unit : pi.getProject().getUnits()) {
            mineAllMentionedItems(unit);
        }

        for (Configuration configuration : pi.getProject().getConfigurations()) {
            if (!(configuration instanceof Commit || configuration instanceof CommittedConfiguration)) {
                for (WorkItemChange change : configuration.getChanges()) {
                    // from wiki page texts
                    if (change.getChangedItem() instanceof Artifact &&
                            ((Artifact) change.getChangedItem()).getArtifactClass().equals(ArtifactClass.WIKIPAGE)) {
                        mineAllMentionedItems(change.getChangedItem());
                    }
                    // from work unit changes and logtimes
                    if (change.getChangedItem() instanceof WorkUnit &&
                            (change.getName().equals("MODIFY") || change.getName().equals("LOGTIME"))) {
                        mineAllMentionedItems(change.getChangedItem(), configuration.getDescription());
                    }
                }
            }
        }
    }

    @Override
    public void mineCategories() {
        Collection<IssueCategory> issueCategories = new LinkedHashSet<>();

        try {
            issueCategories = rootObject.getIssueManager().getCategories(redmineProject.getId());
        } catch (RedmineException e) {
            System.out.println("\tInsufficient permissions for categories");
        }

        for (IssueCategory issueCategory : issueCategories) {
            Category category = new Category();
            category.setExternalId(issueCategory.getId().toString());
            category.setName(issueCategory.getName());
            pi.getCategories().add(category);
        }
    }

    /**
     * mines projects wiki
     */
    private void mineWiki() {
        Map<String, Artifact> wikies = new HashMap<>();

        WikiManager wikiMgr = rootObject.getWikiManager();
        List<WikiPage> pages = new ArrayList<>();
        try {
            pages = wikiMgr.getWikiPagesByProject(redmineProject.getIdentifier());
        } catch (RedmineException e) {
            System.out.println("\tInsufficient permissions for wiki");
        }
        for (WikiPage page : pages) {
            Artifact wikiPage = mineWikiPage(page);
            wikies.put(wikiPage.getName(), wikiPage);
        }

        linkWikiPages(wikies);
    }

    /**
     * links parent and children wiki pages
     * @param wikies map of wiki pages
     */
    private void linkWikiPages(Map<String, Artifact> wikies) {
        WikiManager wikiMgr = rootObject.getWikiManager();
        List<WikiPage> pages = new ArrayList<>();
        try {
            pages = wikiMgr.getWikiPagesByProject(redmineProject.getIdentifier());
        } catch (RedmineException e) {
            System.out.println("\tInsufficient permissions for wiki");
        }

        for (WikiPage page : pages) {

            WikiPageDetail childDetail = null;
            try {
                childDetail = wikiMgr.getWikiPageDetailByProjectAndTitle(page.getTitle(), redmineProject.getIdentifier());
            } catch (RedmineException e) {
                System.out.println("\tInsufficient permissions for wiki detail: " + page.getTitle());
            }
            if (childDetail == null) continue;

            Artifact parent = wikies.get(childDetail.getParent().getTitle());
            Artifact child = wikies.get(page.getTitle());

            parent.getRelatedItems().add(new WorkItemRelation(child, resolveRelation("parent of")));
            child.getRelatedItems().add(new WorkItemRelation(parent, resolveRelation("child of")));
        }
    }

    /**
     * mine a sigular wiki page
     */
    private Artifact mineWikiPage(WikiPage page) {
        WikiManager wikiMgr = rootObject.getWikiManager();

        Artifact artifact = new Artifact();
        artifact.setArtifactClass(ArtifactClass.WIKIPAGE);
        artifact.setCreated(page.getCreatedOn());
        artifact.setName(page.getTitle());

        WikiPageDetail detail = null;
        try {
            detail = wikiMgr.getWikiPageDetailByProjectAndTitle(redmineProject.getIdentifier(), page.getTitle());
        } catch (RedmineException e) {
            System.out.println("\tInsufficient permissions for wiki detail: " + page.getTitle());
        }

        if (detail != null) {
            artifact.setDescription(detail.getText() + "\n\nComments: " + detail.getComments());
            artifact.setAuthor(addPerson(generateIdentity(detail.getUser().getId(), detail.getUser().getLogin())));
            mineAttachments(artifact, detail.getAttachments());
        }

        WorkItemChange change = new WorkItemChange();
        change.setName("ADD");
        change.setDescription("wiki page added");
        change.setChangedItem(artifact);

        Configuration configuration = new Configuration();
        configuration.setCreated(artifact.getCreated());
        configuration.setAuthor(artifact.getAuthor());
        configuration.getChanges().add(change);
        configuration.getRelatedItems().addAll(artifact.getRelatedItems());
        pi.getProject().getConfigurations().add(configuration);

        return artifact;
    }

    public void minePeople() {
        Map<String, Group> groupMap = new HashMap<>();
        List<Membership> memberships = new ArrayList<>();
        try {
            memberships = rootObject.getMembershipManager().getMemberships(redmineProject.getId());
        } catch (RedmineException e) {
            System.out.println("\tInsufficient permissions for memberships");
        }
        for (Membership member : memberships) {
            if (member.getUserId() == null) continue;

            User user = null;
            try {
                user = rootObject.getUserManager().getUserById(member.getUserId());
            } catch (RedmineException e) {
                System.out.println("\tInsufficient permissions for user: " + member.getUserId());
            }
            Person person = addPerson(generateIdentity(member.getUserId(), member.getUserName()));

            Collection<com.taskadapter.redmineapi.bean.Group> redmineGroups = new ArrayList<>();
            if (user != null) redmineGroups = user.getGroups();

            for (com.taskadapter.redmineapi.bean.Group redmineGroup : redmineGroups) {
                Group group = groupMap.get(redmineGroup.getId().toString());
                if (group == null) {
                    group = new Group();
                    group.setExternalId(redmineGroup.getId().toString());
                    group.setName(redmineGroup.getName());
                    groupMap.put(group.getExternalId(), group);
                }
                group.getMembers().add(person);
            }
            for (com.taskadapter.redmineapi.bean.Role redmineRole : member.getRoles()) {
                person.getRoles().add(resolveRole(redmineRole.getName()));
            }
        }

    }

    @Override
    public void mineTickets() {

        List<Issue> issues = new ArrayList<>();
        int queryId = 73;
        try {
            for (SavedQuery query : rootObject.getIssueManager().getSavedQueries()) {
                if (toLetterOnlyLowerCase(query.getName()).equals("allissues")) {
                    queryId = query.getId();
                }
            }
        } catch (RedmineException e) {
            System.out.println("\tInsufficient permissions for queries");
        }

        try {
            issues = rootObject.getIssueManager().getIssues(redmineProject.getIdentifier(), queryId, Include.values());
        } catch (RedmineException e) {
            System.out.println("\tInsufficient permissions for issues");
        }

        for (Issue issue : issues) {

            WorkUnit unit = new WorkUnit();
            unit.setNumber(issue.getId());
            unit.setExternalId(issue.getId().toString());
            unit.setUrl("https://" + getServer() + "/issues/" + issue.getId());
            unit.setName(issue.getSubject());
            unit.setDescription(issue.getDescription());
            unit.setAuthor(addPerson(generateIdentity(issue.getAuthorId(), issue.getAuthorName())));
            unit.setCreated(issue.getCreatedOn());
            unit.setStartDate((issue.getStartDate() == null) ? issue.getCreatedOn() : issue.getStartDate());
            unit.setDueDate(issue.getDueDate());
            unit.setAssignee(addPerson(generateIdentity(issue.getAssigneeId(), issue.getAssigneeName())));
            unit.setStatus(resolveStatus(issue.getStatusName()));
            unit.setType(resolveType(issue.getTracker().getName()));
            unit.setPriority(resolvePriority(issue.getPriorityText()));
            unit.setEstimatedTime((issue.getEstimatedHours() == null) ? 0 : issue.getEstimatedHours());
            unit.setSpentTime(getSpentTimeFromEntries(unit, issue.getId()));
            unit.setProgress(issue.getDoneRatio());
            unit.getCategories().addAll(resolveCategories(issue));
            unit.setSeverity(assignSeverity(issue));

            pi.getProject().addUnit(unit);

            mineAttachments(unit, issue.getAttachments());
            mineHistory(unit, issue.getJournals());
            mineRevisions(unit, issue.getChangesets());

            if (issue.getTargetVersion() != null) {
                Iteration iteration = new Iteration();
                iteration.setExternalId(issue.getTargetVersion().getId() + "");
                unit.setIteration(iteration);
            }

            generateCreationConfig(unit);
            if (issue.getClosedOn() != null) generateClosureConfig(unit, issue.getClosedOn());
        }
    }

    /**
     * mines a configuration/action of closing an issue
     * @param unit WorkUnit (issue)
     * @param closedOn date of closure
     */
    private void generateClosureConfig(WorkUnit unit, Date closedOn) {
        WorkItemChange change = new WorkItemChange();
        change.setName("MODIFY");
        change.setDescription("issue closed");
        change.setChangedItem(unit);

        change.getFieldChanges().add(new FieldChange("status", StatusSuperClass.OPEN.name(), StatusSuperClass.CLOSED.name()));

        Configuration closure = new Configuration();
        closure.setCreated(closedOn);
        closure.getChanges().add(change);

        pi.getProject().getConfigurations().add(closure);
    }

    /**
     * mines relation between issues
     * @param issue (not yet mined) issue
     * @param unit already mined issue (a.k.a. WorkUnit)
     */
    private void mineRelations(WorkUnit unit, Issue issue) {

        if (issue.getParentId() != null) {
            WorkUnit parent = pi.getProject().getUnit(issue.getParentId());
            unit.getRelatedItems().add(new WorkItemRelation(parent, resolveRelation("child of")));
            parent.getRelatedItems().add(new WorkItemRelation(unit, resolveRelation("parent of")));
        }
        for (IssueRelation relation : issue.getRelations()) {
            WorkUnit related = pi.getProject().getUnit(relation.getIssueToId());
            unit.getRelatedItems().add(new WorkItemRelation(related, resolveRelation(relation.getType())));
        }
    }

    /**
     * mines revisions linked to the issue
     * @param unit issue
     * @param changesets changesets (revisions) linked to the issue
     */
    private void mineRevisions(WorkUnit unit, Collection<Changeset> changesets) {
        for (Changeset changeset : changesets) {
            if (pi.getProject().containsCommit(changeset.getRevision())) {
                Commit commit = pi.getProject().getCommit(changeset.getRevision());
                generateMentionRelation(commit, unit);
            }
        }
    }

    /**
     * mines issue historz
     * @param unit issue
     * @param journals journals (history entries)
     */
    private void mineHistory(WorkUnit unit, Collection<Journal> journals) {
        Collection<Configuration> configurations = new LinkedHashSet<>();
        for (Journal journal : journals) {
            Configuration configuration = new Configuration();
            configuration.setAuthor(addPerson(generateIdentity(journal.getUser().getId(), journal.getUser().getLogin())));
            configuration.setCreated(journal.getCreatedOn());
            configuration.setExternalId(journal.getId().toString());
            configuration.setDescription(journal.getNotes());
            WorkItemChange change = new WorkItemChange();
            change.setChangedItem(unit);
            change.setName("MODIFY");
            change.setFieldChanges(mineChanges(journal.getDetails()));

            configurations.add(configuration);
        }
        pi.getProject().getConfigurations().addAll(configurations);
    }

    /**
     * mines issue field changes from a historz record
     * @param details historz record
     * @return list of field changes
     */
    private List<FieldChange> mineChanges(List<JournalDetail> details) {
        List<FieldChange> changes = new ArrayList<>();
        for (JournalDetail detail : details) {
            FieldChange change = new FieldChange();
            change.setName(detail.getProperty());
            change.setNewValue(detail.getNewValue());
            change.setOldValue(detail.getOldValue());
            changes.add(change);
        }
        return changes;
    }

    /**
     * mines attachements of an issue or wiki page
     * @param attachments attachments
     * @param item issue or wiki page
     */
    private void mineAttachments(WorkItem item, Collection<Attachment> attachments) {
        for (Attachment attachment : attachments) {
            Artifact artifact = new Artifact();
            artifact.setArtifactClass(ArtifactClass.FILE);
            artifact.setMimeType(attachment.getContentType());
            artifact.setUrl(attachment.getContentURL());
            artifact.setAuthor(addPerson(generateIdentity(attachment.getAuthor().getId(), attachment.getAuthor().getLogin())));
            artifact.setCreated(attachment.getCreatedOn());
            artifact.setDescription(attachment.getDescription());
            artifact.setExternalId(attachment.getId().toString());
            artifact.setName(attachment.getFileName());
            artifact.setSize(attachment.getFileSize());

            WorkItemChange change = new WorkItemChange();
            change.setChangedItem(artifact);
            change.setName("ADD");
            change.setDescription("attachment added");

            Configuration configuration = new Configuration();
            configuration.setCreated(attachment.getCreatedOn());
            configuration.setAuthor(artifact.getAuthor());
            configuration.getChanges().add(change);

            item.getRelatedItems().add(new WorkItemRelation(artifact, resolveRelation("has attached")));
            artifact.getRelatedItems().add(new WorkItemRelation(item, resolveRelation("attached to")));

            pi.getProject().getConfigurations().add(configuration);
        }
    }

    /**
     * mines issue categories and tags/labels, matches them to the ones used in project or creates new ones in the project
     * @param issue issue
     * @return collection of categories
     */
    private Collection<Category> resolveCategories(Issue issue) {
        Collection<Category> categories = new LinkedHashSet<>();

        if (issue.getCategory() != null) {
            for (Category category : pi.getCategories()) {
                if (category.getName().equals(issue.getCategory().getName())) {
                    categories.add(category);
                    break;
                }
            }
        }

        for (CustomField field : issue.getCustomFields()) {
            if (field.getName().toLowerCase().equals("tags") || field.getName().toLowerCase().equals("labels")) {
                String value = issue.getCustomFieldByName(field.getName()).getValue();
                if (value != null && !value.trim().isEmpty()) {
                    boolean found = false;
                    for (Category category : pi.getCategories()) {
                        if (category.getName().equals(value)) {
                            categories.add(category);
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        Category newCategory = new Category();
                        newCategory.setName(value);
                        categories.add(newCategory);
                    }
                }
            }
        }
        return categories;
    }

    /**
     * matches the issue severity to one of the values used in the project
     * @param issue issue
     * @return corresponding Severity instance or null
     */
    private Severity assignSeverity(Issue issue) {
        for (CustomField field : issue.getCustomFields()) {
            if (field.getName().toLowerCase().equals("severity")) {
                if (field.getValue() == null || field.getValue().isEmpty()) continue;
                return resolveSeverity(field.getValue());
            }
        }
        return null;
    }

    /**
     * mines spent time log entries
     * @param unit issue
     * @param id issue's id
     * @return spent time total
     */
    private double getSpentTimeFromEntries(WorkUnit unit, Integer id) {
        double spentTime = 0;
        List<TimeEntry> entries = new ArrayList<>();
        try {
            entries = rootObject.getTimeEntryManager().getTimeEntriesForIssue(id);
        } catch (RedmineException e) {
            System.out.println("\tInsufficient permissions for log time: " + id);
        }
        for (TimeEntry entry : entries) {
            generateLogTimeConfiguration(unit, spentTime, entry);
            spentTime += entry.getHours();
        }
        return spentTime;
    }

    /**
     * mines a log time entry
     * @param unit issue
     * @param spentTimeBefore time spent on the issue previous to this entry
     * @param entry log time entry
     */
    private void generateLogTimeConfiguration(WorkUnit unit, double spentTimeBefore, TimeEntry entry) {
        CommittedConfiguration configuration = new CommittedConfiguration();
        configuration.setExternalId(entry.getId().toString());
        configuration.setAuthor(addPerson(generateIdentity(entry.getUserId(), entry.getUserName())));
        configuration.setDescription(entry.getComment() + "\n\nActivity: " + entry.getActivityName());
        configuration.setCommitted(entry.getCreatedOn());
        configuration.setCreated(entry.getSpentOn());

        WorkItemChange change = new WorkItemChange();
        change.setChangedItem(unit);
        change.setName("LOGTIME");
        change.setDescription("spent time reported");

        FieldChange fieldChange = new FieldChange();
        fieldChange.setName("spentTime");
        fieldChange.setOldValue(spentTimeBefore + "");
        fieldChange.setNewValue(Double.toString(spentTimeBefore + entry.getHours()));

        change.getFieldChanges().add(fieldChange);
        configuration.getChanges().add(change);

        pi.getProject().getConfigurations().add(configuration);
    }

    /**
     * mines user identity
     * @param id user ID
     * @param name user name
     * @return Identity instance
     */
    private Identity generateIdentity(Integer id, String name) {
        Identity identity = new Identity();
        if (id == null) {
            if (name == null) name = "unknown";
            identity.setName(name);
            return identity;
        }
        try {
            User user = rootObject.getUserManager().getUserById(id);
            identity.setExternalId(id.toString());
            identity.setName(user.getLogin());
            identity.setDescription(user.getFullName());
            identity.setEmail(user.getMail());
        } catch (RedmineException e) {
            System.out.println("\tInsufficient permissions for user: " + id);
        }
        return identity;
    }

    @Override
    public Collection<ProjectSegment> mineIterations() {
        Collection<ProjectSegment> iterations = new LinkedHashSet<>();
        try {
            for (Version version : rootObject.getProjectManager().getVersions(redmineProject.getId())) {
                Iteration iteration = new Iteration();
                iteration.setProject(pi.getProject());
                iteration.setExternalId(version.getId().toString());
                iteration.setName(version.getName());
                iteration.setDescription(version.getDescription());
                iteration.setCreated(version.getCreatedOn());
                iteration.setStartDate(version.getCreatedOn());
                iteration.setEndDate(version.getDueDate());

                Phase phase = new Phase();
                phase.setProject(iteration.getProject());
                phase.setExternalId(iteration.getExternalId());
                phase.setName(iteration.getName());
                phase.setDescription(iteration.getDescription());
                phase.setCreated(iteration.getCreated());
                phase.setStartDate(iteration.getStartDate());
                phase.setEndDate(iteration.getEndDate());

                Activity activity = new Activity();
                activity.setProject(iteration.getProject());
                activity.setExternalId(iteration.getExternalId());
                activity.setName(iteration.getName());
                activity.setDescription(iteration.getDescription());
                activity.setStartDate(iteration.getStartDate());
                activity.setEndDate(iteration.getEndDate());

                iterations.add(phase);
                iterations.add(activity);
                iterations.add(iteration);
            }
        } catch (RedmineException e) {
            System.out.println("\tInsufficient permissions for versions");
        }
        return iterations;
    }

    @Override
    public void mineEnums() {
        super.mineEnums();
        mineStatuses();
        mineSeverities();
    }

    /**
     * mines all the severity values used in the project (if there are any)
     */
    private void mineSeverities() {

        List<CustomFieldDefinition> defs = new ArrayList<>();
        try {
            defs = rootObject.getCustomFieldManager().getCustomFieldDefinitions();
        } catch (RedmineException e) {
            System.out.println("\tInsufficient permissions for custom fields");
        }
        for (CustomFieldDefinition def : defs) {
            if (def.getName().toLowerCase().equals("severity")) {

                for (String issueSeverity : def.getPossibleValues()) {
                    boolean found = false;
                    for (Severity severity : pi.getSeverities()) {
                        if (toLetterOnlyLowerCase(severity.getName()).equals(toLetterOnlyLowerCase(issueSeverity))) {
                            severity.setName(issueSeverity);
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        Severity newSeverity = new Severity(issueSeverity, severityDao.findByClass(SeverityClass.UNASSIGNED));
                        pi.getSeverities().add(newSeverity);
                    }
                }
                break;
            }
        }
    }

    @Override
    public void mineRoles() {

        List<com.taskadapter.redmineapi.bean.Role> roles = new ArrayList<>();
        try {
            roles = rootObject.getUserManager().getRoles();
        } catch (RedmineException e) {
            System.out.println("\tInsufficient permissions for roles");
        }
        for (com.taskadapter.redmineapi.bean.Role redmineRole : roles) {
            boolean found = false;
            for (Role role : pi.getRoles()) {
                if (toLetterOnlyLowerCase(role.getName()).equals(toLetterOnlyLowerCase(redmineRole.getName()))) {
                    role.setName(redmineRole.getName());
                    role.setExternalId(redmineRole.getId().toString());
                    found = true;
                    break;
                }
            }
            if (!found) {
                Role newRole = new Role(redmineRole.getName(), roleDao.findByClass(RoleClass.UNASSIGNED));
                newRole.setExternalId(redmineRole.getId().toString());
                pi.getRoles().add(newRole);
            }
        }
    }

    /**
     * mines all the status values used in the project
     */
    private void mineStatuses() {

        List<IssueStatus> statuses = new ArrayList<>();
        try {
            statuses = rootObject.getIssueManager().getStatuses();
        } catch (RedmineException e) {
            System.out.println("\tInsufficient permissions for statuses");
        }
        for (IssueStatus issueStatus : statuses) {
            boolean found = false;
            for (Status status : pi.getStatuses()) {
                if (toLetterOnlyLowerCase(status.getName()).equals(toLetterOnlyLowerCase(issueStatus.getName()))) {
                    status.setName(issueStatus.getName());
                    status.setExternalId(issueStatus.getId().toString());
                    found = true;
                    break;
                }
            }
            if (!found) {
                Status newStatus = new Status(issueStatus.getName(), statusDao.findByClass(StatusClass.UNASSIGNED));
                newStatus.setExternalId(issueStatus.getId().toString());
                pi.getStatuses().add(newStatus);
            }
        }
    }

    @Override
    public void minePriorities() {

        List<IssuePriority> priorities = new ArrayList<>();
        try {
            priorities = rootObject.getIssueManager().getIssuePriorities();
        } catch (RedmineException e) {
            System.out.println("\tInsufficient permissions for priorities");
        }
        for (IssuePriority issuePriority : priorities) {
            boolean found = false;
            for (Priority priority : pi.getPriorities()) {
                if (toLetterOnlyLowerCase(priority.getName()).equals(toLetterOnlyLowerCase(issuePriority.getName()))) {
                    priority.setName(issuePriority.getName());
                    priority.setExternalId(issuePriority.getId().toString());
                    found = true;
                    break;
                }
            }
            if (!found) {
                Priority newPriority = new Priority(issuePriority.getName(), priorityDao.findByClass(PriorityClass.UNASSIGNED));
                newPriority.setExternalId(issuePriority.getId().toString());
                pi.getPriorities().add(newPriority);
            }
        }
    }

    @Override
    public void mineWUTypes() {

        for (Tracker tracker : redmineProject.getTrackers()) {
            boolean found = false;
            for (WorkUnitType type : pi.getWuTypes()) {
                if (toLetterOnlyLowerCase(type.getName()).equals(toLetterOnlyLowerCase(tracker.getName()))) {
                    type.setName(tracker.getName());
                    type.setExternalId(tracker.getId().toString());
                    found = true;
                    break;
                }
            }
            if (!found) {
                WorkUnitType newType = new WorkUnitType(tracker.getName(), typeDao.findByClass(WorkUnitTypeClass.UNASSIGNED));
                newType.setExternalId(tracker.getId().toString());
                pi.getWuTypes().add(newType);
            }
        }
    }

    @Override
    protected RedmineManager init() {
        String serverWithProtocol = "https://" + getServer();
        if (privateKeyLoc != null)
            return RedmineManagerFactory.createWithApiKey(serverWithProtocol, privateKeyLoc);
        if (username != null && password != null)
            return RedmineManagerFactory.createWithUserAuth(serverWithProtocol, username, password);
        return RedmineManagerFactory.createUnauthenticated(serverWithProtocol);
    }
}
