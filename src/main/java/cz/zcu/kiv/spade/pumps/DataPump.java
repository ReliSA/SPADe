package cz.zcu.kiv.spade.pumps;

import cz.zcu.kiv.spade.App;
import cz.zcu.kiv.spade.dao.*;
import cz.zcu.kiv.spade.dao.jpa.*;
import cz.zcu.kiv.spade.domain.*;
import cz.zcu.kiv.spade.domain.enums.*;
import cz.zcu.kiv.spade.pumps.abstracts.IssueTrackingPump;
import cz.zcu.kiv.spade.pumps.impl.GitPump;

import javax.persistence.EntityManager;
import java.io.File;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Generic data pump
 */
public abstract class DataPump<RootObjectType> {

    /**
     * temporary directory to transfer necessary data into
     */
    protected static final String ROOT_TEMP_DIR = "repos\\";
    private static final String SVN_REVISION_REGEX = "(?<=^r|\\Wr|_r|$r)\\d{1,4}(?=\\W|_|^|$)";

    /**
     * root object of the project's representation in a tool (repository/project)
     */
    protected RootObjectType rootObject;
    /**
     * URL of the project
     */
    protected String projectHandle;
    /**
     * username for authenticated login
     */
    protected String username;
    /**
     * password for authenticated login
     */
    protected String password;
    /**
     * private key location for authenticated login
     */
    protected String privateKeyLoc;

    protected ProjectInstance pi;

    protected Tool tool;

    private ToolInstanceDAO toolDao;
    private RelationClassificationDAO relationDao;
    protected StatusClassificationDAO statusDao;
    private PriorityClassificationDAO priorityDao;
    private SeverityClassificationDAO severityDao;
    private ResolutionClassificationDAO resolutionDao;
    private WorkUnitTypeClassificationDAO typeDao;

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
     *
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

        piDao.deleteByUrl(projectHandle);

        pi = new ProjectInstance();
        pi.setUrl(projectHandle);
        pi.setName(getProjectName());
        pi.getProject().setName(getProjectName());

        return pi;
    }

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

            private int compareCommits(Commit o1, Commit o2) {

                int ret = o1.getCommitted().compareTo(o2.getCommitted());
                if (ret != 0) return ret;
                else return o1.getCreated().compareTo(o2.getCreated());
            }
        };
    }

    private Set<String> mineMentions(String text, String regex) {
        Set<String> mentions = new HashSet<>();

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);

        while (matcher.find()) {
            mentions.add(matcher.group());
            text = text.substring(matcher.end());
            matcher = matcher.reset(text);
        }
        return mentions;
    }

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

    protected  void mineAllMentionedItems(WorkItem item) {
        mineAllMentionedItems(item, item.getDescription());
    }

    protected void mineAllMentionedItems(WorkItem item, String text){
        mineMentionedUnits(item, text);
        mineAllMentionedCommits(item, text);
    }

    protected  void mineAllMentionedItemsGit(WorkItem item) {
        mineAllMentionedItemsGit(item, item.getDescription());
    }

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

    private void mineMentionedUnits(WorkItem item, String text) {
        for (String mention : mineMentions(text, IssueTrackingPump.WU_MENTION_REGEX)) {
            WorkUnit mentioned = pi.getProject().addUnit(new WorkUnit(Integer.parseInt(mention)));
            generateMentionRelation(item, mentioned);
        }
    }

    /*protected void mineMentionedCommits(WorkItem item) {
        mineMentionedGitCommits(item, item.getDescription());
        mineMentionedSvnCommits(item, item.getDescription());
    }*/

    private void mineAllMentionedCommits(WorkItem item, String text) {
        mineMentionedGitCommits(item, text);
        mineMentionedSvnCommits(item, text);
    }

    private void mineMentionedGitCommits(WorkItem item, String text) {
        mineMentionedItemsCommit(item, text, GitPump.GIT_COMMIT_REGEX);
    }

    private void mineMentionedSvnCommits(WorkItem item, String text) {
        mineMentionedItemsCommit(item, text, SVN_REVISION_REGEX);
    }

    private void mineMentionedItemsCommit(WorkItem item, String text, String regex) {
        for (String mention : mineMentions(text, regex)) {
            Commit mentioned = pi.getProject().addCommit(new Commit(mention));
            generateMentionRelation(item, mentioned);
        }
    }

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
        newPerson.setName(identity.getName());
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
                        if (fChange.getName().equals("url")) {
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

    protected void addWorkItemAuthors() {

        for (Configuration conf : pi.getProject().getConfigurations()) {
            for (WorkItemChange change : conf.getChanges()) {
                if (change.getName().equals("ADD")) {
                    change.getChangedItem().setAuthor(conf.getAuthor());
                }
            }
        }

    }

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
        deleteTempDir(new File(DataPump.ROOT_TEMP_DIR));
    }

    protected void assignDefaultEnums() {
        for (WorkUnit unit : pi.getProject().getUnits()) {
            assignDefaultPriority(unit);
            assignDefaultSeverity(unit);
            assignDefaultStatus(unit);
            assignDefaultResolution(unit);
            assignDefaultWuType(unit);
        }
    }

    private void assignDefaultPriority(WorkUnit unit) {
        if (unit.getPriority() == null) {
            for (Priority priority : pi.getPriorities()) {
                if (priority.getName().equals("unassigned")) {
                    unit.setPriority(priority);
                    return;
                }
            }
        }
        Priority defaultPriority = new Priority("unassigned", priorityDao.findByClass(PriorityClass.UNASSIGNED));
        pi.getPriorities().add(defaultPriority);
        unit.setPriority(defaultPriority);
    }

    private void assignDefaultSeverity(WorkUnit unit) {
        if (unit.getSeverity() == null) {
            for (Severity severity : pi.getSeverities()) {
                if (severity.getName().equals("unassigned")) {
                    unit.setSeverity(severity);
                    break;
                }
            }
        }
        Severity defaultSeverity = new Severity("unassigned", severityDao.findByClass(SeverityClass.UNASSIGNED));
        pi.getSeverities().add(defaultSeverity);
        unit.setSeverity(defaultSeverity);
    }

    private void assignDefaultStatus(WorkUnit unit) {
        if (unit.getStatus() == null) {
            for (Status status : pi.getStatuses()) {
                if (status.getName().equals("unassigned")) {
                    unit.setStatus(status);
                    break;
                }
            }
        }
        Status defaultStatus = new Status("unassigned", statusDao.findByClass(StatusClass.UNASSIGNED));
        pi.getStatuses().add(defaultStatus);
        unit.setStatus(defaultStatus);
    }

    private void assignDefaultResolution(WorkUnit unit) {
        if (unit.getStatus().getClassification().getaClass().equals(StatusClass.INVALID)) {
            for (Resolution resolution : pi.getResolutions()) {
                if (resolution.getClassification().getaClass().equals(ResolutionClass.INVALID)) {
                    unit.setResolution(resolution);
                    break;
                }
            }
        }
        if (unit.getStatus().getClassification().getSuperClass().equals(StatusSuperClass.OPEN)) return;
        if (unit.getResolution() == null) {
            for (Resolution resolution : pi.getResolutions()) {
                if (resolution.getName().equals("unassigned")) {
                    unit.setResolution(resolution);
                    break;
                }
            }
        }
        Resolution defaultResolution = new Resolution("unassigned", resolutionDao.findByClass(ResolutionClass.UNASSIGNED));
        pi.getResolutions().add(defaultResolution);
        unit.setResolution(defaultResolution);
    }

    private void assignDefaultWuType(WorkUnit unit) {
        if (unit.getType() == null) {
            for (WorkUnitType type : pi.getWuTypes()) {
                if (type.getName().equals("unassigned")) {
                    unit.setType(type);
                    break;
                }
            }
        }
        WorkUnitType defaultType = new WorkUnitType("unassigned", typeDao.findByClass(WorkUnitTypeClass.UNASSIGNED));
        pi.getWuTypes().add(defaultType);
        unit.setType(defaultType);
    }

    protected void addDeletedStatus() {
        Status delStatus = new Status("deleted", statusDao.findByClass(StatusClass.DELETED));
        boolean add = true;
        for (WorkUnit unit : pi.getProject().getUnits()) {
            if (unit.getUrl() == null) {
                if (add) {
                    pi.getStatuses().add(delStatus);
                    add = false;
                }
                unit.setStatus(delStatus);
            }
        }
    }

    protected FieldChange generateFieldChange(String field, String oldValue, String newValue) {
        FieldChange fChange = new FieldChange();
        fChange.setName(field);
        fChange.setOldValue(oldValue);
        fChange.setNewValue(newValue);

        return fChange;
    }
}
