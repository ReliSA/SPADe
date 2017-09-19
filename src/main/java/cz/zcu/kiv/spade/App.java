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

public class App {

    /** a format for timestamps */
    public static final SimpleDateFormat TIMESTAMP = new SimpleDateFormat("HH:mm:ss.SSS");
    public static final String GIT_SUFFIX = ".git";
    /** regular expression for Git commit hash */
    public static final String GIT_COMMIT_REGEX = "(?<=^r|\\Wr|_r|$r)\\[a-f0-9]{7}(?=\\W|_|^|$)";
    /** temporary directory to transfer necessary data into */
    public static final String ROOT_TEMP_DIR = "repos\\";
    /** regular expression for SVN revision marker */
    public static final String SVN_REVISION_REGEX = "(?<=^r|\\Wr|_r|$r)\\d{1,4}(?=\\W|_|^|$)";

    private static final String PERSISTENCE_UNIT_CREATE = "create";
    private static final String PERSISTENCE_UNIT_UPDATE = "update";
    private static final String GITHUB_PREFIX = "https://github.com/";

    public static PrintStream log;
    private EntityManager updateManager;
    private EntityManager createManager;

    public App() {
        EntityManagerFactory factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_UPDATE);
        this.updateManager = factory.createEntityManager();
        log = System.out;
    }

    public App(PrintStream logOutput) {
        EntityManagerFactory factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_UPDATE);
        this.updateManager = factory.createEntityManager();
        log = logOutput;
    }

    public void createBlankDB() {
        printLogMsg("Initializing DB ...");

        if (createManager == null || !createManager.isOpen()) {
            EntityManagerFactory factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_CREATE);
            createManager = factory.createEntityManager();
        }

        new DBInitializer(createManager).initializeDatabase();

        printLogMsg("DB initialized");
    }

    public void processProjectInstance(String url, Map<String, String> loginResults, String toolName) {
        printLogMsg("mining of " + url + " started...");

        ProjectInstance pi;
        if (toolName == null) pi = this.remineProjectInstance(url, loginResults);
        else pi = this.mineProjectInstance(url, loginResults, toolName);
        printLogMsg("project instance " + pi.getUrl() + " mined");

        this.printProjectInstance(pi);
        printLogMsg("project instance " + pi.getUrl() + " printed");

        this.loadProjectInstance(pi);
        printLogMsg("project instance " + pi.getUrl() + " loaded");
    }

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

        ProjectInstance pi = null;
        try {
            if (pump != null) {
                pi = pump.mineData(updateManager);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (pump != null) {
                pump.close();
            }
        }

        return pi;
    }

    private void loadProjectInstance(ProjectInstance pi) {
        Loader loader = new Loader(updateManager);
        loader.loadProjectInstance(pi);
    }

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

    public List<String> getProjects() {
        ProjectInstanceDAO dao = new ProjectInstanceDAO_JPA(updateManager);

        return dao.selectAllUrls();
    }

    private ProjectInstance remineProjectInstance(String url, Map<String, String> loginResults) {
        ToolInstanceDAO dao = new ToolInstanceDAO_JPA(updateManager);
        Tool tool = dao.findToolByProjectInstanceUrl(url);
        return mineProjectInstance(url, loginResults, tool.name());
    }

    public void close() {
        updateManager.close();
        if (createManager != null && createManager.isOpen()) {
            createManager.close();
        }
    }

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

    public Collection<String> getEnumsByPrjUrl(EnumStrings entity, String url) {
        ProjectInstanceDAO dao = new ProjectInstanceDAO_JPA(updateManager);

        if (url == null) return dao.selectEnums(entity);
        else return dao.selectEnumsByPrjUrl(entity, url);
    }

    public int getUnitCountWithNullEnum(EnumStrings entity, String url) {
        WorkUnitDAO dao = new WorkUnitDAO_JPA(updateManager);

        if (url == null) return dao.getUnitCountWithNullEnum(entity);
        else return dao.getUnitCountWithNullEnum(entity, url);
    }

    public int getUnitCountByEnumName(EnumStrings entity, String url, String name) {
        WorkUnitDAO dao = new WorkUnitDAO_JPA(updateManager);

        if (url == null) return dao.getUnitCountByEnumName(entity, name);
        else return dao.getUnitCountByEnumName(entity, url, name);
    }

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

    public static void printLogMsg(String message) {
        log.println(getTimeStamp() + ": " + message);
    }

    private static String getTimeStamp() {
        return TIMESTAMP.format(System.currentTimeMillis());
    }

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

    void mineFromFile(String tool, Map<String, String> loginResults) {
        List<String> lines = readFile("input\\" + tool + ".txt");
        for (int i = 0; i < lines.size(); i++) {
            printLogMsg("======================================");
            printLogMsg("project: " + (i+1) + "/" + lines.size());
            printLogMsg("======================================");
            this.processProjectInstance(lines.get(i), loginResults, tool);
        }
    }

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
