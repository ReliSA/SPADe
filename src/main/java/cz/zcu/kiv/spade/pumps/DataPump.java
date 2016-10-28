package cz.zcu.kiv.spade.pumps;

import org.eclipse.jgit.revwalk.RevCommit;

import java.io.File;
import java.util.Date;

public abstract class DataPump {

    public static final String ROOT_TEMP_DIR = "D:/repos/";

    protected String projectHandle;
    protected String projectName;

    protected String username;
    protected String password;
    protected String privateKeyLoc;


    public DataPump(String projectHandle) {
        this.projectHandle = projectHandle;
        this.projectName = projectHandle.substring(projectHandle.lastIndexOf("/") + 1, projectHandle.lastIndexOf(".git"));
    }

    public DataPump(String projectHandle, String username, String password) {
        this.projectHandle = projectHandle;
        this.projectName = projectHandle.substring(projectHandle.lastIndexOf("/") + 1, projectHandle.lastIndexOf(".git"));
        this.username = username;
        this.password = password;
    }

    public DataPump(String projectHandle, String privateKeyLoc) {
        this.projectHandle = projectHandle;
        this.projectName = projectHandle.substring(projectHandle.lastIndexOf("/") + 1, projectHandle.lastIndexOf(".git"));
        this.privateKeyLoc = privateKeyLoc;
    }

    public DataPump(String projectHandle, String privateKeyLoc, String username, String password) {
        this.projectHandle = projectHandle;
        this.projectName = projectHandle.substring(projectHandle.lastIndexOf("/") + 1, projectHandle.lastIndexOf(".git"));
        this.privateKeyLoc = privateKeyLoc;
        this.username = username;
        this.password = password;
    }

    public static void deleteTempDir(File file) {
        if (file.isDirectory()) {
            File[] fileList = file.listFiles();
            for (int i = 0; i < fileList.length; i++) {
                deleteTempDir(fileList[i]);
            }
        }
        file.delete();
    }

    public abstract void mineData();

    protected abstract Date convertDate(Object date);

    protected abstract void loadRootObject();

    protected String stripFileName(String path) {
        if (path.contains("/")) return path.substring(path.lastIndexOf("/") + 1);
        else return path;
    }

    protected String getProjectDir() {
        String withoutProtocol = projectHandle;
        if (withoutProtocol.contains("://")) withoutProtocol = withoutProtocol.split("://")[1];
        if (withoutProtocol.contains("@")) withoutProtocol = withoutProtocol.split("@")[1];
        String server = withoutProtocol.substring(0, withoutProtocol.indexOf('/'));
        String pathOnServer = withoutProtocol.substring(withoutProtocol.indexOf('/'));
        String withoutPort = server.split(":")[0] + pathOnServer;
        return withoutPort.substring(0, withoutPort.lastIndexOf(".git"));
    }
}
