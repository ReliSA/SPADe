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

/**
 * Main class of the SPADe application
 *
 * @author Petr Pícha
 */
public class Main extends Application {

    /**
     * runs the whole application, handles command line parameters
     */
    public static void main(String[] args) {

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

        if (args.length > 0) {
            switch (args[0]) {
                case "giu":
                    launch(args);
                    break;
                case "clean":
                    app.createBlankDB();
                    break;
                case "one":
                    app.processProjectInstance(loginResults.get("url"), loginResults, loginResults.get("tool"));
                    break;
            }
        } else {
            app.mineFromFile(loginResults.get("tool"), loginResults);
        }
        app.close();
        System.exit(0);
    }

    @Override
    public void start(Stage primaryStage) {
        new SPADeGUI().showMainWindow(primaryStage);
    }
}
