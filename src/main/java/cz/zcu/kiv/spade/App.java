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
import cz.zcu.kiv.spade.pumps.issuetracking.bugzilla.BugzillaPump;
import cz.zcu.kiv.spade.pumps.issuetracking.github.GitHubPump;
import cz.zcu.kiv.spade.pumps.vcs.git.GitPump;
import cz.zcu.kiv.spade.pumps.issuetracking.jira.JiraPump;
import cz.zcu.kiv.spade.pumps.issuetracking.redmine.RedminePump;
import org.json.JSONException;

import javax.persistence.EntityManager;
import java.io.*;
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
    /** the extension of git repository URLs */
    public static final String GIT_SUFFIX = ".git";
    /** temporary directory to transfer necessary data into */
    public static final String ROOT_TEMP_DIR = "repos/";
    /** a prefix of a GitHub repository URL */
    private static final String GITHUB_PREFIX = "https://github.com/";
    private static final String MESSAGE_FORMAT = "%s: %s: %s";


    private static final String UNKNOWN_ENTITY_PROMPT_FORMAT = "Unknown %s \"%s\", select category to map it onto:";
    private static final String CHOOSE_ENTITY_PROMPT_FORMAT = "Choose %s:";
    private static final String PROMPT_DEFAULT_OPT_FORMAT = "\t%s - %s (default)";
    private static final String PROMPT_OPT_FORMAT = "\t%s - %s";
    private static final String YOUR_CHOICE = "Your choice (leave empty for default): ";

    static final String[] NO_YES = {"No", "Yes"};
    private static final SimpleDateFormat UNIQUE_DATE_FORMAT = new SimpleDateFormat(" (yyyy-MM-dd kk-mm)");

    public enum Flag {
        PRINT_TIMELINE,
        PRINT_COCAEX,
        PRINT_CODEFACE,
        LOAD,
        PRINT_STATS
    }

    // TODO temporary fix
    private EntityManager updateManager = null;

    /** a stream to print log messages into */
    public static PrintStream log;

    /**
     * default constructor, sets default update entity manager and log print stream
     */
    public App() {
        log = System.out;
        try {
            //log = new PrintStream(new FileOutputStream(new File("out.txt")));
            System.setOut(new PrintStream(new FileOutputStream(new File("out.txt"))));
            //System.setErr(new PrintStream(new FileOutputStream(new File("err.txt"))));
        } catch (FileNotFoundException e) {
            System.out.println("File not found!");
        }
    }

    /**
     * a constructor, sets default update entity manager and a given log print stream
     * @param logOutput stream for printing log messages
     */
    public App(PrintStream logOutput) {
        log = logOutput;
    }

    /**
     * creates a blank SPADe database
     */
    public void createBlankDB() {
        printLogMsg(this,"Initializing DB ...");

        new DBInitializer().initializeDatabase();

        printLogMsg(this, "DB initialized");
    }

    /**
     * handles a lifecycle of mining a project instance (mining, printing output files, uploading to database)
     * @param url project's URL to mine
     * @param loginResults a map containing username, password and private kez for accessing the project
     * @param toolName name of the tool storing the project data (all caps, e.g. GITHUB)
     */
    public void processProjectInstance(String url, Map<String, String> loginResults, String toolName, List<Flag> flags) {
        long startTime = System.currentTimeMillis();
        log.println("============================================================================");
        App.printLogMsg(this, "mining of " + loginResults.get("url") + " started...");

        ProjectInstance pi;
        if (toolName == null) pi = reMineProjectInstance(url, loginResults);
        else pi = this.mineProjectInstance(url, loginResults, toolName);
        if (pi == null) return;
        pi.getStats().setStart(startTime);

        long miningTime = System.currentTimeMillis();
        printLogMsg(this, "project instance " + pi.getUrl() + " mined");
        pi.getStats().setMining(miningTime);

        if (flags.contains(Flag.PRINT_TIMELINE) || flags.contains(Flag.PRINT_COCAEX) || flags.contains(Flag.PRINT_CODEFACE)) {
            this.printProjectInstance(pi, flags);

            long printingTime = System.currentTimeMillis();
            pi.getStats().setPrinting(printingTime);
            printLogMsg(this,"project instance " + pi.getUrl() + " printed");
        }

        if (flags.contains(Flag.LOAD)) {
            pi.setName(pi.getName() + UNIQUE_DATE_FORMAT.format(new Date(System.currentTimeMillis())));
            this.loadProjectInstance(pi);

            long loadingTime = System.currentTimeMillis();
            printLogMsg(this, "project instance " + pi.getUrl() + " loaded");
            pi.getStats().setLoading(loadingTime);
        }

        if (flags.contains(Flag.PRINT_STATS)) {
            new StatsPrinter(pi).print();
        }

        log.println("============================================================================");
    }

    /**
     * mines the project data
     * @param url project's URL to mine
     * @param loginResults a map containing username, password and private kez for accessing the project
     * @param toolName name of the tool storing the project data (all caps, e.g. GITHUB)
     * @return a ProjectInstance instance with all the data mined
     */
    private ProjectInstance mineProjectInstance(String url, Map<String, String> loginResults, String toolName) {

        DataPump pump;
        switch (Tool.valueOf(toolName)) {
            case GIT:
                pump = new GitPump(url, loginResults.get("privateKey"), loginResults.get("username"), loginResults.get("password"));
                break;
            case GITHUB:
                pump = new GitHubPump(url, loginResults.get("privateKey"), loginResults.get("username"), loginResults.get("password"));
                break;
            case REDMINE:
                pump = new RedminePump(url, loginResults.get("privateKey"), loginResults.get("username"), loginResults.get("password"));
                pump.setRepo(loginResults.get("repo"));
                break;
            case JIRA:
                pump = new JiraPump(url, loginResults.get("privateKey"), loginResults.get("username"), loginResults.get("password"));
                pump.setRepo(loginResults.get("repo"));
                break;
            case BUGZILLA:
                pump = new BugzillaPump(url, loginResults.get("privateKey"), loginResults.get("username"), loginResults.get("password"));
                pump.setRepo(loginResults.get("repo"));
                break;
            case ASSEMBLA:
                //pump = new AssemblaPump(url, loginResults.get("privateKey"), loginResults.get("username"), loginResults.get("password"));
                //break;
            case SVN:
                //pump = new SvnPump(url, loginResults.get("privateKey"), loginResults.get("username"), loginResults.get("password"));
                //break;
            case RTC:
            default:
                return null;
        }

        ProjectInstance pi = pump.mineData();
        pump.close();

        return pi;
    }

    public void close() {
        if (updateManager != null) updateManager.close();
    }

    /**
     * uploads the ProjectInstance data to the database
     * @param pi instance of the project containing all the data
     */
    private void loadProjectInstance(ProjectInstance pi) {
        Loader loader = new Loader();
        loader.loadProjectInstance(pi);
    }

    /**
     * prints the output files from the data in ProjectInstance
     * @param pi mined data
     */
    private void printProjectInstance(ProjectInstance pi, List<Flag> flags) {
        if (flags.contains(Flag.PRINT_TIMELINE)) {
            TimelineFilePrinter timelinePrinter = new TimelineFilePrinter();
            try {
                timelinePrinter.print(pi);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (flags.contains(Flag.PRINT_COCAEX)) {
            CocaexFilePrinter cocaexPrinter = new CocaexFilePrinter();
            try {
                cocaexPrinter.print(pi);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (flags.contains(Flag.PRINT_CODEFACE)) {
            CodefacePrinter codefacePrinter = new CodefacePrinter();
            codefacePrinter.print(pi);
        }
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
    private ProjectInstance reMineProjectInstance(String url, Map<String, String> loginResults) {
        ToolInstanceDAO dao = new ToolInstanceDAO_JPA(updateManager);
        Tool tool = dao.findToolByProjectInstanceUrl(url);
        return mineProjectInstance(url, loginResults, tool.name());
    }

    /**
     * tries to guess the source tool of the project base on the URL
     * @param toolString URL of the project
     * @return the guessed tool name
     */
    public String guessTool(String toolString) {
        String toolName = "";

        if (toolString.startsWith(GITHUB_PREFIX)) {
            toolName = Tool.GITHUB.name();
        } else if (toolString.endsWith(GIT_SUFFIX)) {
            toolName = Tool.GIT.name();
        } else if (toolString.contains(Tool.JIRA.name().toLowerCase())) {
            toolName = Tool.JIRA.name();
        } else if (toolString.contains(Tool.BUGZILLA.name().toLowerCase())) {
            toolName = Tool.BUGZILLA.name();
        } else if (toolString.contains(Tool.ASSEMBLA.name().toLowerCase())) {
            toolName = Tool.ASSEMBLA.name();
        } else if (toolString.startsWith(Tool.SVN.name().toLowerCase())) {
            toolName = Tool.REDMINE.name();
        } else if (toolString.contains(Tool.REDMINE.name().toLowerCase())) {
            toolName = Tool.REDMINE.name();
        } else if (toolString.contains(Tool.RTC.name().toLowerCase())) {
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
     * gets a count of WorkUnits in a project with null value of a given enumeration (e.g. priority) from database
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
     * @param source class that called to print the message
     * @param message a message
     */
    public static void printLogMsg(Object source, String message) {
        log.println(String.format(MESSAGE_FORMAT, TIMESTAMP.format(System.currentTimeMillis()), source.getClass().getSimpleName(), message));
    }

    /**
     * mines multiple projects one by one from an input file named after a tool;
     * first 3 lines should specify username, password and private key for accessing the projects (in that order)
     * @param tool tool name (all caps, e.g. GITHUB)
     */
    public void mineFromFile(String tool) {
        List<String> lines = readFile("input\\" + tool + ".txt");
        List<App.Flag> flags = new ArrayList<>();

        Map<String, String> loginResults = new HashMap<>();
        loginResults.put("username", lines.get(0));
        loginResults.put("password", lines.get(1));
        if (lines.get(2).isEmpty()) loginResults.put("privateKey", null);
        else loginResults.put("privateKey", lines.get(2));

        for (int i = 3; i < lines.size(); i ++) {
            this.processProjectInstance(lines.get(i), loginResults, tool, flags);
        }
    }

    /**
     * reads an input file until the line starting with "\\"
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

    static int promptUserSelection(String[] options, String entityType) {
        return promptUserSelection(options, entityType, "");
    }

    public static int promptUserSelection(String[] options, String entityType, String entityName) {
        while (true) {
            log.println();
            if (!entityName.isEmpty()) {
                log.println(String.format(UNKNOWN_ENTITY_PROMPT_FORMAT, entityType, entityName));
            } else {
                log.println(String.format(CHOOSE_ENTITY_PROMPT_FORMAT, entityType));
            }
            for (int i = 0; i < options.length; i++) {
                if (i == 0) {
                    log.println(String.format(PROMPT_DEFAULT_OPT_FORMAT, i, options[i]));
                } else {
                    log.println(String.format(PROMPT_OPT_FORMAT, i, options[i]));
                }
            }
            log.print(YOUR_CHOICE);

            Scanner scanner = new Scanner(System.in);
            try {
                String response = scanner.nextLine();
                if (response.isEmpty()) {
                    response = 0 + "";
                }
                int choice = Integer.parseInt(response);
                if (choice < 0 || choice > options.length - 1) {
                    log.println("Illegal selection!\n");
                    continue;
                }
                log.println();
                return choice;
            } catch (NumberFormatException e) {
                log.println("Illegal selection!\n");
            }
        }
    }
}
