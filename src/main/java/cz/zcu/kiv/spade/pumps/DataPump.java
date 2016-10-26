package cz.zcu.kiv.spade.pumps;

import cz.zcu.kiv.spade.domain.enums.Tool;

import java.io.File;
import java.util.Date;

public abstract class DataPump {

    protected final String ROOT_TEMP_DIR = "D:/repos/";

    protected String projectHandle;
    protected String projectName;
    protected String projectDir;
    protected Tool tool;

    public DataPump(String projectHandle, Tool tool) {
        this.projectHandle = projectHandle;
        this.tool = tool;
    }

    public abstract void mineData();

    protected abstract Object getRootObject();

    protected abstract void trimProjectName();

    protected abstract Date convertDate(Object date);

    protected static void deleteTempDir(File file) {
        if (file.isDirectory()) {
            File[] fileList = file.listFiles();
            for (int i = 0; i < fileList.length; i++) {
                deleteTempDir(fileList[i]);
            }
        }
        file.delete();
    }
}
