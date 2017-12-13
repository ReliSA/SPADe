package cz.zcu.kiv.spade.gui.tabs;

import cz.zcu.kiv.spade.domain.enums.Tool;
import cz.zcu.kiv.spade.gui.SPADeGUI;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;

import java.io.*;
import java.util.*;

public class MiningTab extends SPADeTab {

    private final TextField newBox;
    private final ChoiceBox<String> toolBox;
    private final TextArea logArea;
    private final RadioButton reloadBtn;
    private final ListView<String> reloadBox;

    public MiningTab(SPADeGUI gui) {
        super("Mining", gui);

        // components
        RadioButton newBtn = new RadioButton("Load new project:");
        newBox = new TextField();
        toolBox = new ChoiceBox<>();
        reloadBtn = new RadioButton("Reload projects:");
        reloadBox = new ListView<>();
        logArea = new TextArea();
        Button confirmBtn = new Button("Mine projects");
        Button blankBtn = new Button("Create blank database");
        HBox btnHBox = new HBox(10);

        // layout
        grid.add(newBtn, 0, 0);
        grid.add(newBox, 1, 0);
        grid.add(new Label("Tool:"), 2, 0);
        grid.add(toolBox, 3, 0);
        grid.add(new Label("Log:"), 4, 0);
        grid.add(reloadBtn, 0, 1);
        grid.add(reloadBox, 1, 1, 3, 1);
        grid.add(logArea, 4, 1);
        btnHBox.getChildren().addAll(confirmBtn, blankBtn);
        grid.add(btnHBox, 0, 2, 5, 1);

        setColumnWidths(120, 200, 30, 100, 450);
        setColumnHAlignment(HPos.RIGHT, 3);

        // default settings
        ToggleGroup urlGroup = new ToggleGroup();
        newBtn.setToggleGroup(urlGroup);
        reloadBtn.setToggleGroup(urlGroup);
        newBtn.setSelected(true);
        reloadBox.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        logArea.setEditable(true);
        logArea.setFont(Font.font("Courier New"));
        btnHBox.setAlignment(Pos.CENTER);
        confirmBtn.requestFocus();

        // data
        newBox.setPromptText("Project URL");
        for (Tool tool : Tool.values()) {
            toolBox.getItems().add(tool.name());
            if (tool.equals(Tool.REDMINE)) toolBox.getSelectionModel().select(tool.name());
        }

        // behavior
        reloadBox.disableProperty().bind(Bindings.not(reloadBtn.selectedProperty()));
        newBox.textProperty().addListener((observable, oldValue, newValue) -> selectTool(newBox.getText()));
        confirmBtn.setOnAction(e -> startJob(true));
        blankBtn.setOnAction(event -> startJob(false));
    }

    private void startJob(boolean mine) {
        if (mine) {
            boolean reload = reloadBtn.isSelected();
            String newProject = newBox.getText();
            String tool = toolBox.getSelectionModel().getSelectedItem();
            List<String> selectedProjects = reloadBox.getSelectionModel().getSelectedItems();
            if (!checkInputs(reload, newProject, tool, selectedProjects)) return;
            processForm(reload, newProject, tool, selectedProjects);
        } else
            gui.getApp().createBlankDB();
        gui.refreshProjects();
    }

    private boolean checkInputs(boolean reload, String newProject, String tool, List<String> selectedProjects) {
        if (!reload) {
            if (!newProject.trim().isEmpty()) {
                if (tool == null) {
                    showError("No tool specified!", "Please select a tool for the new project data");
                    return false;
                }
            }/* else {
                this.showError("No URL specified!", "Please input the URL of the project data to mine");
                return false;
            }*/
        } else if (selectedProjects.isEmpty()) {
            showError("No projects selected!", "Please choose at least one project");
            return false;
        }
        return true;
    }

    private void selectTool(String text) {
        String toolName = gui.getApp().guessTool(text);
        if (toolName.isEmpty()) {
            toolBox.getSelectionModel().clearSelection();
        }
        toolBox.getSelectionModel().select(toolName);
    }

    private void processForm(boolean reload, String newProject, String tool, List<String> selectedProjects) {
        Map<String, String> loginResults;

        if (!reload) {
            // one new project
            if (!newProject.trim().isEmpty()) {
                loginResults = showLoginDialog(tool);
                if (loginResults != null) {
                    gui.getApp().processProjectInstance(newProject, loginResults, tool);
                }
            // project list from file
            } else {
                gui.getApp().mineFromFile(tool + ".txt");
            }
        // reload projects
        } else {
            for (String url : selectedProjects) {
                loginResults = showLoginDialog(url);
                gui.getApp().processProjectInstance(url, loginResults, null);
            }
        }
    }

    @Override
    public void refreshProjects(List<String> projects) {
        reloadBox.getItems().clear();
        reloadBox.getItems().addAll(projects);
    }

    private Map<String, String> showLoginDialog(String url) {
        Dialog<Map<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Login");
        dialog.setHeaderText("Please input login information for the project data in\n" + url);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));
        dialog.getDialogPane().setContent(grid);

        FileChooser fileChooser = new FileChooser();
        TextField username = new TextField();
        PasswordField password = new PasswordField();
        TextField privateKey = new TextField();
        Button openButton = new Button("Browse");
        ButtonType loginButtonType = new ButtonType("Confirm", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

        grid.add(new Label("Username:"), 0, 0);
        grid.add(username, 1, 0, 2, 1);
        grid.add(new Label("Password:"), 0, 1);
        grid.add(password, 1, 1, 2, 1);
        grid.add(new Label("Private key:"), 0, 2);
        grid.add(privateKey, 1, 2);
        grid.add(openButton, 2, 2);

        username.setPromptText("Username");
        password.setPromptText("Password");
        privateKey.setPromptText("Private key");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Private key files (*.ppk)", "*.ppk"));

        openButton.setOnAction(
                e -> {
                    File file = fileChooser.showOpenDialog(dialog.getOwner());
                    if (file != null) {
                        privateKey.setText(file.getAbsolutePath());
                    }
                });

        Platform.runLater(username::requestFocus);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == loginButtonType) {
                Map<String, String> values = new LinkedHashMap<>();
                values.put("username", username.getText());
                values.put("password", password.getText());
                values.put("privateKey", privateKey.getText());
                for (Map.Entry<String, String> value : values.entrySet()) {
                    if (value.getValue().trim().isEmpty()) {
                        values.put(value.getKey(), null);
                    }
                }
                return values;
            }
            return null;
        });

        Optional<Map<String, String>> result = dialog.showAndWait();

        return result.orElse(null);
    }

    private void showError(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public TextArea getLogArea() {
        return logArea;
    }
}
