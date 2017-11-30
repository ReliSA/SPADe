package cz.zcu.kiv.spade.pumps;

import cz.zcu.kiv.spade.App;
import cz.zcu.kiv.spade.dao.*;
import cz.zcu.kiv.spade.dao.jpa.*;
import cz.zcu.kiv.spade.domain.*;
import cz.zcu.kiv.spade.domain.abstracts.ProjectSegment;
import cz.zcu.kiv.spade.domain.enums.*;
import cz.zcu.kiv.spade.load.DBInitializer;
import cz.zcu.kiv.spade.pumps.abstracts.IssueTrackingPump;

import javax.persistence.EntityManager;
import java.io.File;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Generic data pump
 *
 * @author Petr Pícha
 */
public abstract class DataPump<RootObjectType> {

    /** root object of the project's representation in a tool (repository/project) */
    protected RootObjectType rootObject;
    /** URL of the project */
    protected String projectHandle;
    /** username for authenticated login */
    protected String username;
    /** password for authenticated login */
    protected String password;
    /** private key location for authenticated login */
    protected String privateKeyLoc;
    /** Project Instance to store all the mined data in */
    protected ProjectInstance pi;
    /** ALM tool of mined Project Instance */
    protected Tool tool;
    /** DAO object for handling Tool Instance */
    private ToolInstanceDAO toolDao;
    /** DAO object for handling Status Classification instances */
    protected StatusClassificationDAO statusDao;
    /** DAO object for handling Priority Classification instances */
    protected PriorityClassificationDAO priorityDao;
    /** DAO object for handling Relation Classification instances */
    protected RelationClassificationDAO relationDao;
    /** DAO object for handling Resolution Classification instances */
    protected ResolutionClassificationDAO resolutionDao;
    /** DAO object for handling Work Unit Type Classification instances */
    protected WorkUnitTypeClassificationDAO typeDao;
    /** DAO object for handling Severity Classification instances */
    protected SeverityClassificationDAO severityDao;
    /** DAO object for handling Role Classification instances */
    protected RoleClassificationDAO roleDao;
    protected EntityManager entityManager;

    /**
     * @param projectHandle URL of the project instance
     * @param privateKeyLoc private key location for authenticated login
     * @param username      username for authenticated login
     * @param password      password for authenticated login
     */
    public DataPump(String projectHandle, String privateKeyLoc, String username, String password) {
        this.projectHandle = projectHandle;
        this.privateKeyLoc = privateKeyLoc;
        this.username = username;
        this.password = password;
        this.rootObject = init();
    }

    /**
     * loads root object of the project's representation in a tool (repository/project)
     */
    protected abstract RootObjectType init();

    /**
     * deletes temporary directory
     *
     * @param file temporary directory
     */
    private static void deleteTempDir(File file) {
        if (file.exists()) {
            if (file.isDirectory()) {
                File[] fileList = file.listFiles();
                if (fileList != null) {
                    for (File f : fileList) {
                        deleteTempDir(f);
                    }
                }
            }
            if (!file.delete()) System.out.println("Delete TMP DIR !!!");
        }
    }

    /**
     * gathers all data needed from project instance
     * @param em JPA entitny manager for accessing the database
     * @return ProjectInstrance with all data
     */
    public ProjectInstance mineData(EntityManager em) {

        ProjectInstanceDAO piDao = new ProjectInstanceDAO_JPA(em);
        toolDao = new ToolInstanceDAO_JPA(em);
        relationDao = new RelationClassificationDAO_JPA(em);
        statusDao = new StatusClassificationDAO_JPA(em);
        priorityDao = new PriorityClassificationDAO_JPA(em);
        severityDao = new SeverityClassificationDAO_JPA(em);
        resolutionDao = new ResolutionClassificationDAO_JPA(em);
        typeDao = new WorkUnitTypeClassificationDAO_JPA(em);
        roleDao = new RoleClassificationDAO_JPA(em);

        piDao.deleteByUrl(projectHandle);

        pi = new ProjectInstance();
        pi.setUrl(projectHandle);
        pi.setName(getProjectName());
        pi.getProject().setName(getProjectName());

        this.entityManager = em;

        return pi;
    }

        return pi;
    }

    /**
     * assigns appropriate Tool Instance to the Project Instance;
     * checks whether the Tool Instance already exists in the database,
     * if not it creates it
     */
    protected void setToolInstance() {
        ToolInstance ti = toolDao.findByToolInstance(getServer(), tool);
        if (ti == null) {
            ti = new ToolInstance();
            ti.setExternalId(getServer());
            ti.setTool(tool);
        }

        pi.setToolInstance(ti);
    }

    /**
     * gets project name
     *
     * @return project name
     */
    public String getProjectName() {
        String ret;
        if (projectHandle.endsWith(App.GIT_SUFFIX)) {
            ret = projectHandle.substring(projectHandle.lastIndexOf("/") + 1, projectHandle.lastIndexOf(App.GIT_SUFFIX));
        } else {
            ret = projectHandle.substring(projectHandle.lastIndexOf("/") + 1);
        }
        return ret;
    }

    /**
     * returns a temporary directory location of this project instance data
     *
     * @return project's temporary directory
     */
    protected String getProjectDir() {
        String cut = cutProtocolAndUser();
        String withoutPort = getServer().split(":")[0] + cut.substring(cut.indexOf('/'));
        return withoutPort.substring(0, withoutPort.lastIndexOf(App.GIT_SUFFIX));
    }

    /**
     * cuts protocol and username (e.g. "ppicha@...") from project handle
     *
     * @return project URL without protocol and username
     */
    private String cutProtocolAndUser() {
        String withoutProtocol = projectHandle;
        if (withoutProtocol.contains("://")) withoutProtocol = withoutProtocol.split("://")[1];
        if (withoutProtocol.contains("@")) withoutProtocol = withoutProtocol.split("@")[1];
        return withoutProtocol;
    }

    /**
     * cuts a server name from project handle
     *
     * @return server the project is mined from
     */
    protected String getServer() {
        String cut = cutProtocolAndUser();
        return cut.substring(0, cut.indexOf('/'));
    }

    /**
     * gets a new comparator instance for comparing Commit instances based on creation and commit dates
     * @return a new Commit comparator
     */
    private static Comparator<Configuration> getConfigurationByDateComparator() {
        return new Comparator<Configuration>() {
            @Override
            public int compare(Configuration o1, Configuration o2) {
                if ((o1 instanceof Commit) && (o2 instanceof Commit)) {
                    Commit c1 = (Commit) o1;
                    Commit c2 = (Commit) o2;
                    return compareCommits(c1, c2);
                }
                else return o1.getCreated().compareTo(o2.getCreated());
            }

            /**
             * compares two Commit instances based on date of creation and commit date
             * @param o1 first commit
             * @param o2 second commit
             * @return comparisson result
             */
            private int compareCommits(Commit o1, Commit o2) {

                int ret = o1.getCommitted().compareTo(o2.getCommitted());
                if (ret != 0) return ret;
                else return o1.getCreated().compareTo(o2.getCreated());
            }
        };
    }

    /**
     * gets substrings fitting given regular expression in a string
     * @param text text to look into
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
     * tries to find a Relation instance used in the Project corresponding with a given string,
     * and if it fails, creates and adds a new one (with a UNNASSIGNED class)
     * @param name Relation name
     * @return found or new Relation instance
     */
    protected Relation resolveRelation(String name) {

        for (Relation relation : pi.getRelations()) {
            if (toLetterOnlyLowerCase(name).equals(toLetterOnlyLowerCase(relation.getName()))) {
                relation.setName(name);

                return relation;
            }
        }
        Relation newRelation = new Relation(name, relationDao.findByClass(RelationClass.UNASSIGNED));
        pi.getRelations().add(newRelation);
        return newRelation;
    }

    /**
     * mines all mentions of other Work Items in a given items's description
     * and links mentioned items to the given one
     * @param item given Work Item instance
     */
    protected  void mineAllMentionedItems(WorkItem item) {
        mineAllMentionedItems(item, item.getDescription());
    }

    /**
     * mines all mentions of Work Items in a given string
     * and links mentioned items to the given one
     * @param item given Work Item instance
     * @param text text to search mentions in
     */
    protected void mineAllMentionedItems(WorkItem item, String text){
        mineMentionedUnits(item, text);
        mineAllMentionedCommits(item, text);
    }

    /**
     * mines all mentions of other Work Items (possibly to be found in Git ropository data}
     * in a given items's description and links mentioned items to the given one
     * @param item given Work Item instance
     */
    protected  void mineAllMentionedItemsGit(WorkItem item) {
        mineAllMentionedItemsGit(item, item.getDescription());
    }

    /**
     * mines all mentions of Work Items (possibly to be found in Git ropository data}
     * in a given string and links mentioned items to the given one
     * @param item given Work Item instance
     * @param text text to search mentions in
     */
    protected void mineAllMentionedItemsGit(WorkItem item, String text){
        mineMentionedUnits(item, text);
        mineMentionedGitCommits(item, text);
    }

    /*protected  void mineAllMentionedItemsSvn(WorkItem item) {
        mineAllMentionedItemsSvn(item, item.getDescription());
    }

    protected void mineAllMentionedItemsSvn(WorkItem item, String text){
        mineMentionedUnits(item, text);
        mineMentionedSvnCommits(item, text);
    }

    protected void mineMentionedUnits(WorkItem item) {
        mineMentionedUnits(item, item.getDescription());
    }*/

    /**
     * mines all mentions of Work Unit in a given string and links mentioned units to the given Work Item
     * @param item given Work Item instance
     * @param text text to search mentions in
     */
    protected void mineMentionedUnits(WorkItem item, String text) {
        for (String mention : mineMentions(text, IssueTrackingPump.WU_MENTION_REGEX)) {
            if (pi.getProject().containsUnit(mention)) {
                WorkUnit mentioned = pi.getProject().getUnit(mention);
                generateMentionRelation(item, mentioned);
            }
        }
    }

    /*protected void mineMentionedCommits(WorkItem item) {
        mineMentionedGitCommits(item, item.getDescription());
        mineMentionedSvnCommits(item, item.getDescription());
    }*/

    /**
     * mines all mentions of Git or SVN commits in a given string and links mentioned commits to the given Work Item
     * @param item given Work Item instance
     * @param text text to search mentions in
     */
    private void mineAllMentionedCommits(WorkItem item, String text) {
        mineMentionedGitCommits(item, text);
        mineMentionedSvnCommits(item, text);
    }

    /**
     * mines all mentions of Git commits in a given string and links mentioned commits to the given Work Item
     * @param item given Work Item instance
     * @param text text to search mentions in
     */
    protected void mineMentionedGitCommits(WorkItem item, String text) {
        mineMentionedItemsCommit(item, text, App.GIT_COMMIT_REGEX);
    }

    /**
     * mines all mentions of SVN commits in a given string and links mentioned commits to the given Work Item
     * @param item given Work Item instance
     * @param text text to search mentions in
     */
    private void mineMentionedSvnCommits(WorkItem item, String text) {
        mineMentionedItemsCommit(item, text, App.SVN_REVISION_REGEX);
    }

    /**
     * mines all mentions of Git or SVN (base on the given regular expression) commits
     * in a given string and links mentioned commits to the given Work Item
     * @param item given Work Item instance
     * @param text text to search mentions in
     * @param regex regular expression for mentions to look for
     */
    private void mineMentionedItemsCommit(WorkItem item, String text, String regex) {
        for (String mention : mineMentions(text, regex)) {
            if (pi.getProject().containsCommit(mention)) {
                Commit mentioned = pi.getProject().getCommit(mention);
                generateMentionRelation(item, mentioned);
            }
        }
    }

    /**
     * generaten a Work Item Relation instance between two given Work Items
     * @param mentioner the item where mentioned was found
     * @param mentionee mentioned item
     */
    protected void generateMentionRelation(WorkItem mentioner, WorkItem mentionee) {
        Relation mentionsRelation = resolveRelation("mentions");
        Relation mentionedByRelation = resolveRelation("mentioned by");

        mentioner.getRelatedItems().add(new WorkItemRelation(mentionee, mentionsRelation));
        mentionee.getRelatedItems().add(new WorkItemRelation(mentioner, mentionedByRelation));
    }

    /**
     * adds either a new Project-Person-Role to a collection or a new identity to an existing person based on name and email
     *
     * @param identity person's identity
     * @return added person or identified or enhanced (added identity) priviosly existing one
     */
    protected Person addPerson(Identity identity) {
        for (Person person : pi.getProject().getPeople()) {

            boolean foundSimilar = false;

            for (Identity member : person.getIdentities()) {

                if (identity.getExternalId() != null && member.getExternalId() != null) {
                    if (identity.getExternalId().equals(member.getExternalId())) {
                        return person;
                    }
                }

                boolean sameEmail = false;
                boolean sameName = false;

                if (!identity.getEmail().isEmpty()) {
                    sameEmail = identity.getEmail().equals(member.getEmail());
                }
                if (!identity.getName().isEmpty()) {
                    sameName = identity.getName().equals(member.getName());
                }

                if (sameEmail && sameName) {
                    return person;
                }
                if (sameEmail != sameName) {
                    if (identity.getEmail().equals(member.getEmail()) && identity.getName().equals(member.getName())) {
                        return person;
                    }
                    foundSimilar = true;
                }
                if (!identity.getDescription().isEmpty() &&
                        (identity.getDescription().equals(member.getName()) ||
                                identity.getDescription().equals(member.getDescription()))) {
                    foundSimilar = true;
                }
            }
            if (foundSimilar) {
                person.getIdentities().add(identity);
                if (identity.getName().length() > person.getName().length()) {
                    person.setName(identity.getName());
                }
                if (identity.getDescription().length() > person.getName().length()) {
                    person.setName(identity.getDescription());
                }
                return person;
            }
        }

        Person newPerson = new Person();
        if (identity.getDescription().isEmpty())  newPerson.setName(identity.getName());
        else newPerson.setName(identity.getDescription());
        newPerson.getIdentities().add(identity);

        pi.getProject().getPeople().add(newPerson);
        return newPerson;
    }

    /**
     * returns configurations in a form of a list sorted by date from earliest
     *
     * @return sorted list of configurations
     */
    protected List<Configuration> sortConfigsByDate() {
        List<Configuration> list = new ArrayList<>();
        list.addAll(pi.getProject().getConfigurations());
        list.sort(getConfigurationByDateComparator());
        return list;
    }

    /**
     * attaches correct artifacts, authors and dates to artifact changes and generates artifact external IDs unique inside a project
     *
     * @param list list of configurations to by cleaned up
     * @return list after clean up
     */
    protected List<Configuration> cleanUpCommitList(List<Configuration> list) {
        long externalId = 0;
        Map<String, Artifact> artifacts = new TreeMap<>();

        for (Configuration configuration : list) {
            if (!(configuration instanceof Commit)) continue;
            Commit commit = (Commit) configuration;
            for (WorkItemChange change : commit.getChanges()) {

                Artifact changed = (Artifact) change.getChangedItem();
                changed.setAuthor(commit.getAuthor());
                changed.setCreated(commit.getCreated());

                if (change.getName().equals("ADD") || change.getName().contains("COPY")) {
                    changed.setExternalId(Long.toString(externalId));
                    externalId++;
                    artifacts.put(changed.getUrl(), changed);

                } else {
                    for (FieldChange fChange : change.getFieldChanges()) {
                        if (fChange.getName().equals("url") && fChange.getOldValue() != null && artifacts.get(fChange.getOldValue()) != null) {
                            Artifact oldVersion = artifacts.get(fChange.getOldValue());
                            if (!change.getName().equals("DELETE")) {
                                oldVersion.setUrl(changed.getUrl());
                                oldVersion.setName(changed.getName());
                            }
                            artifacts.remove(changed.getUrl());
                            artifacts.put(oldVersion.getUrl(), oldVersion);
                            change.setChangedItem(oldVersion);
                        }
                    }
                }
            }

            /*Collection<Configuration> parents = new LinkedHashSet<>();
            for (Configuration parent : conf.getParents()) {
                Configuration trueParent = configurations.get(parent.getExternalId());
                trueParent.raiseChildrenCont();
                if (trueParent.getChildrenCont() > 1) trueParent.setBranchPoint(true);
                parents.add(trueParent);
            }
            conf.setParents(parents);
            if (conf.getParents().size() > 1) conf.setMergePoint(true);*/
        }
        return list;
    }

    /**
     * cuts off the path part of the file path
     *
     * @param path path of the file
     * @return simple name of the file
     */
    protected String stripFileName(String path) {
        if (path.contains("/")) return path.substring(path.lastIndexOf("/") + 1);
        else return path;
    }

    /**
     * adds correct authors to Work Item instances
     */
    protected void addWorkItemAuthors() {

        for (Configuration conf : pi.getProject().getConfigurations()) {
            for (WorkItemChange change : conf.getChanges()) {
                if (change.getName().equals("ADD")) {
                    change.getChangedItem().setAuthor(conf.getAuthor());
                }
            }
        }

    }

    /**
     * transforms the given string leaving only letters converted to lower case if necessary and cutting any other characters
     * @param anyString string to transform
     * @return letters only lower case version of the string
     */
    protected String toLetterOnlyLowerCase(String anyString) {
        StringBuilder compressed = new StringBuilder();
        for (int i = 0; i < anyString.length(); i++) {
            if (Character.isLetter(anyString.charAt(i))) {
                compressed.append(anyString.charAt(i));
            }
        }
        return compressed.toString().toLowerCase();
    }

    /**
     * performs the steps necessary to successfully close the instance in a clean way
     */
    public void close() {
        deleteTempDir(new File(App.ROOT_TEMP_DIR));
    }

    /**
     * assigns default sets of enumeration values (priorities, severities, statuses, resolutions and Work Unit types) to the Project Instance
     */
    private void assignDefaultEnums() {
        for (WorkUnit unit : pi.getProject().getUnits()) {
            assignDefaultPriority(unit);
            assignDefaultSeverity(unit);
            assignDefaultStatus(unit);
            assignDefaultResolution(unit);
            assignDefaultWuType(unit);
        }
        for (Person person : pi.getProject().getPeople()) {
            assignDefaultRole(person);
        }
    }

    private void assignDefaultRole(Person person) {
        if (person.getRoles().isEmpty()) {
            for (Role role : pi.getRoles()) {
                if (role.getName().equals("member")) {
                    person.getRoles().add(role);
                    return;
                }
            }
            Role defaultRole = new Role("member", roleDao.findByClass(RoleClass.TEAMMEMBER));
            pi.getRoles().add(defaultRole);
            person.getRoles().add(defaultRole);
        }
    }

    /**
     * assigns a default priority ("unassigned") to the given Work Unit instance;
     * creates and adds this priority to the Project if necessary
     * @param unit given Work Unit instance
     */
    private void assignDefaultPriority(WorkUnit unit) {
        if (unit.getPriority() == null) {
            for (Priority priority : pi.getPriorities()) {
                if (priority.getName().equals("unassigned")) {
                    unit.setPriority(priority);
                    return;
                }
            }
            Priority defaultPriority = new Priority("unassigned", priorityDao.findByClass(PriorityClass.UNASSIGNED));
            pi.getPriorities().add(defaultPriority);
            unit.setPriority(defaultPriority);
        }
    }

    /**
     * assigns a default severity ("unassigned") to the given Work Unit instance;
     * creates and adds this severity to the Project if necessary
     * @param unit given Work Unit instance
     */
    private void assignDefaultSeverity(WorkUnit unit) {
        if (unit.getSeverity() == null) {
            for (Severity severity : pi.getSeverities()) {
                if (severity.getName().equals("unassigned")) {
                    unit.setSeverity(severity);
                    return;
                }
            }
            Severity defaultSeverity = new Severity("unassigned", severityDao.findByClass(SeverityClass.UNASSIGNED));
            pi.getSeverities().add(defaultSeverity);
            unit.setSeverity(defaultSeverity);
        }
    }

    /**
     * assigns a default status ("unassigned") to the given Work Unit instance;
     * creates and adds this status to the Project if necessary
     * @param unit given Work Unit instance
     */
    private void assignDefaultStatus(WorkUnit unit) {
        if (unit.getStatus() == null) {
            for (Status status : pi.getStatuses()) {
                if (status.getName().equals("unassigned")) {
                    unit.setStatus(status);
                    return;
                }
            }
            Status defaultStatus = new Status("unassigned", statusDao.findByClass(StatusClass.UNASSIGNED));
            pi.getStatuses().add(defaultStatus);
            unit.setStatus(defaultStatus);
        }
    }

    /**
     * assigns a resolution to the given Work Unit instance - "invalid" if the Status class is INVALID,
     * default ("unassigned") otherwise if Status class is CLOSED;
     * creates and adds the default resolution to the Project if necessary
     * @param unit given Work Unit instance
     */
    private void assignDefaultResolution(WorkUnit unit) {
        if (unit.getStatus().getClassification().getaClass().equals(StatusClass.INVALID)) {
            for (Resolution resolution : pi.getResolutions()) {
                if (resolution.getClassification().getaClass().equals(ResolutionClass.INVALID)) {
                    unit.setResolution(resolution);
                    return;
                }
            }
        }
        if (unit.getStatus().getClassification().getSuperClass().equals(StatusSuperClass.OPEN)) return;
        if (unit.getResolution() == null) {
            for (Resolution resolution : pi.getResolutions()) {
                if (resolution.getName().equals("unassigned")) {
                    unit.setResolution(resolution);
                    return;
                }
            }
            Resolution defaultResolution = new Resolution("unassigned", resolutionDao.findByClass(ResolutionClass.UNASSIGNED));
            pi.getResolutions().add(defaultResolution);
            unit.setResolution(defaultResolution);
        }
    }

    /**
     * assigns a default Work Unit type ("unassigned") to the given Work Unit instance;
     * creates and adds this status to the Project if necessary
     * @param unit given Work Unit instance
     */
    private void assignDefaultWuType(WorkUnit unit) {
        if (unit.getType() == null) {
            for (WorkUnitType type : pi.getWuTypes()) {
                if (type.getName().equals("unassigned")) {
                    unit.setType(type);
                    return;
                }
            }
            WorkUnitType defaultType = new WorkUnitType("unassigned", typeDao.findByClass(WorkUnitTypeClass.UNASSIGNED));
            pi.getWuTypes().add(defaultType);
            unit.setType(defaultType);
        }
    }

    /**
     * adds a new Status ("deleted") to the Work Units which have apparently been deleted from the project
     * (a mention of them exist, but URL can't be found); if at least one such unit exists,
     * it adds the new status to the Project instance as well
     */
    private void addDeletedStatus() {
        Status delStatus = new Status("deleted", statusDao.findByClass(StatusClass.DELETED));
        boolean add = true;
        for (WorkUnit unit : pi.getProject().getUnits()) {
            if (unit.getUrl().isEmpty()) {
                if (add) {
                    pi.getStatuses().add(delStatus);
                    add = false;
                }
                unit.setStatus(delStatus);
            }
        }
    }

    /**
     * mines all the mentions of WorkItems from other WorkItem's data and creates links where necessary
     */
    protected abstract void mineMentions();

    /**
     * performs a few tasks at the end of mining`
     * add the "deleted" status to possibly deleted WorkUnits found,
     * assigns default enumeration values to WorkUnits with none set yet,
     * mines mentions among the WorkItems
     */
    protected void finalTouches() {
        addDeletedStatus();
        assignDefaultEnums();
        mineMentions();
    }

    /**
     * mines a configuration/action of creating an issue
     * @param unit WorkUnit (issue)
     */
    protected void generateCreationConfig(WorkUnit unit) {

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

    /**
     * matches the issue priority to one of the values used in the project
     * @param name priority value
     * @return corresponding Priority instance or null
     */
    protected Priority resolvePriority(String name) {
        for (Priority priority : pi.getPriorities()) {
            if (name.equals(priority.getName())) {
                return priority;
            }
        }
        return null;
    }

    /**
     * matches the issue type to one of the values used in the project
     * @param name type value
     * @return corresponding WorkUnitType instance or null
     */
    protected WorkUnitType resolveType(String name) {
        for (WorkUnitType type : pi.getWuTypes()) {
            if (name.equals(type.getName())) {
                return type;
            }
        }
        return null;
    }

    /**
     * matches the issue status to one of the values used in the project
     * @param name status value
     * @return corresponding Status instance or null
     */
    protected Status resolveStatus(String name) {
        for (Status status : pi.getStatuses()) {
            if (name.equals(status.getName())) {
                return status;
            }
        }
        return null;
    }

    /**
     * matches the issue severity to one of the values used in the project
     * @param name severity value
     * @return corresponding Severity instance or null
     */
    protected Severity resolveSeverity(String name) {
        for (Severity severity : pi.getSeverities()) {
            if (name.equals(severity.getName())) {
                return severity;
            }
        }
        return null;
    }

    /**
     * matches the issue resolution to one of the values used in the project
     * @param name resolution value
     * @return corresponding Resolution instance or null
     */
    protected Resolution resolveResolution(String name) {
        for (Resolution resolution : pi.getResolutions()) {
            if (name.equals(resolution.getName())) {
                return resolution;
            }
        }
        return null;
    }

    /**
     * gets a Role instance with a given name
     * @param name role name
     * @return Role instance or null
     */
    protected Role resolveRole(String name) {
        for (Role role : pi.getRoles()) {
            if (name.equals(role.getName())) {
                return role;
            }
        }
        return null;
    }

    /**
     * mines all the enumeration values used in the project
     */
    public void mineEnums() {
        new DBInitializer(entityManager).setDefaultEnums(pi);
        mineRoles();
        mineCategories();
        minePeople();
    }

    /**
     * mines custom issue categories (components, tags, labels) in project instance
     */
    protected abstract void mineCategories();

    /**
     * mines data of users with access to the project
     */
    protected abstract void minePeople();

    /**
     * mines all the roles used in the project
     */
    protected abstract void mineRoles();

    protected void mineContent() {
        mineEnums();
        mineTickets();

        Collection<ProjectSegment> iterations = mineIterations();
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
                    if (iteration instanceof Activity) {
                        Activity activity = (Activity) iteration;
                        unit.setActivity(activity);
                    }
                }
            }
        }
    }

    /**
     * mines all the tickets/issues in the project
     */
    protected abstract void mineTickets();

    /**
     * mines all iterations (milestones, phases, etc.) in the project and saves each one as Iteration, Phase and Activity
     * for future analysis
     * @return all project segments in all 3 forms
     */
    protected abstract Collection<ProjectSegment> mineIterations();
}
