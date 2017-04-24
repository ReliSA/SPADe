package cz.zcu.kiv.spade.gui;

import cz.zcu.kiv.spade.App;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class SPADeGUI {

    App app;
    MiningTab mineTab;
    ChartTab chartTab;

    public void showMainWindow(Stage stage) {
        stage.setTitle("SPADe - Software Process Anti-patterns Detector");
        stage.getIcons().add(new Image("file:SPADe.png"));

        app = new App();

        mineTab = new MiningTab(app);
        chartTab = new ChartTab(app);

        mineTab.setClosable(false);
        TabPane tabPane = new TabPane(mineTab, chartTab, new TimelineTab());

        Scene scene = new Scene(tabPane, 800, 600);
        stage.setScene(scene);

        stage.setOnCloseRequest(event -> app.close());

        stage.show();
    }

    void refreshProjects() {
        mineTab.refreshProjects(app.getProjects());
        chartTab.refreshProjects(app.getProjects());
    }
}
