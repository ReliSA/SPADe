package cz.zcu.kiv.spade.gui.tabs;

import cz.zcu.kiv.spade.gui.SPADeGUI;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public abstract class BrowserTab extends SPADeTab {

    protected String url;
    String folder;
    protected String file;

    ComboBox<String> prjSelect;
    WebEngine webEngine;

    BrowserTab(String name, SPADeGUI gui) {
        super(name, gui);

        prjSelect = new ComboBox<>();
        WebView browser = new WebView();
        webEngine = browser.getEngine();
        HBox prjBox = new HBox(10);
        prjBox.setAlignment(Pos.CENTER_LEFT);
        prjBox.setPadding(new Insets(0, 0, 0, 5));

        grid.setPadding(new Insets(5, 0, 0, 0));
        grid.setVgap(5);

        prjBox.getChildren().addAll(new Label("Project: "), prjSelect);
        grid.add(prjBox, 0, 0);
        grid.add(browser, 0, 1);

        setColumnWidths(1000);

        browser.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.F5) webEngine.reload();
        });

        prjSelect.setOnAction(event -> selectProject());
    }

    public List<String> getDataFiles() {
        File folder = new File(this.folder);
        File[] listOfFiles = folder.listFiles();

        List<String> list = new ArrayList<>();

        if (listOfFiles == null || listOfFiles.length == 0) return list;

        for (File listOfFile : listOfFiles) {
            if (listOfFile.isFile() && !listOfFile.getName().equals(file)) {
                list.add(listOfFile.getName());
            }
        }
        return list;
    }

    @Override
    public abstract void refreshProjects(List<String> projects);

    protected abstract void selectProject();
}
