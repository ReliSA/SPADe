package cz.zcu.kiv.spade;

import cz.zcu.kiv.spade.dao.ProjectInstanceDAO;
import cz.zcu.kiv.spade.dao.ToolInstanceDAO;
import cz.zcu.kiv.spade.dao.WorkUnitDAO;
import cz.zcu.kiv.spade.dao.jpa.ProjectInstanceDAO_JPA;
import cz.zcu.kiv.spade.dao.jpa.ToolInstanceDAO_JPA;
import cz.zcu.kiv.spade.dao.jpa.WorkUnitDAO_JPA;
import cz.zcu.kiv.spade.domain.ProjectInstance;
import cz.zcu.kiv.spade.domain.enums.*;
import cz.zcu.kiv.spade.gui.utils.EnumStrings;
import cz.zcu.kiv.spade.load.DBInitializer;
import cz.zcu.kiv.spade.load.Loader;
import cz.zcu.kiv.spade.output.CocaexFilePrinter;
import cz.zcu.kiv.spade.output.CodefacePrinter;
import cz.zcu.kiv.spade.output.StatsPrinter;
import cz.zcu.kiv.spade.output.TimelineFilePrinter;
import cz.zcu.kiv.spade.pumps.DataPump;
import cz.zcu.kiv.spade.pumps.impl.GitHubPump;
import cz.zcu.kiv.spade.pumps.impl.GitPump;
import cz.zcu.kiv.spade.pumps.impl.RedminePump;
import org.json.JSONException;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Class for handling most of the application logic in SPADe
 *
 * @author Petr Pícha
 */
public class App {

    /** a format for timestamps */
    public static final SimpleDateFormat TIMESTAMP = new SimpleDateFormat("HH:mm:ss.SSS");
    /** the extension of Git repository URLs */
    public static final String GIT_SUFFIX = ".git";
    /** regular expression for Git commit hash */
    public static final String GIT_COMMIT_REGEX = "(?<=^r|\\Wr|_r|$r)\\[a-f0-9]{7}(?=\\W|_|^|$)";
    /** temporary directory to transfer necessary data into */
    public static final String ROOT_TEMP_DIR = "repos\\";
    /** regular expression for SVN revision marker */
    public static final String SVN_REVISION_REGEX = "(?<=^r|\\Wr|_r|$r)\\d{1,4}(?=\\W|_|^|$)";
    /** a prefix of a GitHub repository URL */
    private static final String GITHUB_PREFIX = "https://github.com/";

    /** a JPA persistance unit for updating the SPADe database */
    private static final String PERSISTENCE_UNIT_UPDATE = "update";
    /** a JPA persistance unit for creating a blank SPADe database */
    private static final String PERSISTENCE_UNIT_CREATE = "create";

    /** a stream to print log messages into */
    public static PrintStream log;
    /** JPA entity manager for updating the SPADe database */
    private EntityManager updateManager;
    /** JPA entity manager for creating a blank SPADe database */
    private EntityManager createManager;

    /**
     * default constructor, sets default update entity manager and log print stream
     */
    public App() {
        EntityManagerFactory factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_UPDATE);
        this.updateManager = factory.createEntityManager();
        log = System.out;
    }

    /**
     * a constructor, sets default update entity manager and a given log print stream
     * @param logOutput stream for printing log messages
     */
    public App(PrintStream logOutput) {
        EntityManagerFactory factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_UPDATE);
        this.updateManager = factory.createEntityManager();
        log = logOutput;
    }

    /**
     * creates a blank SPADe database
     */
    public void createBlankDB() {
        printLogMsg("Initializing DB ...");

        if (createManager == null || !createManager.isOpen()) {
            EntityManagerFactory factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_CREATE);
            createManager = factory.createEntityManager();
        }

        new DBInitializer(createManager).initializeDatabase();

        printLogMsg("DB initialized");
    }

    /**
     * handles a lifeczcle of mining a project instance (mining, printing output files, uploading to database)
     * @param url project's URL to mine
     * @param loginResults a map containing username, password and private kez for accessing the project
     * @param toolName name of the tool storing the project data (all caps, e.g. GITHUB)
     */
    public void processProjectInstance(String url, Map<String, String> loginResults, String toolName) {
        printLogMsg("mining of " + url + " started...");
        long startTime = System.currentTimeMillis();

        ProjectInstance pi;
        if (toolName == null) pi = this.remineProjectInstance(url, loginResults);
        else pi = this.mineProjectInstance(url, loginResults, toolName);
        pi.getStats().setStart(startTime);
        printLogMsg("project instance " + pi.getUrl() + " mined");
        long miningTime = System.currentTimeMillis();
        pi.getStats().setMining(miningTime);

        this.printProjectInstance(pi);
        printLogMsg("project instance " + pi.getUrl() + " printed");
        long printingTime = System.currentTimeMillis();
        pi.getStats().setPrinting(printingTime);

        this.loadProjectInstance(pi);
        printLogMsg("project instance " + pi.getUrl() + " loaded");
        long loadingTime = System.currentTimeMillis();
        pi.getStats().setLoading(loadingTime);

        StatsPrinter statsPrinter = new StatsPrinter();
        statsPrinter.print(pi);
    }

    /**
     * mines the project data
     * @param url project's URL to mine
     * @param loginResults a map containing username, password and private kez for accessing the project
     * @param toolName name of the tool storing the project data (all caps, e.g. GITHUB)
     * @return a ProjectInstance instance with all the data mined
     */
    private ProjectInstance mineProjectInstance(String url, Map<String, String> loginResults, String toolName) {

        Tool tool = Tool.valueOf(toolName);

        DataPump pump = null;
        if (tool.equals(Tool.GIT)) {
            pump = new GitPump(url, loginResults.get("privateKey"), loginResults.get("username"), loginResults.get("password"));
        } else if (tool.equals(Tool.GITHUB)) {
            pump = new GitHubPump(url, loginResults.get("privateKey"), loginResults.get("username"), loginResults.get("password"));
        } else if (tool.equals(Tool.REDMINE)) {
            pump = new RedminePump(url, loginResults.get("privateKey"), loginResults.get("username"), loginResults.get("password"));
        } /*else if (tool.equals(Tool.SVN)) {

        } else if (tool.equals(Tool.BUGZILLA)) {

        } else if (tool.equals(Tool.JIRA)) {

        } else if (tool.equals(Tool.ASSEMBLA)) {

        } else if (tool.equals(Tool.RTC)) {

        }*/

        ProjectInstance pi = pump.mineData(updateManager);
        pump.close();

        return pi;
    }

    /**
     * uploads the ProjectInstance data to the database
     * @param pi instance of the project containing all the data
     */
    private void loadProjectInstance(ProjectInstance pi) {
        Loader loader = new Loader(updateManager);
        loader.loadProjectInstance(pi);
    }

    /**
     * prints the output files from the data in ProjectInstance
     * @param pi mined data
     */
    private void printProjectInstance(ProjectInstance pi) {
        TimelineFilePrinter timelinePrinter = new TimelineFilePrinter();
        CocaexFilePrinter cocaexPrinter = new CocaexFilePrinter();
        try {
            timelinePrinter.print(pi);
            cocaexPrinter.print(pi);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        CodefacePrinter codefacePrinter = new CodefacePrinter();
        codefacePrinter.print(pi);
    }

    /**
     * gets all the ProjectInstance URLs from the database
     * @return list of URLs
     */
    public List<String> getProjects() {
        ProjectInstanceDAO dao = new ProjectInstanceDAO_JPA(updateManager);

        return dao.selectAllUrls();
    }

    /**
     * mines a project already in the database (deletes previous instance and mines a current one)
     * @param url URL of the project instance
     * @param loginResults a map containing username, password and private kez for accessing the project
     * @return current ProjectInstance data
     */
    private ProjectInstance remineProjectInstance(String url, Map<String, String> loginResults) {
        ToolInstanceDAO dao = new ToolInstanceDAO_JPA(updateManager);
        Tool tool = dao.findToolByProjectInstanceUrl(url);
        return mineProjectInstance(url, loginResults, tool.name());
    }

    /**
     * closes the application (the entity managers)
     */
    public void close() {
        updateManager.close();
        if (createManager != null && createManager.isOpen()) {
            createManager.close();
        }
    }

    /**
     * tries to guess the source tool of the project base on the URL
     * @param toolString URL of the project
     * @return the guessed tool name
     */
    public String guessTool(String toolString) {
        String toolName = "";

        if (toolString.startsWith(App.GITHUB_PREFIX)) {
            toolName = Tool.GITHUB.name();
        } else if (toolString.endsWith(App.GIT_SUFFIX)) {
            toolName = Tool.GIT.name();
        } else if (toolString.toUpperCase().contains("REDMINE")) {
            toolName = Tool.REDMINE.name();
        } else if (toolString.toUpperCase().contains("BUGZILLA")) {
            toolName = Tool.REDMINE.name();
        } else if (toolString.toUpperCase().contains("SVN")) {
            toolName = Tool.REDMINE.name();
        } else if (toolString.toUpperCase().contains("JIRA")) {
            toolName = Tool.REDMINE.name();
        } else if (toolString.toUpperCase().contains("RTC")) {
            toolName = Tool.REDMINE.name();
        } else if (toolString.toUpperCase().contains("ASSEMBLA")) {
            toolName = Tool.REDMINE.name();
        }
        return toolName;
    }

    /**
     * gets the names of selected enumeration values (e.g. priorities) used in the project from database
     * @param entity an EntityStrings instance for the particular enumeration (e.g. priority)
     * @param url project's URL (if null gets for all the projects)
     * @return collection of enumeration values
     */
    public Collection<String> getEnumsByPrjUrl(EnumStrings entity, String url) {
        ProjectInstanceDAO dao = new ProjectInstanceDAO_JPA(updateManager);

        if (url == null) return dao.selectEnums(entity);
        else return dao.selectEnumsByPrjUrl(entity, url);
    }

    /**
     * gets a cout of WorkUnits in a project with null value of a given enumeration (e.g. priority) from database
     * @param entity an EntityStrings instance for the particular enumeration (e.g. priority)
     * @param url project's URL (if null gets for all the projects)
     * @return number of WorkUnits with enumeration value null
     */
    public int getUnitCountWithNullEnum(EnumStrings entity, String url) {
        WorkUnitDAO dao = new WorkUnitDAO_JPA(updateManager);

        if (url == null) return dao.getUnitCountWithNullEnum(entity);
        else return dao.getUnitCountWithNullEnum(entity, url);
    }

    /**
     * gets number of WorkUnits in a project with a specific name value (e.g. normal) of a given enumeration (e.g. priority)
     * from database
     * @param entity an EntityStrings instance for the particular enumeration (e.g. priority)
     * @param url project's URL (if null gets for all the projects)
     * @param name searched enumeration name value
     * @return number of WorkUnits fitting the criteria
     */
    public int getUnitCountByEnumName(EnumStrings entity, String url, String name) {
        WorkUnitDAO dao = new WorkUnitDAO_JPA(updateManager);

        if (url == null) return dao.getUnitCountByEnumName(entity, name);
        else return dao.getUnitCountByEnumName(entity, url, name);
    }

    /**
     * get a count of WorkUnits in a project with a specific value (e.g. NORMAL) of a specific enumeration field
     * (class or superclass) of priority from the database
     * @param field class or superclass
     * @param name field value (e.g. NORMAL)
     * @param url project's URL (if null gets for all the projects)
     * @return number of WorkUnits fitting the criteria
     */
    public int getUnitCountByPriority(String field, String name, String url) {
        WorkUnitDAO dao = new WorkUnitDAO_JPA(updateManager);

        int result = 0;
        if (field.equals("class")) {
            PriorityClass priorityClass = PriorityClass.valueOf(name);

            if (url == null) result = dao.getUnitCountByPriority(priorityClass);
            else result = dao.getUnitCountByPriority(priorityClass, url);

        } else if (field.equals("superclass")) {
            PrioritySuperClass prioritySuperClass = PrioritySuperClass.valueOf(name);

            if (url == null) result = dao.getUnitCountByPriority(prioritySuperClass);
            else result = dao.getUnitCountByPriority(prioritySuperClass, url);

        }
        return result;
    }

    /**
     * get a count of WorkUnits in a project with a specific value (e.g. OPEN) of a specific enumeration field
     * (class or superclass) of status from the database
     * @param field class or superclass
     * @param name field value (e.g. OPEN)
     * @param url project's URL (if null gets for all the projects)
     * @return number of WorkUnits fitting the criteria
     */
    public int getUnitCountByStatus(String field, String name, String url) {

        WorkUnitDAO dao = new WorkUnitDAO_JPA(updateManager);
        int result = 0;
        if (field.equals("class")) {
            StatusClass statusClass = StatusClass.valueOf(name);

            if (url == null) result = dao.getUnitCountByStatus(statusClass);
            else result = dao.getUnitCountByStatus(statusClass, url);

        } else if (field.equals("superclass")) {
            StatusSuperClass statusSuperClass = StatusSuperClass.valueOf(name);

            if (url == null) result = dao.getUnitCountByStatus(statusSuperClass);
            else result = dao.getUnitCountByStatus(statusSuperClass, url);
        }
        return result;
    }

    /**
     * get a count of WorkUnits in a project with a specific value (e.g. INVALID) of a specific enumeration field
     * (class or superclass) of resolution from the database
     * @param field class or superclass
     * @param name field value (e.g. INVALID)
     * @param url project's URL (if null gets for all the projects)
     * @return number of WorkUnits fitting the criteria
     */
    public int getUnitCountByResolution(String field, String name, String url) {

        WorkUnitDAO dao = new WorkUnitDAO_JPA(updateManager);
        int result = 0;
        if (field.equals("class")) {
            ResolutionClass resolutionClass = ResolutionClass.valueOf(name);

            if (url == null) result = dao.getUnitCountByResolution(resolutionClass);
            else result = dao.getUnitCountByResolution(resolutionClass, url);

        } else if (field.equals("superclass")) {
            ResolutionSuperClass resolutionSuperClass = ResolutionSuperClass.valueOf(name);

            if (url == null) result = dao.getUnitCountByResolution(resolutionSuperClass);
            else result = dao.getUnitCountByResolution(resolutionSuperClass, url);

        }
        return result;
    }

    /**
     * get a count of WorkUnits in a project with a specific value (e.g. NORMAL) of a specific enumeration field
     * (class or superclass) of severity from the database
     * @param field class or superclass
     * @param name field value (e.g. NORMAL)
     * @param url project's URL (if null gets for all the projects)
     * @return number of WorkUnits fitting the criteria
     */
    public int getUnitCountBySeverity(String field, String name, String url) {

        WorkUnitDAO dao = new WorkUnitDAO_JPA(updateManager);
        int result = 0;
        if (field.equals("class")) {
            SeverityClass severityClass = SeverityClass.valueOf(name);

            if (url == null) result = dao.getUnitCountBySeverity(severityClass);
            else result = dao.getUnitCountBySeverity(severityClass, url);


        } else if (field.equals("superclass")) {
            SeveritySuperClass severitySuperClass = SeveritySuperClass.valueOf(name);

            if (url == null) result = dao.getUnitCountBySeverity(severitySuperClass);
            else result = dao.getUnitCountBySeverity(severitySuperClass, url);

        }
        return result;
    }

    /**
     * get a count of WorkUnits in a project with a specific value (e.g. BUGFIX) of a specific enumeration field
     * (class or superclass) of type from the database
     * @param field class or superclass
     * @param name field value (e.g. BUGFIX)
     * @param url project's URL (if null gets for all the projects)
     * @return number of WorkUnits fitting the criteria
     */
    public int getUnitCountByType(String field, String name, String url) {

        WorkUnitDAO dao = new WorkUnitDAO_JPA(updateManager);
        int result = 0;
        if (field.equals("class")) {
            WorkUnitTypeClass typeClass = WorkUnitTypeClass.valueOf(name);

            if (url == null) result = dao.getUnitCountByType(typeClass);
            else result = dao.getUnitCountByType(typeClass, url);

        }
        return result;
    }

    /**
     * prints a message with a timestamp to the log output
     * @param message a message
     */
    public static void printLogMsg(String message) {
        log.println(getTimeStamp() + ": " + message);
    }

    /**
     * gets a timestamp for a log message
     * @return timestamp
     */
    private static String getTimeStamp() {
        return TIMESTAMP.format(System.currentTimeMillis());
    }

    /**
     * mines multiple projects one by one from an input file named after a tool;
     * first 3 lines should specify username, password and private key for accessing the projects (in that order)
     * @param tool tool name (all caps, e.g. GITHUB)
     */
    public void mineFromFile(String tool) {
        List<String> lines = readFile("input\\" + tool + ".txt");

        Map<String, String> loginResults = new HashMap<>();
        loginResults.put("username", lines.get(0));
        loginResults.put("password", lines.get(1));
        if (lines.get(2).isEmpty()) loginResults.put("privateKey", null);
        else loginResults.put("privateKey", lines.get(2));

        for (int i = 3; i < lines.size(); i ++) {
            this.processProjectInstance(lines.get(i), loginResults, tool);
        }
    }

    /**
     * mines multiple projects one by one from an input file named after a tool
     * @param tool tool name (all caps, e.g. GITHUB)
     * @param loginResults a map containing username, password and private key for accessing the projects
     */
    void mineFromFile(String tool, Map<String, String> loginResults) {
        List<String> lines = readFile("input\\" + tool + ".txt");
        boolean[] successes = new boolean[lines.size()];
        for (int i = 0; i < successes.length; i++) {
            successes[i] = false;
        }

        int i = 0;
        Scanner s = new Scanner(System.in, "UTF-8");
        while (true) {
            boolean overallSuccess = true;

            for (boolean success : successes) {
                if (!success){
                    overallSuccess = false;
                    break;
                }
            }
            if (overallSuccess) break;

            if (successes[i]) continue;

            log.println("======================================");
            log.println("\t\tproject: " + (i+1) + "/" + lines.size());
            log.println("======================================");
            for (int attempt = 0; attempt < 3; attempt++) {
                try {
                    this.processProjectInstance(lines.get(i), loginResults, tool);
                    successes[i] = true;
                    printLogMsg(lines.get(i) + " - mining successful");
                    break;
                } catch (Exception e) {
                    e.printStackTrace();
                    successes[i] = false;
                    s.nextLine();
                }
            }
            i = (i + 1) % lines.size();
        }
        s.close();
    }

    /**
     * reads an input file untill the line starting with "\\"
     * @param file input file name
     * @return list of lines read
     */
    private List<String> readFile(String file) {
        List<String> lines = new ArrayList<>();
        BufferedReader reader;

        try {
            reader = new BufferedReader(
                        new InputStreamReader(
                            new FileInputStream(file), "UTF-8"));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("//")) break;
                lines.add(line.trim());
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }
}
