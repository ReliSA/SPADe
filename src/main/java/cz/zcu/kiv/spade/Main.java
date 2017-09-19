package cz.zcu.kiv.spade;

import cz.zcu.kiv.spade.gui.SPADeGUI;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

public class Main extends Application {

    public static void main(String[] args) {
        if (args.length > 0) launch(args);
        else {

            Properties props = new Properties();
            try {
                props.load(new InputStreamReader(new FileInputStream("login.properties"), "UTF8"));
            } catch (IOException e) {
                e.printStackTrace();
            }

            Map<String, String> loginResults = new TreeMap<>();
            for (String key : props.stringPropertyNames()) {
                loginResults.put(key, props.getProperty(key));
                if (props.getProperty(key).isEmpty()) loginResults.put(key, null);
            }

            App app = new App();
            //app.createBlankDB();
            //app.processProjectInstance(loginResults.get("url"), loginResults, loginResults.get("tool"));
            app.mineFromFile(loginResults.get("tool"), loginResults);

            app.close();
            System.exit(0);
        }
    }

    public void start(Stage primaryStage) {
        new SPADeGUI().showMainWindow(primaryStage);
    }
}
