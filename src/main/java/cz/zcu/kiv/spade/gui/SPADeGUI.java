package cz.zcu.kiv.spade.gui;

import cz.zcu.kiv.spade.App;
import cz.zcu.kiv.spade.gui.tabs.MiningTab;
import cz.zcu.kiv.spade.gui.tabs.ChartTab;
import cz.zcu.kiv.spade.gui.tabs.SPADeTab;
import cz.zcu.kiv.spade.gui.tabs.TimelineTab;
import javafx.scene.Scene;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class SPADeGUI {

    private App app;
    private SPADeTab mineTab, chartTab, timelineTab;

    public void showMainWindow(Stage stage) {
        stage.setTitle("SPADe - Software Process Anti-patterns Detector");
        stage.getIcons().add(new Image("file:SPADe.png"));

        app = new App();

        mineTab = new MiningTab(this);
        chartTab = new ChartTab(this);
        timelineTab = new TimelineTab(this);

        mineTab.setClosable(false);
        TabPane tabPane = new TabPane(mineTab, chartTab, timelineTab);

        refreshProjects();

        Scene scene = new Scene(tabPane);

        stage.setScene(scene);

        stage.setOnCloseRequest(event -> app.close());

        stage.show();
    }

    public void refreshProjects() {
        mineTab.refreshProjects(app.getProjects());
        chartTab.refreshProjects(app.getProjects());
        timelineTab.refreshProjects(app.getProjects());
    }

    public App getApp() {
        return app;
    }
}
