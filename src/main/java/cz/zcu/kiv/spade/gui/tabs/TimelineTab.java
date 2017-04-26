package cz.zcu.kiv.spade.gui.tabs;

import cz.zcu.kiv.spade.gui.SPADeGUI;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import java.util.List;

public class TimelineTab extends SPADeTab {

    private ComboBox<String> prjSelect;
    private WebEngine webEngine;

    public TimelineTab(SPADeGUI gui) {
        super("Timeline", gui);

        prjSelect = new ComboBox<>();
        WebView browser = new WebView();
        webEngine = browser.getEngine();

        grid.add(new Label("Project: "), 0, 0);
        grid.add(prjSelect, 1, 0);
        grid.add(browser, 0, 1, 2, 1);

        webEngine.load("file:///C:/Users/oem/Downloads/pichy/_škola/_výzkum/_workspace/SPADe/Timeline/software/index.html");

        browser.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.F5) refreshProjects(gui.getApp().getProjects());
        });
        prjSelect.setOnAction(event -> refreshProjects(gui.getApp().getProjects()));

    }

    @Override
    public void refreshProjects(List<String> projects) {
        if (projects.isEmpty()) return;
        prjSelect.getItems().clear();
        prjSelect.getItems().addAll(projects);
        prjSelect.getSelectionModel().selectFirst();
        webEngine.reload();
    }
}
