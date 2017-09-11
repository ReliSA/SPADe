package cz.zcu.kiv.spade.pumps.impl;

import cz.zcu.kiv.spade.domain.*;
import cz.zcu.kiv.spade.domain.abstracts.ProjectSegment;
import cz.zcu.kiv.spade.domain.Category;
import cz.zcu.kiv.spade.domain.enums.StatusClass;
import cz.zcu.kiv.spade.domain.enums.Tool;
import cz.zcu.kiv.spade.load.DBInitializer;
import cz.zcu.kiv.spade.pumps.DataPump;
import cz.zcu.kiv.spade.pumps.abstracts.ComplexPump;
import org.kohsuke.github.*;

import javax.persistence.EntityManager;
import java.io.IOException;
import java.util.*;

/**
 * data pump for mining GitHub repositories
 *
 * @author Petr Pícha
 */
public class GitHubPump extends ComplexPump<GHRepository> {

    /**
     * @param projectHandle URL of the project instance
     * @param privateKeyLoc private key location for authenticated login
     * @param username      username for authenticated login
     * @param password      password for authenticated login
     */
    public GitHubPump(String projectHandle, String privateKeyLoc, String username, String password) {
        super(projectHandle, privateKeyLoc, username, password);
        this.tool = Tool.GITHUB;
    }

    @Override
    protected GHRepository init() {
        GHRepository repo = null;
        try {
            GitHub gitHub = GitHub.connectUsingPassword(username, password);
            repo = gitHub.getRepository(getProjectFullName());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return repo;
    }

    @Override
    public ProjectInstance mineData(EntityManager em) {
        super.mineData(em);

        DataPump gitPump = new GitPump(projectHandle, null, null, null);
        pi = gitPump.mineData(em);
        gitPump.close();

        this.tool = Tool.GITHUB;
        pi.getToolInstance().setTool(tool);
        setToolInstance();

        pi.getProject().setDescription(rootObject.getDescription());

        enhanceCommits();

        new DBInitializer(em).setDefaultEnums(pi);

        mineEnums();
        Collection<ProjectSegment> iterations = mineIterations();
        printLogMsg("milestones mining done");

        mineTickets();
        printLogMsg("tickets mining done");

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

        addDeletedStatus();

        getTagDescriptions();
        printLogMsg("tags mining done");

        assignDefaultEnums();

        return pi;
    }

    /**
     * adds correct URLs to commits mined from Git
     */
    private void enhanceCommits() {
        for (Configuration configuration : pi.getProject().getConfigurations()) {
            Commit commit = null;
            if (configuration instanceof Commit) {
                commit = (Commit) configuration;
            }
            GHCommit ghCommit = null;
            try {
                ghCommit = rootObject.getCommit(configuration.getName());
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (ghCommit != null && commit != null) {
                commit.setUrl(ghCommit.getHtmlUrl().toString());
                mineCommitComments(commit, ghCommit);
            }
        }
    }

    private void mineCommitComments(Commit commit, GHCommit ghCommit) {
        for (GHCommitComment comment : ghCommit.listComments()) {
            ConfigPersonRelation relation = new ConfigPersonRelation();
            try {
                relation.setPerson(addPerson(generateIdentity(comment.getUser())));
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (relation.getPerson() != null) {
                relation.setName("Commented-on-by");
                relation.setExternalId(comment.getId() + "");
                String date = "";
                try {
                    date = comment.getCreatedAt().toString();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                relation.setDescription(comment.getBody() + "\n" +
                        "Date: " + date +
                        "File: " + comment.getPath() + "\n" +
                        "Line:" + comment.getLine() + "\n" +
                        "URL: " + comment.getHtmlUrl());
                commit.getRelations().add(relation);

                mineAllMentionedItemsGit(commit);
            }
        }
    }

    /**
     * adds release names and description to SPADe tag descriptions mined from Git
     */
    private void getTagDescriptions() {
        for (Configuration conf : pi.getProject().getConfigurations()) {
            if (!(conf instanceof Commit)) continue;
            Commit commit = (Commit) conf;
            for (VCSTag tag : commit.getTags()) {
                try {
                    for (GHRelease release : rootObject.listReleases()) {
                        if (tag.getName().equals(release.getTagName())) {
                            tag.setDescription(release.getName() + "\n" + release.getBody());
                            mineAllMentionedItemsGit(commit, release.getBody());
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * mines milestones and saves each one as Iteration, Phase and Activity
     * for further analysis
     * @return collection of Iterations, Phases and Activities
     */
    public Collection<ProjectSegment> mineIterations() {
        Collection<ProjectSegment> iterations = new LinkedHashSet<>();
        for (GHMilestone milestone : rootObject.listMilestones(GHIssueState.ALL)) {
            Iteration iteration = new Iteration();
            iteration.setProject(pi.getProject());
            iteration.setExternalId(milestone.getId() + "");
            iteration.setName(milestone.getTitle());
            iteration.setDescription(milestone.getDescription());
            try {
                iteration.setCreated(milestone.getCreatedAt());
            } catch (IOException e) {
                e.printStackTrace();
            }
            iteration.setStartDate(iteration.getCreated());
            iteration.setEndDate(milestone.getDueOn());

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
        return iterations;
    }

    @Override
    public void mineBranches() {}

    @Override
    public void addTags() {}

    /**
     * mines issues (not pull requests)
     */
    public void mineTickets() {

        Set<GHIssue> issues = rootObject.listIssues(GHIssueState.ALL).asSet();
        for (GHIssue issue : issues) {
            WorkUnit unit = pi.getProject().addUnit(new WorkUnit(issue.getNumber()));
            unit.setExternalId(issue.getId() + "");
            unit.setUrl(issue.getHtmlUrl().toString());
            unit.setName(issue.getTitle());
            unit.setDescription(issue.getBody());
            unit.setAuthor(addPerson(generateIdentity(issue.getUser())));
            unit.setAssignee(addPerson(generateIdentity(issue.getAssignee())));
            unit.setStatus(resolveStatus(issue.getState().name()));

            if (issue.getMilestone() != null) {
                Iteration iteration = new Iteration();
                iteration.setExternalId(issue.getMilestone().getId() + "");
                unit.setIteration(iteration);
            }
        printLogMsg(issues.size() + " issues listed");

            try {
                unit.setCreated(issue.getCreatedAt());
                unit.setStartDate(issue.getCreatedAt());
                if (issue.getLabels() != null && !issue.getLabels().isEmpty()) {
                    mineLabels(issue.getLabels(), unit);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            mineChanges(issue, unit);

            mineAllMentionedItemsGit(unit);
        }
    }

    /**
     * gets a correct Status instance base on name of issue state in GitHub
     * or null if project doesn't use this status name
     * @param gitHubName name of the issue state in GitHub
     * @return Status instance or null
     */
    private Status resolveStatus(String gitHubName) {

        for (Status status : pi.getStatuses()) {
            if (gitHubName.equals(status.getName())) {
                return status;
            }
        }
        return null;
    }

    /**
     * mines issue changes (creation, comments and closure)
     * @param issue GitHub issue
     * @param unit SPADe Work Unit to link the changes to
     */
    private void mineChanges(GHIssue issue, WorkUnit unit) {

        pi.getProject().getConfigurations().add(generateCreationConfig(unit));

        try {
            for (GHIssueComment comment : issue.getComments()) {
                pi.getProject().getConfigurations().add(generateCommentConfig(unit, comment));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (issue.getClosedBy() != null && issue.getClosedAt() != null) {
            pi.getProject().getConfigurations().add(generateClosureConfig(unit, issue.getClosedAt(), issue.getClosedBy()));
        }
    }

    /**
     * generates a Configuration representing the creation of a Work Unit (issue)
     * @param unit Work Unit to link the creation Configuration to
     * @return creation Configuration
     */
    private Configuration generateCreationConfig(WorkUnit unit) {
        WorkItemChange change = new WorkItemChange();
        change.setChangedItem(unit);
        change.setName("ADD");
        change.setDescription("added");

        change.getFieldChanges().add(new FieldChange("status", null, GHIssueState.OPEN.name()));

        Configuration configuration = new Configuration();
        configuration.setAuthor(unit.getAuthor());
        configuration.setCreated(unit.getCreated());
        configuration.getChanges().add(change);

        return configuration;
    }

    /**
     * generates a Configuration representing a comment being added to a Work Unit (issue)
     * @param unit Work Unit to link the comment Configuration to
     * @param comment an issue comment form GitHub
     * @return comment Configuration
     */
    private Configuration generateCommentConfig(WorkUnit unit, GHIssueComment comment) {
        WorkItemChange change = new WorkItemChange();
        change.setName("COMMENT");
        change.setDescription("comment added");
        change.setChangedItem(unit);

        Configuration configuration = new Configuration();
        configuration.setDescription(comment.getBody());
        try {
            configuration.setAuthor(addPerson(generateIdentity(comment.getUser())));
            configuration.setCreated(comment.getCreatedAt());
        } catch (IOException e) {
            e.printStackTrace();
        }
        configuration.getChanges().add(change);

        mineAllMentionedItemsGit(unit, comment.getBody());
        configuration.getRelatedItems().addAll(unit.getRelatedItems());

        return configuration;
    }

    /**
     * generates a Configuration representing the closure of a Work Unit (issue)
     * @param unit Work Unit to link to the closure Configuration
     * @param closedAt date of closure
     * @param closedBy a GitHub User who closed the issue
     * @return closure Configuration
     */
    private Configuration generateClosureConfig(WorkUnit unit, Date closedAt, GHUser closedBy) {
        WorkItemChange change = new WorkItemChange();
        change.setChangedItem(unit);
        change.setName("MODIFY");
        change.setDescription("closed");

        change.getFieldChanges().add(new FieldChange("status", GHIssueState.OPEN.name(), GHIssueState.CLOSED.name()));

        Configuration configuration = new Configuration();
        configuration.setAuthor(addPerson(generateIdentity(closedBy)));
        configuration.setCreated(closedAt);
        configuration.getChanges().add(change);

        return configuration;
    }

    /**
     * generates SPADe Identity instance based on GitHub user data
     * @param user GitHub user
     * @return Identity instance
     */
    private Identity generateIdentity(GHUser user) {

        Identity identity = new Identity();

        if (user == null) {
            identity.setName("unknown");
            return identity;
        }

        identity.setExternalId(user.getId() + "");
        if (user.getLogin() != null && !user.getLogin().isEmpty()) {
            identity.setName(user.getLogin());
        }
        try {
            if (user.getName() != null && !user.getName().isEmpty()) {
                identity.setDescription(user.getName());
            }
            if (user.getEmail() != null && !user.getEmail().isEmpty()) {
                if (identity.getName().isEmpty() || identity.getName().equals("unknown")) {
                    identity.setName(user.getEmail().split("@")[0]);
                }
                identity.setEmail(user.getEmail());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return identity;
    }

    /**
     * mine labels of a Work Unit (issue);
     * if they correspond with Work Unit type, resolution, priority or severity
     * used in the project the method assigns appropriate attributes,
     * if not in ads the label to Work Unit's categories
     * @param labels labels of the issue
     * @param unit Work Unit to resolve the labels for
     */
    private void mineLabels(Collection<GHLabel> labels, WorkUnit unit) {
        for (GHLabel label : labels) {
            if (!resolveType(unit, label.getName())
                    && !resolveResolution(unit, label.getName())
                    && !resolvePriority(unit, label.getName())
                    && !resolveSeverity(unit, label.getName())) {
                for (Category category : pi.getCategories()) {
                    if (category.getName().equals(label.getName())) {
                        unit.getCategories().add(category);
                    }
                }
            }
        }
    }

    /**
     * if a label name corresponds with a Work Unit type used in the project
     * it assigns it to the Work Unit and returns true, otherwise returns false
     * @param unit Wurk Unit to resolve the type for
     * @param label a label name
     * @return true if the type is assigned, else false
     */
    private boolean resolveType(WorkUnit unit, String label) {

        for (WorkUnitType type : pi.getWuTypes()) {
            if (label.equals(type.getName())) {
                unit.setType(type);
                return true;
            }
        }
        return false;
    }

    /**
     * if a label name corresponds with a Work Unit priority used in the project
     * it assigns it to the Work Unit and returns true, otherwise returns false
     * @param unit Wurk Unit to resolve the priority for
     * @param label a label name
     * @return true if the priority is assigned, else false
     */
    private boolean resolvePriority(WorkUnit unit, String label) {

        for (Priority priority : pi.getPriorities()) {
            if (label.equals(priority.getName())) {
                unit.setPriority(priority);
                return true;
            }
        }
        return false;
    }

    /**
     * if a label name corresponds with a Work Unit severity used in the project
     * it assigns it to the Work Unit and returns true, otherwise returns false
     * @param unit Wurk Unit to resolve the severity for
     * @param label a label name
     * @return true if the severity is assigned, else false
     */
    private boolean resolveSeverity(WorkUnit unit, String label) {

        for (Severity severity : pi.getSeverities()) {
            if (label.equals(severity.getName())) {
                unit.setSeverity(severity);
                return true;
            }
        }
        return false;
    }

    /**
     * if a label name corresponds with a Work Unit resolution used in the project
     * it assigns it to the Work Unit and returns true, otherwise returns false
     * @param unit Wurk Unit to resolve the resolution for
     * @param label a label name
     * @return true if the resolution is assigned, else false
     */
    private boolean resolveResolution(WorkUnit unit, String label) {

        for (Resolution resolution : pi.getResolutions()) {
            if (label.equals(resolution.getName())) {
                unit.setResolution(resolution);
                return true;
            }
        }
        return false;
    }

    @Override
    public void mineEnums() {
        mineStatuses();

        List<GHLabel> labels = new ArrayList<>();
        try {
            labels = rootObject.listLabels().asList();
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (GHLabel label : labels) {
            if (isWUType(label)) continue;
            if (isResolution(label)) continue;
            if (isPriority(label)) continue;
            if (!isSeverity(label)) {
                Category category = new Category();
                category.setExternalId(label.getUrl());
                category.setName(label.getName());
                category.setDescription(label.getColor());
                pi.getCategories().add(category);
            }
        }
    }

    /**
     * checks if GitHub label name corresponds with any severity value used in the project
     * @param label label to check
     * @return true if label name is severity, else false
     */
    private boolean isSeverity(GHLabel label) {
        String name = toLetterOnlyLowerCase(label.getName());
        for (Severity severity : pi.getSeverities()) {
            if (name.equals(toLetterOnlyLowerCase(severity.getName()))) {
                severity.setDescription(label.getColor() + "\n" + label.getUrl());
                severity.setName(label.getName());
                return true;
            }
        }
        return false;
    }

    /**
     * checks if GitHub label name corresponds with any priority value used in the project
     * @param label label to check
     * @return true if label name is priority, else false
     */
    private boolean isPriority(GHLabel label) {
        String name = toLetterOnlyLowerCase(label.getName());
        for (Priority priority : pi.getPriorities()) {
            if (name.equals(toLetterOnlyLowerCase(priority.getName()))) {
                priority.setDescription(label.getColor() + "\n" + label.getUrl());
                priority.setName(label.getName());
                return true;
            }
        }
        return false;
    }

    /**
     * checks if GitHub label name corresponds with any resolution value used in the project
     * @param label label to check
     * @return true if label name is resolution, else false
     */
    private boolean isResolution(GHLabel label) {
        String name = toLetterOnlyLowerCase(label.getName());
        for (Resolution resolution : pi.getResolutions()) {
            if (name.equals(toLetterOnlyLowerCase(resolution.getName()))) {
                resolution.setDescription(label.getColor() + "\n" + label.getUrl());
                resolution.setName(label.getName());
                return true;
            }
        }
        return false;
    }

    /**
     * checks if GitHub label name corresponds with any Work Unit type value used in the project
     * @param label label to check
     * @return true if label name is Work Unit type, else false
     */
    private boolean isWUType(GHLabel label) {
        String name = toLetterOnlyLowerCase(label.getName());
        for (WorkUnitType type : pi.getWuTypes()) {
            if (name.equals(toLetterOnlyLowerCase(type.getName()))) {
                type.setDescription(label.getColor() + "\n" + label.getUrl());
                type.setName(label.getName());
                return true;
            }
        }
        return false;
    }

    /**
     * mines statuses from GitHub and adds corresponding Statuses to the Project Instance
     */
    private void mineStatuses() {
        for (GHIssueState state : GHIssueState.values()) {
            if (state == GHIssueState.ALL) continue;

            String name = toLetterOnlyLowerCase(state.name());

            boolean found = false;
            for (Status status : pi.getStatuses()) {
                if (name.equals(toLetterOnlyLowerCase(status.getName()))) {
                    status.setName(state.name());
                    found = true;
                    break;
                }
            }
            if (!found) {
                Status newStatus = new Status(state.name(), statusDao.findByClass(StatusClass.UNASSIGNED));
                pi.getStatuses().add(newStatus);
            }
        }
    }

    /**
     * gets project full name (organisation/project)
     *
     * @return project name
     */
    private String getProjectFullName() {
        return getProjectDir().substring(getProjectDir().indexOf("/") + 1);
    }
}
