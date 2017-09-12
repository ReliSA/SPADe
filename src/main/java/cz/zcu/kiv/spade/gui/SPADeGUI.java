package cz.zcu.kiv.spade.gui;

import cz.zcu.kiv.spade.App;
import cz.zcu.kiv.spade.gui.tabs.*;
import javafx.scene.Scene;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

public class SPADeGUI {

    private App app;
    private MiningTab mineTab;
    private SPADeTab chartTab;
    private TimelineTab timelineTab;
    //private CocaexTab cocaexTab;

    public void showMainWindow(Stage stage) {
        stage.setTitle("SPADe - Software Process Anti-patterns Detector");
        stage.getIcons().add(new Image("file:SPADe.png"));

        mineTab = new MiningTab(this);
        chartTab = new ChartTab(this);
        timelineTab = new TimelineTab(this);
        //cocaexTab = new CocaexTab(this);

        app = new App(new PrintStream(new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                mineTab.getLogArea().setText(mineTab.getLogArea().getText() + b);
            }
        }));

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
