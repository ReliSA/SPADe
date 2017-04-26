package cz.zcu.kiv.spade.gui.tabs;

import cz.zcu.kiv.spade.domain.ProjectInstance;
import cz.zcu.kiv.spade.domain.enums.Tool;
import cz.zcu.kiv.spade.gui.SPADeGUI;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

public class MiningTab extends SPADeTab {

    private TextField newBox;
    private ChoiceBox<String> toolBox;
    private TextArea logArea;
    private RadioButton reloadBtn;
    private ListView<String> reloadBox;
    private Text info;
    private ProgressBar progBar;
    private ProgressIndicator progInd;

    private long startTime;

    public MiningTab(SPADeGUI gui) {
        super("Mining", gui);

        // conponents
        RadioButton newBtn = new RadioButton("Load new project:");
        newBox = new TextField();
        toolBox = new ChoiceBox<>();
        reloadBtn = new RadioButton("Reload projects:");
        reloadBox = new ListView<>();
        logArea = new TextArea();
        Button confirmBtn = new Button("Mine projects");
        Button blankBtn = new Button("Create blank database");
        HBox btnHBox = new HBox(10);
        info = new Text();
        progBar = new ProgressBar();
        HBox progHBox = new HBox(10);
        progInd = new ProgressIndicator();

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
        grid.add(btnHBox, 0, 2, 6, 1);
        grid.add(info, 0, 3, 6, 1);
        progHBox.getChildren().addAll(progBar, progInd);
        grid.add(progHBox, 0, 4, 6, 1);

        setColumnWidths(120, 200, 30, 100, 450);
        setColumnHalignment(HPos.RIGHT, 3);

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

        info.setVisible(false);
        progBar.setVisible(false);
        progBar.setPrefWidth(800);
        progInd.setVisible(false);
        progHBox.setAlignment(Pos.CENTER);

        // data
        newBox.setPromptText("Project URL");
        for (Tool tool : Tool.values()) {
            toolBox.getItems().add(tool.name());
            if (tool.equals(Tool.GIT)) toolBox.getSelectionModel().select(tool.name());
        }

        // behavior
        reloadBox.disableProperty().bind(Bindings.not(reloadBtn.selectedProperty()));
        newBox.textProperty().addListener((observable, oldValue, newValue) -> selectTool(newBox.getText()));
        confirmBtn.setOnAction(e -> startJob(true));
        blankBtn.setOnAction(event -> startJob(false));
    }

    private void startJob(boolean mine) {
        setProgress(0);
        setStartJobLog();
        if (mine) {
            boolean reload = reloadBtn.isSelected();
            String newProject = newBox.getText();
            String tool = toolBox.getSelectionModel().getSelectedItem();
            List<String> selectedProjects = reloadBox.getSelectionModel().getSelectedItems();
            if (!checkInputs(reload, newProject, tool, selectedProjects)) return;
            processForm(reload, newProject, tool, selectedProjects);
        } else
            createDb();
        setProgress(1);
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
        Map<String, String> loginResults = new HashMap<>();

        if (!reload) {
            // one new project
            if (!newProject.trim().isEmpty()) {
                loginResults = showLoginDialog(tool);
                if (loginResults != null) {
                    mineSingleProject(1, 1, newProject, tool, loginResults);
                }
            // project list from file
            } else {
                List<String> lines = readFile(tool + ".txt");

                loginResults.put("username", lines.get(0));
                loginResults.put("password", lines.get(1));
                if (lines.get(2).isEmpty()) loginResults.put("privateKey", null);
                else loginResults.put("privateKey", lines.get(2));

                for (int i = 3; i < lines.size(); i ++) {
                    mineSingleProject(lines.size() - 3, i - 2, lines.get(i), tool, loginResults);
                }
            }
        // reload projects
        } else {
            int i = 1;
            for (String url : selectedProjects) {
                loginResults = showLoginDialog(url);
                mineSingleProject(selectedProjects.size(), i++, url, null, loginResults);
            }
        }
    }

    private void setStartJobLog() {
        startTime = System.currentTimeMillis();

        String separator = "----------New Job----------";
        if (!logArea.getText().isEmpty()) separator = "\n" + separator;
        logArea.setText(logArea.getText() + separator);
        info.setText("");
        info.setVisible(true);
        progBar.setVisible(true);
        progInd.setVisible(true);
    }

    private void createDb() {
        String logLine = "Initializing DB ...";
        printLogline(logLine);

        gui.getApp().createBlankDB();

        logLine = "DB initialized";
        printLogline(logLine);

        gui.refreshProjects();
    }

    private void printLogline(String logLine) {
        logLine = getTimeStamp(startTime) + " - " + logLine;
        info.setText(logLine);
        System.out.println(logLine);
        logArea.setText(logArea.getText().concat("\n" + logLine));
    }

    private String getTimeStamp(long time) {
        long elapsed = System.currentTimeMillis() - time - 3600000;
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss.SSSS");
        return format.format(new Date(elapsed));
    }

    private List<String> readFile(String file) {
        List<String> lines = new ArrayList<>();
        BufferedReader reader;

        try {
            reader = new BufferedReader(
                        new InputStreamReader(
                        new FileInputStream(file)
                        , StandardCharsets.UTF_8));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("//")) break;
                lines.add(line.trim());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }

    private void mineSingleProject(int prjNumber, int order, String url, String tool, Map<String, String> loginResults) {
        String logLine = "Now mining "+ url + " ...";
        printLogline(logLine);

        long prevTime = System.currentTimeMillis();

        ProjectInstance pi;
        if (tool == null) pi = gui.getApp().reloadProjectInstance(url, loginResults);
        else pi = gui.getApp().loadProjectInstance(url, loginResults, tool);

        double progress = ((order * 2.0) - 1) / (prjNumber * 2);
        logLine = String.format("\"%s\" data mined (%s/%s - %.2f%%) - took %s", pi.getName(), order, prjNumber, progress * 100, getTimeStamp(prevTime));

        printLogline(logLine);
        setProgress(progress);
        prevTime = System.currentTimeMillis();

        gui.getApp().loadProjectInstance(pi);

        progress = order * 2.0 / (prjNumber * 2);
        logLine = String.format("\"%s\" data loaded (%s/%s - %.2f%%) - took %s", pi.getName(), order, prjNumber, progress * 100, getTimeStamp(prevTime));

        setProgress(progress);
        printLogline(logLine);

        gui.refreshProjects();
    }

    @Override
    public void refreshProjects(List<String> projects) {
        reloadBox.getItems().clear();
        reloadBox.getItems().addAll(projects);
    }

    private void setProgress(double progress) {
        progBar.setProgress(progress);
        progInd.setProgress(progress);
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
}
