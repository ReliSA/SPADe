package cz.zcu.kiv.spade;

import cz.zcu.kiv.spade.gui.SPADeGUI;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage primaryStage) {
        new SPADeGUI().showMainWindow(primaryStage);
    }
}
