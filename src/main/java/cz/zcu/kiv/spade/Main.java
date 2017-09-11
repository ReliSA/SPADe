package cz.zcu.kiv.spade;

import cz.zcu.kiv.spade.domain.ProjectInstance;
import cz.zcu.kiv.spade.gui.SPADeGUI;
import cz.zcu.kiv.spade.pumps.DataPump;
import javafx.application.Application;
import javafx.stage.Stage;

import java.util.Map;
import java.util.TreeMap;

public class Main extends Application {

    public static void main(String[] args) {
        if (args.length > 0) launch(args);
        else {

            Map<String, String> loginResults = new TreeMap<>();
            loginResults.put("privateKey", null);
            loginResults.put("username", "ppicha");
            loginResults.put("password", "RATMKoRn48");

            App app = new App();

            printLogMsg("mining started...");
            ProjectInstance pi = app.loadProjectInstance("https://github.com/BVLC/caffe.git", loginResults, "GITHUB");
            printLogMsg("project instance " + pi.getUrl() + " mined");
            app.printProjectInstance(pi);
            printLogMsg("project instance " + pi.getUrl() + " printed");
            app.loadProjectInstance(pi);
            printLogMsg("project instance " + pi.getUrl() + " loaded");
            app.close();
        }
    }

    private static void printLogMsg(String message) {
        String timeStamp = getTimeStamp();
        System.out.println(timeStamp + ": " + message);
    }

    private static String getTimeStamp() {
        return DataPump.TIME.format(System.currentTimeMillis());
    }

    public void start(Stage primaryStage) {
        new SPADeGUI().showMainWindow(primaryStage);
    }
}
