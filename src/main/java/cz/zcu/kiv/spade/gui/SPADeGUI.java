package cz.zcu.kiv.spade.gui;

import cz.zcu.kiv.spade.App;
import cz.zcu.kiv.spade.gui.tabs.*;
import javafx.scene.Scene;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class SPADeGUI {

    private App app;
    private SPADeTab mineTab, chartTab;
    private TimelineTab timelineTab;
    //private CocaexTab cocaexTab;

    public void showMainWindow(Stage stage) {
        stage.setTitle("SPADe - Software Process Anti-patterns Detector");
        stage.getIcons().add(new Image("file:SPADe.png"));

        app = new App();

        mineTab = new MiningTab(this);
        chartTab = new ChartTab(this);
        timelineTab = new TimelineTab(this);
        //cocaexTab = new CocaexTab(this);

        mineTab.setClosable(false);
        TabPane tabPane = new TabPane(mineTab, chartTab, timelineTab/*, cocaexTab*/);

        refreshProjects();

        stage.setScene(new Scene(tabPane));

        stage.setOnCloseRequest(event -> app.close());

        stage.show();
    }

    public void refreshProjects() {
        mineTab.refreshProjects(app.getProjects());
        chartTab.refreshProjects(app.getProjects());
        timelineTab.refreshProjects(timelineTab.getDataFiles());
        //cocaexTab.refreshProjects(cocaexTab.getDataFiles());
    }

    public App getApp() {
        return app;
    }
}
