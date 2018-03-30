package cz.zcu.kiv.spade;

import java.io.*;
import java.util.*;

/**
 * Main class of the SPADe application
 *
 * @author Petr Pícha
 */
public class Main {

    private static String[] getProjectOptions(File[] files) {
        String[] options = new String[files.length + 1];
        for (int i = 0; i < files.length; i++) {
            if (files[i].isDirectory()) {
                int childrenCount = 0;
                File[] children = files[i].listFiles();
                if (children != null) {
                    childrenCount = children.length;
                }
                if (childrenCount > 0) {
                    options[i] = files[i].getName() + " (" + childrenCount + " projects)";
                }
            } else {
                options[i] = files[i].getName().substring(0, files[i].getName().indexOf("."));
            }
        }
        options[files.length] = "to previous menu";
        return options;
    }

    /**
     * runs the whole application, handles command line parameters
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        App app = new App();

        boolean cycle = true;
        while (cycle) {

            String[] choices = {"create blank database",
                                "mine project(s)",
                                "show GUI",
                                "exit"};
            int action = App.promptUserSelection(choices, "action");

            switch (action) {
                case 0:
                    app.createBlankDB();
                    cycle = false;
                    break;
                case 1:
                    File settings = new File("settings");
                    File[] files = settings.listFiles();
                    if (files == null || files.length == 0) {
                        App.log.println("No properties files in settings directory!");
                        cycle = false;
                        break;
                    }

                    int project = App.promptUserSelection(getProjectOptions(files), "project(s)");
                    if (project == files.length) continue;

                    List<App.Flag> flags = new ArrayList<>();
                    for (App.Flag flag : App.Flag.values()) {
                        if (App.promptUserSelection(App.NO_YES, flag.name()) == 1) {
                            flags.add(flag);
                        }
                    }

                    if (files[project].isFile()) {
                        mineSingleProject(app, files[project], flags);
                    } else if (files[project].isDirectory()) {
                        File[] children = files[project].listFiles();
                        if (children != null && children.length > 0) {
                            int i = 1;
                            App.log.println("============================================================================");
                            for (File file : children) {
                                App.log.println("\t\t\t\t\tPROJECT " + i + "/" + children.length);
                                mineSingleProject(app, file, flags);
                                i++;
                            }
                        }
                    }
                    cycle = false;
                    break;
                case 2:
                    //launch(args);
                    //break;
                case 3:
                default:
                    break;
            }
        }
        app.close();
        scanner.close();
        System.exit(0);
    }

    private static void mineSingleProject(App app, File file, List<App.Flag> flags) {
        try {
            Properties props = new Properties();
            props.load(new InputStreamReader(new FileInputStream(file), "UTF8"));
            Map<String, String> loginResults = new TreeMap<>();
            for (String key : props.stringPropertyNames()) {
                loginResults.put(key, props.getProperty(key));
                if (props.getProperty(key).isEmpty()) loginResults.put(key, null);
            }
            app.processProjectInstance(loginResults.get("url"), loginResults, loginResults.get("tool"), flags);
        } catch (IOException e) {
            App.log.println("Cannot read file " + file.getName() + "!");
        }
    }

    /*@Override
    public void start(Stage primaryStage) {
        new SPADeGUI().showMainWindow(primaryStage);
    }*/
}
