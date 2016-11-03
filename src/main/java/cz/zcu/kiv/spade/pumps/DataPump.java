package cz.zcu.kiv.spade.pumps;

import cz.zcu.kiv.spade.domain.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.*;

/**
 * Generic data pump
 */
public abstract class DataPump {

    /**
     * temporary directory to transfer necessary data into
     */
    protected static final String ROOT_TEMP_DIR = "D:/repos/";

    /**
     * URL of the project
     */
    protected String projectHandle;
    /**
     * name of the project
     */
    protected String projectName;

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
     * project personnel gathered from this project instance
     */
    protected Collection<Person> people = new HashSet<>();
    /**
     * project configurations gathered from this project instance using SHA/revision number as key
     */
    protected Map<String, Configuration> configurations = new HashMap<>();


    /**
     * @param projectHandle URL of the project instance
     */
    public DataPump(String projectHandle) {
        this.projectHandle = projectHandle;
        this.projectName = projectHandle.substring(projectHandle.lastIndexOf("/") + 1, projectHandle.lastIndexOf(".git"));
    }

    /**
     * @param projectHandle URL of the project instance
     * @param username      username for authenticated login
     * @param password      password for authenticated login
     */
    public DataPump(String projectHandle, String username, String password) {
        this.projectHandle = projectHandle;
        this.projectName = projectHandle.substring(projectHandle.lastIndexOf("/") + 1, projectHandle.lastIndexOf(".git"));
        this.username = username;
        this.password = password;
    }

    /**
     * @param projectHandle URL of the project instance
     * @param privateKeyLoc private key location for authenticated login
     */
    public DataPump(String projectHandle, String privateKeyLoc) {
        this.projectHandle = projectHandle;
        this.projectName = projectHandle.substring(projectHandle.lastIndexOf("/") + 1, projectHandle.lastIndexOf(".git"));
        this.privateKeyLoc = privateKeyLoc;
    }

    /**
     * @param projectHandle URL of the project instance
     * @param privateKeyLoc private key location for authenticated login
     * @param username      username for authenticated login
     * @param password      password for authenticated login
     */
    public DataPump(String projectHandle, String privateKeyLoc, String username, String password) {
        this.projectHandle = projectHandle;
        this.projectName = projectHandle.substring(projectHandle.lastIndexOf("/") + 1, projectHandle.lastIndexOf(".git"));
        this.privateKeyLoc = privateKeyLoc;
        this.username = username;
        this.password = password;
    }

    /**
     * deletes temporary directory
     *
     * @param file temporary directory
     */
    protected static void deleteTempDir(File file) {
        if (file.isDirectory()) {
            File[] fileList = file.listFiles();
            if (fileList != null) {
                for (File f : fileList) {
                    deleteTempDir(f);
                }
            }
        }
        file.delete();
    }

    /**
     * gathers all data needed from project instance and stores it in private fields
     */
    public abstract void mineData();

    /**
     * loads root object of the project's representation in a tool (repository/project)
     */
    protected abstract void loadRootObject();

    /**
     * loads a map using commit's external ID as a key and a set of associated tags as a value
     *
     * @return map of tags per commit ID
     */
    protected abstract Map<String, Set<VCSTag>> loadTags();

    /**
     * mines data from all commits associated with a particular branch
     *
     * @param branch branch to mine commits from
     */
    protected abstract void mineCommits(Branch branch);

    /**
     * adds either a new person to a private collection or a new identity to an existing person based on name and email
     *
     * @param name  person's name
     * @param email person's email
     * @return added person
     */
    protected Person addPerson(String name, String email) {
        // TODO disambiguation - heuristic for aggregating people based on name and email
        for (Person person : people) {
            for (Identity identity : person.getIdentities()) {
                if (identity.getEmail().equals(email)) {
                    return person;
                }
            }
            if (person.getName().equals(name)) {
                Identity identity = new Identity();
                identity.setName(name);
                identity.setEmail(email);
                person.getIdentities().add(identity);
                return person;
            }
        }

        Identity identity = new Identity();
        identity.setName(name);
        identity.setEmail(email);

        Person newPerson = new Person();
        newPerson.setName(identity.getName());
        newPerson.getIdentities().add(identity);

        people.add(newPerson);
        return newPerson;
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
     * prints data report to an output text file
     *
     * @param pi project instance with all the necessary data
     */
    protected void printReport(ProjectInstance pi) {
        try {
            PrintStream stream = new PrintStream("D:/reports/" + projectName + ".txt");

            stream.println();
            stream.println("Project: " + pi.getProject().getName());
            stream.println("Start date: " + pi.getProject().getStartDate());
            stream.println("Tool: " + pi.getToolInstance().getTool() + " (" + pi.getToolInstance().getExternalId() + ")");
            stream.println("URL: " + pi.getUrl());
            stream.println("Personnel: " + pi.getProject().getPersonnel().size());
            for (Person person : pi.getProject().getPersonnel()) {
                String personString = "\t" + person.getName() + " (";
                for (Identity identity : person.getIdentities()) {
                    personString += identity.getEmail() + ", ";
                }
                stream.println(personString.substring(0, personString.length() - 2) + ")");
            }

            Set<String> tags = new HashSet<>();
            Set<String> branches = new HashSet<>();

            stream.println("Configurations: " + pi.getProject().getConfigurations().size());
            for (Configuration conf : pi.getProject().getConfigurations()) {
                stream.println("\tSHA: " + conf.getName().substring(0, 7));
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
                    stream.println("\t\t" + rel.getDescription() + ": " + rel.getPerson().getName());
                }
                String workUnitsList = "\tAssociated work units: ";
                for (WorkUnit wu : conf.getWorkUnits()) {
                    workUnitsList = workUnitsList.concat(wu.getNumber() + ", ");
                }
                if (workUnitsList.endsWith(", ")) {
                    workUnitsList = workUnitsList.substring(0, workUnitsList.length() - 2);
                }
                stream.println(workUnitsList);
                stream.println("\tArtifacts: " + conf.getArtifacts().size());
                stream.println("\tChanged files: " + conf.getChanges().size());
                for (WorkItemChange change : conf.getChanges()) {
                    stream.println("\t\t" + change.getName() + " " + change.getChangedItem().getName());
                    stream.println("\t\t\t" + change.getDescription().replaceAll("\n", "\n\t\t\t"));
                }
                stream.println();
            }
            stream.println("Tags: " + tags.size() + " " + tags.toString());
            stream.println("Branches: " + branches.size() + " " + branches.toString());

            stream.println();

            Map<String, Integer> brs = new HashMap<>();
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
                        if (b.getKey().contains(br.getName())) found = true;
                    }
                    if (found) stream.print("x ");
                    else stream.print("  ");
                }
                stream.println(" " + conf.getName().substring(0, 7) + " " + conf.getCommitted());
            }

            stream.flush();
            stream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
