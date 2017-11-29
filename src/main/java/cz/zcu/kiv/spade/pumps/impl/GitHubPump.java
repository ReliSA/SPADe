package cz.zcu.kiv.spade.pumps.impl;

import cz.zcu.kiv.spade.App;
import cz.zcu.kiv.spade.domain.*;
import cz.zcu.kiv.spade.domain.abstracts.ProjectSegment;
import cz.zcu.kiv.spade.domain.Category;
import cz.zcu.kiv.spade.domain.enums.*;
import cz.zcu.kiv.spade.load.DBInitializer;
import cz.zcu.kiv.spade.pumps.DataPump;
import cz.zcu.kiv.spade.pumps.abstracts.ComplexPump;
import org.kohsuke.github.*;

import javax.persistence.EntityManager;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

/**
 * data pump for mining GitHub repositories
 *
 * @author Petr Pícha
 */
public class GitHubPump extends ComplexPump<GHRepository> {

    /** GitHub instance for getting items. */
    private GitHub gitHub;
    /** list of usernames necessary for continuous mining (rate limit workaround) */
    private List<String> usernames = new ArrayList<>();
    /** list of passwords necessary for continuous mining (rate limit workaround) */
    private List<String> passwords = new ArrayList<>();

    /**
     * a constructor, sets project's URL and login credentials
     *
     * @param projectHandle URL of the project instance
     * @param privateKeyLoc private key location for authenticated login
     * @param username      username for authenticated login
     * @param password      password for authenticated login
     */
    public GitHubPump(String projectHandle, String privateKeyLoc, String username, String password) {
        super(projectHandle, privateKeyLoc, username, password);
        this.tool = Tool.GITHUB;
        usernames.add(0, username);
        usernames.add(1, "spade01");
        usernames.add(2, "spade02");
        usernames.add(3, "spade03");
        passwords.add(0, password);
        passwords.add(1, "papepi48");
        passwords.add(2, "papepi48");
        passwords.add(3, "papepi48");
    }

    @Override
    protected GHRepository init() {
        return null;
    }

    /**
     * initializes connection to the GitHub server and swaps user acounts and reconnects if necessary (rate limit runs out)
     * @param wait true if there should be a 5s waiting period before reconecting, false if users should be switched
     * @return an instance for mining GitHub data
     */
    private GHRepository init(boolean wait) {
        GHRepository repo;
        while (true) {
            try {
                if (wait) Thread.sleep(5000);
                else {
                    int index = (usernames.indexOf(username) + 1) % 4;
                    username = usernames.get(index);
                    password = passwords.get(index);
                }
                gitHub = GitHub.connectUsingPassword(username, password);
                GHRateLimit limit = gitHub.getRateLimit();
                App.printLogMsg("connected...");
                System.out.println("username: " + username + ", remaining rate limit: " + limit.remaining + ", reset at: " + limit.getResetDate().toString());
                repo = gitHub.getRepository(getProjectFullName());
                if (limit.remaining < 500) continue;
                break;
            } catch (IOException e) {
                System.out.println(e.toString());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return repo;
    }

    @Override
    public ProjectInstance mineData(EntityManager em) {
        super.mineData(em);

        DataPump gitPump = new GitPump(projectHandle, null, null, null);
        pi = gitPump.mineData(em);
        gitPump.close();
        pi.getStats().setRepo(System.currentTimeMillis());

        this.tool = Tool.GITHUB;
        pi.getToolInstance().setTool(tool);
        setToolInstance();

        rootObject = init(false);

        pi.getStats().setName(getProjectFullName());

        if (rootObject.getDescription() != null) pi.getProject().setDescription(rootObject.getDescription().trim());
        Date creation = null;
        try {
            creation = rootObject.getCreatedAt();
        } catch (IOException e) {
            this.init(true);
        }
        if (creation != null && creation.before(pi.getProject().getStartDate())) {
            pi.getProject().setStartDate(creation);
        }

        setDefaultBranch();

        enhanceCommits();
        mineCommitComments();

        new DBInitializer(em).setDefaultEnums(pi);

        mineEnums();
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

        getTagDescriptions();

        /*DataPump wikiPump = new GitPump(App.GITHUB_WIKI_REPO_PREFIX + getProjectFullName() + App.GIT_SUFFIX, null, null, null);
        pi = wikiPump.mineData(em, pi);
        wikiPump.close();*/

        finalTouches();
        return pi;
    }

    @Override
    protected void mineMentions() {
        Set<VCSTag> tags = new HashSet<>();
        for (Commit commit : pi.getProject().getCommits()) {
            // unit mentions from commit messages
            mineMentionedUnits(commit, commit.getDescription());
            for (VCSTag tag : commit.getTags()) {
                if (!tags.contains(tag)) {
                    // from release descriptions
                    mineAllMentionedItemsGit(commit, tag.getDescription());
                    tags.add(tag);
                }
            }
        }
        // from work unit descriptions
        for (WorkUnit unit : pi.getProject().getUnits()) {
            mineAllMentionedItemsGit(unit);
        }
        for (Configuration configuration : pi.getProject().getConfigurations()) {
            if (!(configuration instanceof Commit || configuration instanceof CommittedConfiguration)) {
                for (WorkItemChange change : configuration.getChanges()) {
                    // from issue and commit comments
                    if ((change.getChangedItem() instanceof WorkUnit
                            || change.getChangedItem() instanceof Commit)
                            && change.getName().equals("COMMENT")) {
                        mineAllMentionedItemsGit(change.getChangedItem(), configuration.getDescription());
                    }
                }
            }
        }
    }

    /**
     * finds and sets a default repository branch
     */
    private void setDefaultBranch() {
        String defaultBranch = rootObject.getDefaultBranch();
        pi.getStats().setDefaultBranch(defaultBranch);
        if (!defaultBranch.equals("master")) {
            Map<String, Branch> branches = new HashMap<>();
            for (Commit commit : pi.getProject().getCommits()) {
                for (Branch branch : commit.getBranches()) {
                    if (!branches.containsKey(branch.getName())) {
                        branch.setIsMain(false);
                        branches.put(branch.getName(), branch);
                    }
                }
            }
            branches.get(defaultBranch).setIsMain(true);
        }
    }

    /**
     * adds correct URLs to commits mined from Git
     */
    private void enhanceCommits() {
        for (Commit commit : pi.getProject().getCommits()) {
            String commitUrlPrefix = projectHandle.substring(0, projectHandle.lastIndexOf(App.GIT_SUFFIX)) + "/commit/";
            commit.setUrl(commitUrlPrefix + commit.getName());
        }
    }

    /**
     * mines commit comments
     */
    private void mineCommitComments() {
        List<GHCommitComment> comments = rootObject.listCommitComments().asList();
        int count = 1;
        for (GHCommitComment comment : comments) {

            GHCommit ghCommit = null;

            while (true) {
                try {
                    ghCommit = comment.getCommit();
                    break;
                } catch (IOException e) {
                    if (e instanceof FileNotFoundException) break;
                    else rootObject = init(true);
                }
            }

            if (ghCommit == null) {
                count++;
                continue;
            }

            Commit commit;
            commit = pi.getProject().getCommit(ghCommit.getSHA1().substring(0, 7));

            if (commit != null) {
                pi.getProject().getConfigurations().add(generateCommitCommentConfig(commit, comment));
            }
            if ((count % 100) == 0) {
                if ((count % 500) == 0) {
                    App.printLogMsg("mined " + count + "/" + comments.size() + " commit comments");
                }
                checkRateLimit();
            }
            count++;
        }
        pi.getStats().setCommitComments(comments.size());
    }

    private Configuration generateCommitCommentConfig(Commit commit, GHCommitComment comment) {
        WorkItemChange change = new WorkItemChange();
        change.setName("COMMENT");
        change.setDescription("comment added");
        change.setChangedItem(commit);

        Configuration configuration = new Configuration();
        configuration.setExternalId(comment.getId() + "");
        configuration.setUrl(comment.getHtmlUrl().toString());
        String specifics = "";
        if (comment.getLine() != -1) {
            specifics += "File: " + comment.getPath() + "\n" +
                            "Line: " + comment.getLine() + "\n\n";
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
                rootObject = init(true);
            }
        }
        configuration.setAuthor(addPerson(generateIdentity(user)));
        configuration.setCreated(creation);
        configuration.getChanges().add(change);

        configuration.getRelatedItems().addAll(commit.getRelatedItems());

        return configuration;
    }

    /**
     * checks current access rate limit for GitHub server
     */
    private void checkRateLimit() {
        GHRateLimit limit;
        while (true) {
            try {
                limit = gitHub.getRateLimit();
                break;
            } catch (IOException e) {
                rootObject = init(true);
            }
        }
        if (limit != null && limit.remaining < 500) {
            System.out.println("username: " + username + ", remaining rate limit: " + limit.remaining + ", reset at: " + limit.getResetDate().toString());
            rootObject = init(false);
        }
    }

    /**
     * adds release names and description to SPADe tag descriptions mined from Git
     */
    private void getTagDescriptions() {

        List<GHRelease> releases;
        List<GHTag> tags;
        while (true) {
            try {
                releases = rootObject.listReleases().asList();
                tags = rootObject.listTags().asList();
                break;
            } catch (IOException e) {
                rootObject = init(true);
            }
        }

        Map<String, VCSTag> spadeTags = new HashMap<>();
        for (Commit commit : pi.getProject().getCommits()) {
            for (VCSTag tag : commit.getTags()) {
                if (!spadeTags.containsKey(tag.getName())) {
                    spadeTags.put(tag.getName(), tag);
                }
            }
        }

        int i = 0;
        for (GHRelease release : releases) {
            VCSTag spadeTag = spadeTags.get(release.getTagName());
            if (release.getName() != null) {
                spadeTag.setDescription(release.getName());
            }
            if (release.getBody() != null) {
                spadeTag.setDescription(spadeTag.getDescription() + "\n" + release.getBody().trim());
            }
            i++;
            if ((i % 100) == 0) {
                checkRateLimit();
            }
        }
        App.printLogMsg("mined " + i + "/" + releases.size() + " releases (/" + tags.size() + ")");
        pi.getStats().setTags2(i);
        pi.getStats().setReleases(releases.size());
    }

    /**
     * mines milestones and saves each one as Iteration, Phase and Activity
     * for further analysis
     * @return collection of Iterations, Phases and Activities
     */
    public Collection<ProjectSegment> mineIterations() {
        Collection<ProjectSegment> iterations = new LinkedHashSet<>();
        int i = 1;
        List<GHMilestone> milestones = rootObject.listMilestones(GHIssueState.ALL).asList();
        for (GHMilestone milestone : milestones) {
            Iteration iteration = new Iteration();
            iteration.setProject(pi.getProject());
            iteration.setExternalId(milestone.getId() + "");
            iteration.setName(milestone.getTitle());
            if (milestone.getDescription() != null) iteration.setDescription(milestone.getDescription().trim());

            Date creation;
            while (true) {
                try {
                    creation = milestone.getCreatedAt();
                    break;
                } catch (IOException e) {
                    rootObject = init(true);
                }
            }
            iteration.setCreated(creation);
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

            if ((i % 100) == 0) {
                checkRateLimit();
            }
            i++;
        }
        pi.getStats().setMilestones(milestones.size());

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
        App.printLogMsg("issues listed");
        pi.getStats().setIssues(issues.size());

        int count = 1;
        for (int id = issues.size(); id > 0;) {

            if (((issues.size() - (id - 1)) % 100) == 0) {
                checkRateLimit();
            }

            GHIssue issue;
            try {
                issue = rootObject.getIssue(id);
            } catch (IOException e) {
                if (e instanceof FileNotFoundException) {
                    id--;
                    continue;
                } else {
                    rootObject = init(true);
                    continue;
                }
            }

            if (!issue.isPullRequest()) {
                WorkUnit unit = new WorkUnit();
                unit.setNumber(issue.getNumber());
                unit.setExternalId(issue.getId() + "");
                unit.setUrl(issue.getHtmlUrl().toString());
                unit.setName(issue.getTitle());
                if (issue.getBody() != null) unit.setDescription(issue.getBody().trim());

                unit.setStatus(resolveStatus(issue.getState().name()));

                if (issue.getMilestone() != null) {
                    Iteration iteration = new Iteration();
                    iteration.setExternalId(issue.getMilestone().getId() + "");
                    unit.setIteration(iteration);
                }

                Date creation;
                Collection<GHLabel> labels;
                GHUser author, assignee;
                while (true) {
                    try {
                        author = issue.getUser();
                        assignee = issue.getAssignee();
                        creation = issue.getCreatedAt();
                        labels = issue.getLabels();
                        break;
                    } catch (IOException e) {
                        rootObject = init(true);
                    }
                }

                unit.setAuthor(addPerson(generateIdentity(author)));
                if (assignee != null) unit.setAssignee(addPerson(generateIdentity(assignee)));
                unit.setCreated(creation);
                unit.setStartDate(creation);
                if (labels != null && !labels.isEmpty()) mineLabels(labels, unit);

                pi.getProject().addUnit(unit);

                mineChanges(issue, unit);

                if ((count % 100) == 0) {
                    App.printLogMsg("mined " + count + " tickets");
                }
                count++;
            }
            id--;
        }
        App.printLogMsg("mined " + (count - 1) + " tickets");
        pi.getStats().setRealIssues(count - 1);
    }

    /**
     * mines issue changes (creation, comments and closure)
     * @param issue GitHub issue
     * @param unit SPADe Work Unit to link the changes to
     */
    private void mineChanges(GHIssue issue, WorkUnit unit) {

        generateCreationConfig(unit);

        Collection<GHIssueComment> comments;
        GHUser closer;
        while (true) {
            try {
                comments = issue.getComments();
                closer = issue.getClosedBy();
                break;
            } catch (IOException e) {
                rootObject = init(true);
            }
        }
        pi.getStats().setIssueComments(pi.getStats().getIssueComments() + comments.size());

        for (GHIssueComment comment : comments) {
            pi.getProject().getConfigurations().add(generateUnitCommentConfig(unit, comment));
        }

        if (closer != null && issue.getClosedAt() != null) {
            pi.getProject().getConfigurations().add(generateClosureConfig(unit, issue.getClosedAt(), closer));
        }
    }

    /**
     * generates a Configuration representing a comment being added to a Work Unit (issue)
     * @param unit Work Unit to link the comment Configuration to
     * @param comment an issue comment form GitHub
     * @return comment Configuration
     */
    private Configuration generateUnitCommentConfig(WorkUnit unit, GHIssueComment comment) {
        WorkItemChange change = new WorkItemChange();
        change.setName("COMMENT");
        change.setDescription("comment added");
        change.setChangedItem(unit);

        Configuration configuration = new Configuration();
        configuration.setExternalId(comment.getId() + "");
        configuration.setDescription(comment.getBody().trim());

        GHUser user;
        Date creation;
        while (true) {
            try {
                user = comment.getUser();
                creation = comment.getCreatedAt();
                break;
            } catch (IOException e) {
                rootObject = init(true);
            }
        }
        configuration.setAuthor(addPerson(generateIdentity(user)));
        configuration.setCreated(creation);
        configuration.getChanges().add(change);

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

        String name, email;
        while (true) {
            try {
                name = user.getName();
                email = user.getEmail();
                break;
            } catch (IOException e) {
                rootObject = init(true);
            }
        }

        if (name != null && !name.isEmpty()) {
            identity.setDescription(name);
        }
        if (email != null && !email.isEmpty()) {
            if (identity.getName().isEmpty() || identity.getName().equals("unknown")) {
                identity.setName(email.split("@")[0]);
            }
            identity.setEmail(email);
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

            WorkUnitType type = resolveType(label.getName());
            if (type != null){
                unit.setType(type);
                return;
            }

            Resolution resolution = resolveResolution(label.getName());
            if (resolution != null){
                unit.setResolution(resolution);
                return;
            }

            Severity severity = resolveSeverity(label.getName());
            if (severity != null){
                unit.setSeverity(severity);
                return;
            }

            Priority priority = resolvePriority(label.getName());
            if (priority != null){
                unit.setPriority(priority);
                return;
            }

            for (Category category : pi.getCategories()) {
                if (category.getName().equals(label.getName())) {
                    unit.getCategories().add(category);
                }
            }
        }
    }

    @Override
    public void mineEnums() {
        mineStatuses();
        mineCategories();
    }

    @Override
    public void mineAllRelations() {

    }

    @Override
    public void mineCategories() {
        List<GHLabel> labels;
        while (true) {
            try {
                labels = rootObject.listLabels().asList();
                break;
            } catch (IOException e) {
                rootObject = init(true);
            }
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
        pi.getStats().setLabels(labels.size());
        pi.getStats().setCategories(pi.getCategories().size());
    }

    @Override
    public void minePeople() {
        //TODO + resolveRole
    }

    @Override
    public void mineRoles() {

    }

    @Override
    public void minePriorities() {

    }

    @Override
    public void mineWUTypes() {

    }

    /**
     * checks if GitHub label name corresponds with any severity value used in the project
     * @param label label to check
     * @return true if label name is severity, else false
     */
    private boolean isSeverity(GHLabel label) {
        String name = toLetterOnlyLowerCase(label.getName());
        boolean createIfNotFound = false;
        if (name.contains("severity")) {
            name = name.replace("severity", "");
            createIfNotFound = true;
        }
        for (Severity severity : pi.getSeverities()) {
            if (name.equals(toLetterOnlyLowerCase(severity.getName()))) {
                severity.setExternalId(label.getUrl());
                severity.setDescription(label.getColor());
                severity.setName(label.getName());
                return true;
            }
        }
        if (createIfNotFound) {
            Severity newSeverity = new Severity(label.getName(), severityDao.findByClass(SeverityClass.UNASSIGNED));
            newSeverity.setExternalId(label.getUrl());
            newSeverity.setDescription(label.getColor());
            pi.getSeverities().add(newSeverity);
            return true;
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
        boolean createIfNotFound = false;
        if (name.contains("priority")) {
            name = name.replace("priority", "");
            createIfNotFound = true;
        }
        for (Priority priority : pi.getPriorities()) {
            if (name.equals(toLetterOnlyLowerCase(priority.getName()))) {
                priority.setExternalId(label.getUrl());
                priority.setDescription(label.getColor());
                priority.setName(label.getName());
                return true;
            }
        }
        if (createIfNotFound) {
            Priority newPriority = new Priority(label.getName(), priorityDao.findByClass(PriorityClass.UNASSIGNED));
            newPriority.setExternalId(label.getUrl());
            newPriority.setDescription(label.getColor());
            pi.getPriorities().add(newPriority);
            return true;
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
        boolean createIfNotFound = false;
        if (name.contains("resolution")) {
            name = name.replace("resolution", "");
            createIfNotFound = true;
        }
        for (Resolution resolution : pi.getResolutions()) {
            if (name.equals(toLetterOnlyLowerCase(resolution.getName()))) {
                resolution.setExternalId(label.getUrl());
                resolution.setDescription(label.getColor());
                resolution.setName(label.getName());
                return true;
            }
        }
        if (createIfNotFound) {
            Resolution newResolution = new Resolution(label.getName(), resolutionDao.findByClass(ResolutionClass.UNASSIGNED));
            newResolution.setExternalId(label.getUrl());
            newResolution.setDescription(label.getColor());
            pi.getResolutions().add(newResolution);
            return true;
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
                type.setExternalId(label.getUrl());
                type.setDescription(label.getColor());
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
