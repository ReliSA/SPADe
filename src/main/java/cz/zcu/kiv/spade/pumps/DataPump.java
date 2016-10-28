package cz.zcu.kiv.spade.pumps;

import java.io.File;
import java.util.Date;

public abstract class DataPump {

    protected final String ROOT_TEMP_DIR = "D:/repos/";

    protected String projectHandle;
    protected String projectName;

    public DataPump(String projectHandle) {
        this.projectHandle = projectHandle;
        this.projectName = projectHandle.substring(projectHandle.lastIndexOf("/") + 1);
        this.getRootObject();
    }

    protected static void deleteTempDir(File file) {
        if (file.isDirectory()) {
            File[] fileList = file.listFiles();
            for (int i = 0; i < fileList.length; i++) {
                deleteTempDir(fileList[i]);
            }
        }
        file.delete();
    }

    public abstract void mineData();

    protected abstract void getRootObject();

    protected abstract Date convertDate(Object date);
}
