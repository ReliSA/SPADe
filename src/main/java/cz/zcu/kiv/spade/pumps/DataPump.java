package cz.zcu.kiv.spade.pumps;

import cz.zcu.kiv.spade.App;
import cz.zcu.kiv.spade.domain.*;
import cz.zcu.kiv.spade.domain.enums.Tool;

import java.io.File;

/**
 * Generic data pump
 *
 * @author Petr Pícha
 */
public abstract class DataPump<RootObjectType, SecondaryObjectType> {

    private static final String DELETE_TMP_DIR_PROMPT = "Delete TMP DIR !!!";
    private static final String PROTOCOL_SEPARATOR = "://";
    public static final String COLON = ":";

    protected static final String HTTPS_PROTOCOL = "https" + PROTOCOL_SEPARATOR;

    public static final String SLASH = "/";
    public static final String AT = "@";
    public static final String DOT = ".";
    public static final String SPACE = " ";
    public static final String LINE_BREAK = "\n";

    /**
     * URL of the project
     */
    protected final String projectHandle;
    /**
     * private key location for authenticated login
     */
    protected final String privateKeyLoc;
    /**
     * root object of the project's representation in a tool (repository/project)
     */
    protected RootObjectType rootObject;
    protected SecondaryObjectType secondaryObject;
    /**
     * username for authenticated login
     */
    protected String username;
    /**
     * password for authenticated login
     */
    protected String password;
    /**
     * Project Instance to store all the mined data in
     */
    protected ProjectInstance pi;
    /**
     * ALM tool of mined Project Instance
     */
    protected Tool tool;
    protected PeopleMiner peopleMiner;
    /**
     * @param projectHandle URL of the project instance
     * @param privateKeyLoc private key location for authenticated login
     * @param username      username for authenticated login
     * @param password      password for authenticated login
     */
    protected DataPump(String projectHandle, String privateKeyLoc, String username, String password) {
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
            if (!file.delete()) {
                App.printLogMsg(DataPump.class.getSimpleName(), DELETE_TMP_DIR_PROMPT);
            }
        }
    }

    /**
     * loads root object of the project's representation in a tool (repository/project)
     */
    protected abstract RootObjectType init();

    /**
     * gathers all data needed from project instance
     *
     * @return ProjectInstance with all data
     */
    public ProjectInstance mineData() {
        pi = new ProjectInstance();
        pi.setUrl(projectHandle);
        pi.setName(getProjectName());
        return pi;
    }

    /**
     * assigns appropriate Tool Instance to the Project Instance;
     * checks whether the Tool Instance already exists in the database,
     * if not it creates it
     */
    protected void setToolInstance() {
        ToolInstance ti = new ToolInstance();
        ti.setExternalId(getServer());
        ti.setTool(tool);

        pi.setToolInstance(ti);
    }

    /**
     * gets project name
     *
     * @return project name
     */
    protected String getProjectName() {
        if (projectHandle.endsWith(DOT)) {
            return projectHandle.substring(projectHandle.lastIndexOf(SLASH) + 1, projectHandle.lastIndexOf(DOT));
        } else {
            return projectHandle.substring(projectHandle.lastIndexOf(SLASH) + 1);
        }
    }

    /**
     * returns a temporary directory location of this project instance data
     *
     * @return project's temporary directory
     */
    protected String getProjectDir() {
        String cut = cutProtocolAndUser();
        String withoutPort = getServer().split(COLON)[0] + cut.substring(cut.indexOf(SLASH));
        if (withoutPort.endsWith(App.GIT_SUFFIX)) {
            return withoutPort.substring(0, withoutPort.lastIndexOf(App.GIT_SUFFIX));
        }
        else return withoutPort;
    }

    /**
     * cuts protocol and username (e.g. "ppicha@...") from project handle
     *
     * @return project URL without protocol and username
     */
    private String cutProtocolAndUser() {
        String withoutProtocol = projectHandle;
        if (withoutProtocol.contains(PROTOCOL_SEPARATOR))
            withoutProtocol = withoutProtocol.split(PROTOCOL_SEPARATOR)[1];
        if (withoutProtocol.contains(AT)) {
            withoutProtocol = withoutProtocol.split(AT)[1];
        }
        return withoutProtocol;
    }

    /**
     * cuts a server name from project handle
     *
     * @return server the project is mined from
     */
    public String getServer() {
        String cut = cutProtocolAndUser();
        return cut.substring(0, cut.indexOf(SLASH));
    }

    /**
     * performs the steps necessary to successfully close the instance in a clean way
     */
    public void close() {
        deleteTempDir(new File(App.ROOT_TEMP_DIR));
    }

    protected abstract void mineContent();

    public RootObjectType getRootObject() {
        return rootObject;
    }

    public SecondaryObjectType getSecondaryObject() {
        return secondaryObject;
    }

    public ProjectInstance getPi() {
        return pi;
    }

    public PeopleMiner getPeopleMiner() {
        return peopleMiner;
    }

    public void setRepo(String repo){}

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}