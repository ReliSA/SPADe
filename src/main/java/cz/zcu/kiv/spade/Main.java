package cz.zcu.kiv.spade;

import cz.zcu.kiv.spade.gui.SPADeGUI;
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
            app.procesProjectInstance("https://github.com/BVLC/caffe.git", loginResults, "GITHUB");
        }
    }

    public void start(Stage primaryStage) {
        new SPADeGUI().showMainWindow(primaryStage);
    }
}
