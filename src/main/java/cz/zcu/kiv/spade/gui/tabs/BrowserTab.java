package cz.zcu.kiv.spade.gui.tabs;

import cz.zcu.kiv.spade.gui.SPADeGUI;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
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

        grid.add(new Label("Project: "), 0, 0);
        grid.add(prjSelect, 1, 0);
        grid.add(browser, 0, 1, 2, 1);

        setColumnWidths(200, 1000);

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
    public void refreshProjects(List<String> projects) {
        if (projects.isEmpty()) return;
        prjSelect.getItems().clear();
        prjSelect.getItems().addAll(projects);
        prjSelect.getSelectionModel().selectFirst();
    }

    protected abstract void selectProject();
}
