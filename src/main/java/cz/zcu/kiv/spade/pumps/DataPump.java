package cz.zcu.kiv.spade.pumps;

import cz.zcu.kiv.spade.domain.*;

import java.io.File;
import java.io.PrintStream;
import java.util.*;

/**
 * Generic data pump
 */
public abstract class DataPump<RootObjectType> {

    /**
     * temporary directory to transfer necessary data into
     */
    protected static final String ROOT_TEMP_DIR = "D:/repos/";
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

    private static Comparator<Configuration> getConfigurationByDateComparator() {
        return new Comparator<Configuration>() {
            @Override
            public int compare(Configuration o1, Configuration o2) {
                int ret = o1.getCommitted().compareTo(o2.getCommitted());
                if (ret != 0) return ret;
                else return o1.getCreated().compareTo(o2.getCreated());
            }
        };
    }

    /**
     * loads root object of the project's representation in a tool (repository/project)
     */
    protected abstract RootObjectType init();

    /**
     * gathers all data needed from project instance
     *
     * @return ProjectInstrance with all data
     */
    public abstract ProjectInstance mineData();

    /**
     * performs the steps necessary to successfully close the instance in a clean way
     */
    public void close() {
        deleteTempDir(new File(DataPump.ROOT_TEMP_DIR));
    }

    /**
     * adds either a new Project-Person-Role to a collection or a new identity to an existing person based on name and email
     *
     * @param peopleColl collection to check for existence of a person/identity
     * @param identity   person's identity
     * @return added person or identified or enhanced (added identity) priviosly existing one
     */
    protected Person addPerson(Collection<Person> peopleColl, Identity identity) {
        // TODO disambiguation - heuristic for aggregating people based on name and email
        /*
        prijmeni, krestni|k. [stredni|s.] = *, * [*] = contains ","; 2/3 parts
        prijmeni krestni s.|k. s.|k. = * *.* = ends with "."; 2/3 parts
        krestni|k. [stredni|s.] prijmeni = * [*] * = else
        */
        /*
        kspp
        ppks
        pp
        kksp
        */
        for (Person person : peopleColl) {
            boolean foundSimilar = false;
            for (Identity ident : person.getIdentities()) {

                boolean sameEmail = ident.getEmail().equals(identity.getEmail());
                boolean sameName = ident.getName().equals(identity.getName());

                if (sameName && sameEmail) return person;
                if (sameName != sameEmail) {
                    foundSimilar = true;
                }
            }
            if (foundSimilar) {
                person.getIdentities().add(identity);
                if (identity.getName().length() > person.getName().length()) {
                    person.setName(identity.getName());
                }
                return person;
            }
        }

        Person newPerson = new Person();
        newPerson.setName(identity.getName());
        newPerson.getIdentities().add(identity);

        peopleColl.add(newPerson);
        return newPerson;
    }

    /**
     * returns configurations in a form of a list sorted by date from earliest
     *
     * @return sorted list of configurations
     */
    protected List<Configuration> sortConfigsByDate(Collection<Configuration> configurations) {
        List<Configuration> list = new ArrayList<>();
        list.addAll(configurations);
        list.sort(getConfigurationByDateComparator());
        return list;
    }

    /**
     * attaches correct artifacts, authors and dates to artifact changes and generates artifact external IDs unique inside a project
     *
     * @param list list of configurations to by cleaned up
     * @return list after clean up
     */
    protected List<Configuration> cleanUpConfList(List<Configuration> list) {
        long externalId = 0;
        Map<String, Artifact> artifacts = new TreeMap<>();

        for (Configuration conf : list) {
            for (WorkItemChange change : conf.getChanges()) {

                Artifact changed = (Artifact) change.getChangedItem();
                changed.setAuthor(conf.getAuthor());
                changed.setCreated(conf.getCreated());

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
     * returns a temporary directory location of this project instance data
     *
     * @return project's temporary directory
     */
    protected String getProjectDir() {
        String cut = cutProtocolAndUser();
        String withoutPort = getServer().split(":")[0] + cut.substring(cut.indexOf('/'));
        return withoutPort.substring(0, withoutPort.lastIndexOf(".git"));
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
     * gets project name
     *
     * @return project name
     */
    public String getProjectName() {
        return projectHandle.substring(projectHandle.lastIndexOf("/") + 1, projectHandle.lastIndexOf(".git"));
    }

    /**
     * prints data report to an output text file
     *
     * @param pi     project instance with all the necessary data
     * @param stream print destination
     */
    public ProjectInstance printReport(ProjectInstance pi, PrintStream stream) {

        stream.println();
        stream.println("Project: " + pi.getProject().getName());
        stream.println("Tool: " + pi.getToolInstance().getTool() + " (" + pi.getToolInstance().getExternalId() + ")");
        stream.println("URL: " + pi.getUrl());
        stream.println("Description: " + pi.getProject().getDescription());
        stream.println("Start date: " + pi.getProject().getStartDate());

        Collection<String> tags = new LinkedHashSet<>();
        Collection<String> branches = new LinkedHashSet<>();
        Collection<Person> people = new LinkedHashSet<>();

        stream.println("Configurations: " + pi.getProject().getConfigurations().size());
        for (Configuration conf : pi.getProject().getConfigurations()) {
            stream.println("\tSHA: " + conf.getName().substring(0, 7));
            for (Identity identity : conf.getAuthor().getIdentities()) {
                Person author = addPerson(people, identity);
                conf.setAuthor(author);
            }
            stream.println("\tAuthor: " + conf.getAuthor().getName());
            stream.println("\tCreated: " + conf.getCreated());
            stream.println("\tCommitted: " + conf.getCommitted());
            String tagList = "\tTags: ";
            for (VCSTag tag : conf.getTags()) {
                tagList = tagList.concat(tag.getName());
                tagList = tagList.concat(", ");
                tags.add(tag.getName());
            }
            if (tagList.endsWith(", ")) stream.println(tagList.substring(0, tagList.length() - 2));
            String branchList = "\tBranches: ";
            for (Branch branch : conf.getBranches()) {
                branchList = branchList.concat(branch.getName());
                if (branch.getIsMain()) {
                    branchList = branchList.concat(" (default)");
                    branches.add(branch.getName() + " (default)");
                } else {
                    branches.add(branch.getName());
                }
                branchList = branchList.concat(", ");
            }
            stream.println(branchList.substring(0, branchList.length() - 2));
            String formatted = conf.getDescription().replaceAll("\n\n", "\n").replaceAll("\n", "\n\t\t");
            stream.println("\tMsg: " + formatted.trim());
            stream.println("\tAssociated people:");
            for (ConfigPersonRelation rel : conf.getRelations()) {
                for (Identity identity : rel.getPerson().getIdentities()) {
                    Person person = addPerson(people, identity);
                    rel.setPerson(person);
                }
                stream.println("\t\t" + rel.getName() + ": " + rel.getPerson().getName());
            }
            String workUnitsList = "\tAssociated work units: ";
            for (WorkUnit wu : conf.getWorkUnits()) {
                workUnitsList = workUnitsList.concat(wu.getNumber() + ", ");
            }
            if (workUnitsList.endsWith(", ")) {
                workUnitsList = workUnitsList.substring(0, workUnitsList.length() - 2);
            }
            stream.println(workUnitsList);
            stream.println("\tChanged files: " + conf.getChanges().size());
            for (WorkItemChange change : conf.getChanges()) {
                stream.println("\t\t" + change.getName() + " " + change.getChangedItem().getName());
                stream.println("\t\t\t" + "File path: " + change.getChangedItem().getUrl());
                for (FieldChange field : change.getFieldChanges()) {
                    stream.println("\t\t\t" + field.getName() + " changed from: " + field.getOldValue() + " to: " + field.getNewValue());
                }
                stream.println("\t\t\t" + change.getDescription().replaceAll("\n", "\n\t\t\t"));

                if (change.getName().equals("ADD")) {
                    change.getChangedItem().setAuthor(conf.getAuthor());
                }
            }
            stream.println();
        }
        stream.println("Tags: " + tags.size() + " " + tags.toString());
        stream.println("Branches: " + branches.size() + " " + branches.toString());
        stream.println("Personnel: " + people.size());
        pi.getProject().setPeople(people);
        for (Person person : people) {
            pi.getProject().getPeople().add(person);
            String personString = "\t" + person.getName() + " (";
            for (Identity identity : person.getIdentities()) {
                personString += identity.getEmail() + ", ";
            }
            stream.println(personString.substring(0, personString.length() - 2) + ")");
        }

        stream.println();

        /*Map<String, Integer> brs = new HashMap<>();
        int i = 1;
        for (String b : branches) {
            brs.put(b, i);
            stream.println(i + " - " + b);
            i++;
        }
        stream.println();


        for (Configuration conf : pi.getProject().getConfigurations()) {
            for (Map.Entry<String, Integer> b : brs.entrySet()) {
                boolean found = false;
                for (Branch br : conf.getBranches()) {
                    if (b.getKey().contains(br.getName())) {
                        found = true;
                        break;
                    }
                }
                if (found) stream.print("x ");
                else stream.print("  ");
            }
            stream.print(" " + conf.getName().substring(0, 7) + " " + conf.getCommitted());
            //if (conf.isMergePoint()) stream.print(" merge");
            //if (conf.isBranchPoint()) stream.print(" branch");
            stream.println();
        }*/

        stream.flush();
        stream.close();
        return pi;
    }

    public void printWorkItemHistories(ProjectInstance pi, PrintStream stream) {
        Map<String, Artifact> artifacts = new TreeMap<>();
        for (Configuration conf : pi.getProject().getConfigurations()) {
            for (WorkItemChange change : conf.getChanges()) {
                if (!artifacts.containsKey(change.getChangedItem().getExternalId())) {
                    artifacts.put(change.getChangedItem().getExternalId(), (Artifact) change.getChangedItem());
                }
            }
        }
        Map<String, List<String>> artifactHistories = new TreeMap<>();
        for (String id : artifacts.keySet()) {
            artifactHistories.put(id, new ArrayList<>());
        }
        for (String artifactId : artifacts.keySet()) {
            for (Configuration conf : pi.getProject().getConfigurations()) {
                for (WorkItemChange change : conf.getChanges()) {
                    Artifact changedArtifact = (Artifact) change.getChangedItem();
                    if (artifactId.equals(changedArtifact.getExternalId())) {
                        artifactHistories.get(artifactId).add(conf.getCommitted() + "\t" + change.getName());
                    }
                }
            }
        }
        for (Artifact artifact : artifacts.values()) {
            stream.println(artifact.getName() + "\t" + artifact.getExternalId());
            for (String history : artifactHistories.get(artifact.getExternalId())) {
                stream.println("\t" + history);
            }
        }
    }
}
