package cz.zcu.kiv.spade.gui;

import cz.zcu.kiv.spade.App;
import cz.zcu.kiv.spade.domain.enums.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.text.Text;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

class ChartTab extends Tab {

    private final PieChart pieChart = new PieChart();
    private final Map<String, String> enums = new HashMap<>();
    private CategoryAxis xAxis = new CategoryAxis();
    private NumberAxis yAxis = new NumberAxis();
    private final BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
    private App app;
    private GridPane grid;
    private ComboBox<String> prjSelect;
    private ComboBox<String> enumSelect;
    private ComboBox<String> fldSelect;
    private CheckBox nullBox;
    private Text stats;

    ChartTab(App app) {
        super("Charts");
        this.app = app;

        grid = new GridPane();
        grid.setAlignment(Pos.TOP_LEFT);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        this.setContent(grid);

        prjSelect = new ComboBox<>();
        enumSelect = new ComboBox<>();
        fldSelect = new ComboBox<>();
        nullBox = new CheckBox("Include null");
        ToggleGroup chartGroup = new ToggleGroup();
        RadioButton pieBtn = new RadioButton("pie chart");
        RadioButton barBtn = new RadioButton("bar chart");
        stats = new Text();

        grid.add(new Label("Project: "), 0, 0);
        grid.add(prjSelect, 1, 0, 3, 1);
        grid.add(new Label("Field: "), 0, 1);
        grid.add(enumSelect, 1, 1);
        grid.add(fldSelect, 2, 1);
        grid.add(nullBox, 3, 1);
        grid.add(pieBtn, 0, 2, 2, 1);
        grid.add(barBtn, 0, 3, 2, 1);
        grid.add(stats, 0, 4, 2, 1);
        grid.add(pieChart, 2, 2, 2, 3);

        addRow(5, false);
        addRow(5, false);
        addRow(5, false);
        addRow(5, true);
        addRow(90, true);

        grid.getColumnConstraints().add(new ColumnConstraints(45));
        grid.getColumnConstraints().add(new ColumnConstraints(120));
        grid.getColumnConstraints().add(new ColumnConstraints(100));
        ColumnConstraints cc = new ColumnConstraints();
        cc.setPercentWidth(63);
        grid.getColumnConstraints().add(cc);

        pieBtn.setToggleGroup(chartGroup);
        pieBtn.setSelected(true);
        barBtn.setToggleGroup(chartGroup);

        refreshProjects(app.getProjects());
        enums.put("Priority", "priorities");
        enums.put("Severity", "severities");
        enums.put("Status", "statuses");
        enums.put("Resolution", "resolutions");
        enums.put("WorkUnitType", "wuTypes");
        enumSelect.getItems().addAll(enums.keySet());
        fldSelect.getItems().addAll("name", "class", "superclass");

        enumSelect.getSelectionModel().selectFirst();
        fldSelect.getSelectionModel().selectFirst();

        fillChart();

        prjSelect.setOnAction(event -> fillChart());
        enumSelect.setOnAction(event -> {
            fillChart();
            if (getSelected(enumSelect).equals("WorkUnitType")) fldSelect.getItems().remove("superclass");
            else if (fldSelect.getItems().size() == 2) fldSelect.getItems().add("superclass");
        });
        fldSelect.setOnAction(prjSelect.getOnAction());
        nullBox.setOnAction(prjSelect.getOnAction());

        pieBtn.setOnAction(event -> {
            grid.getChildren().remove(barChart);
            grid.add(pieChart, 2, 2, 2, 3);
            fillChart();
        });
        barBtn.setOnAction(event -> {
            grid.getChildren().remove(pieChart);
            grid.add(barChart, 2, 2, 2, 3);
            fillChart();
        });
    }

    private void addRow(double heightPercentage, boolean top) {
        RowConstraints rc = new RowConstraints();
        rc.setPercentHeight(heightPercentage);
        if (top) rc.setValignment(VPos.TOP);
        grid.getRowConstraints().add(rc);
    }

    private String getSelected(ComboBox<String> selectBox) {
        return selectBox.getSelectionModel().getSelectedItem();
    }

    void refreshProjects(List<String> projects) {
        prjSelect.getItems().clear();
        prjSelect.getItems().add("ALL");
        prjSelect.getItems().addAll(projects);
        prjSelect.getSelectionModel().selectFirst();
    }

    private void fillChart() {
        String prj;
        if (getSelected(prjSelect).equals("ALL")) {
            prj = "all projects";
        } else {
            int index = getSelected(prjSelect).lastIndexOf('/') + 1;
            prj = getSelected(prjSelect).substring(index);
            if (prj.endsWith(App.GIT_SUFFIX)) prj = prj.substring(0, prj.indexOf(App.GIT_SUFFIX));
        }

        pieChart.setTitle("Work units from " + prj + " by " + getSelected(enumSelect) + "." + getSelected(fldSelect));
        if (nullBox.isSelected()) pieChart.setTitle(pieChart.getTitle() + " including null");
        barChart.setTitle(pieChart.getTitle());

        pieChart.getData().clear();
        barChart.getData().clear();

        xAxis.setLabel(getSelected(enumSelect) + "." + getSelected(fldSelect));
        yAxis.setLabel("Count");

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        barChart.getData().add(series);
        barChart.setLegendVisible(false);

        Map<String, Integer> data = collectData();
        int total = 0;
        for (Map.Entry<String, Integer> dataPoint : data.entrySet()) {
            total += dataPoint.getValue();
            pieChart.getData().add(new PieChart.Data(dataPoint.getKey(), dataPoint.getValue()));
            series.getData().add(new BarChart.Data<>(dataPoint.getKey(), dataPoint.getValue()));
        }

        refreshStats(total, data);
    }

    private void refreshStats(int total, Map<String, Integer> data) {
        StringBuilder statText = new StringBuilder();
        String format = "%-10s %20s\t( %.2f%% )\n";

        for (Map.Entry<String, Integer> dataPoint : data.entrySet()) {
            if (dataPoint.getValue() != 0) {
                double percent = (dataPoint.getValue() * 100.0) / total;
                statText.append(String.format(Locale.ROOT, format, dataPoint.getKey() + ": ", dataPoint.getValue(), percent));
            }
        }
        statText.insert(0, String.format("TOTAL:\t %d\n\n", total));
        statText.insert(0, "STATS\n\n");
        stats.setText(statText.toString());
    }

    private Map<String, Integer> collectData() {
        String url = getSelected(prjSelect);
        String enumer = getSelected(enumSelect);
        String column = getSelected(fldSelect);
        boolean includeNull = nullBox.isSelected();

        if (url.equals("ALL")) url = null;

        Map<String, Integer> data = new HashMap<>();

        int count;
        if (column.equals("name")) {
            for (String name : app.getEnumsByPrjUrl(enumer, enums.get(enumer), url)) {
                count = app.getUnitCountByEnumName(name, url, enumer);
                data.put(name, count);
            }
        } else {
            switch (enumer) {
                case "Priority":
                    data = withPriorities(url, column);
                    break;
                case "Status":
                    data = withStatuses(url, column);
                    break;
                case "Resolution":
                    data = withResolutions(url, column);
                    break;
                case "Severity":
                    data = withSeverities(url, column);
                    break;
                case "WorkUnitType":
                    data = withTypes(url, column);
                    break;
            }
        }
        if (includeNull) {
            count = app.getUnitCountWithNullEnum(url, enumer);
            data.put("null", count);
        }
        return data;
    }

    private Map<String, Integer> withPriorities(String url, String column) {
        Map<String, Integer> data = new HashMap<>();

        int count;
        if (column.equals("class")) {
            for (PriorityClass aClass : PriorityClass.values()) {
                count = app.getUnitCountByPriority(column, aClass.name(), url);
                data.put(aClass.name(), count);
            }

        } else if (column.equals("superclass")) {
            for (PrioritySuperClass superClass : PrioritySuperClass.values()) {
                count = app.getUnitCountByPriority(column, superClass.name(), url);
                data.put(superClass.name(), count);
            }

        }
        return data;
    }

    private Map<String, Integer> withStatuses(String url, String column) {
        Map<String, Integer> data = new HashMap<>();

        int count;
        if (column.equals("class")) {
            for (StatusClass aClass : StatusClass.values()) {
                count = app.getUnitCountByStatus(column, aClass.name(), url);
                data.put(aClass.name(), count);
            }

        } else if (column.equals("superclass")) {
            for (StatusSuperClass superClass : StatusSuperClass.values()) {
                count = app.getUnitCountByStatus(column, superClass.name(), url);
                data.put(superClass.name(), count);
            }

        }
        return data;
    }

    private Map<String, Integer> withResolutions(String url, String column) {
        Map<String, Integer> data = new HashMap<>();

        int count;
        if (column.equals("class")) {
            for (ResolutionClass aClass : ResolutionClass.values()) {
                count = app.getUnitCountByResolution(column, aClass.name(), url);
                data.put(aClass.name(), count);
            }

        } else if (column.equals("superclass")) {
            for (ResolutionSuperClass superClass : ResolutionSuperClass.values()) {
                count = app.getUnitCountByResolution(column, superClass.name(), url);
                data.put(superClass.name(), count);
            }

        }
        return data;
    }

    private Map<String, Integer> withSeverities(String url, String column) {
        Map<String, Integer> data = new HashMap<>();

        int count;
        if (column.equals("class")) {
            for (SeverityClass aClass : SeverityClass.values()) {
                count = app.getUnitCountBySeverity(column, aClass.name(), url);
                data.put(aClass.name(), count);
            }

        } else if (column.equals("superclass")) {
            for (SeveritySuperClass superClass : SeveritySuperClass.values()) {
                count = app.getUnitCountBySeverity(column, superClass.name(), url);
                data.put(superClass.name(), count);
            }

        }
        return data;
    }

    private Map<String, Integer> withTypes(String url, String column) {
        Map<String, Integer> data = new HashMap<>();

        int count;
        if (column.equals("class")) {
            for (WorkUnitTypeClass aClass : WorkUnitTypeClass.values()) {
                count = app.getUnitCountByType(column, aClass.name(), url);
                data.put(aClass.name(), count);
            }
        }
        return data;
    }
}
