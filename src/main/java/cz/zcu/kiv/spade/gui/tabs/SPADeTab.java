package cz.zcu.kiv.spade.gui.tabs;

import cz.zcu.kiv.spade.gui.SPADeGUI;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.Tab;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;

import java.util.List;

public abstract class SPADeTab extends Tab {

    final SPADeGUI gui;
    final GridPane grid;

    SPADeTab(String text, SPADeGUI gui) {
        super(text);

        this.gui = gui;

        grid = new GridPane();
        grid.setAlignment(Pos.TOP_LEFT);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        this.setContent(grid);
    }

    public abstract void refreshProjects(List<String> projects);

    void setRowHeightPercentages(double... heights) {
        for (double height : heights) {
            RowConstraints rc = new RowConstraints();
            rc.setPercentHeight(height);
            grid.getRowConstraints().add(rc);
        }
    }

    void setRowVAlignment(VPos vAlignment, int... rowIndexes) {
        for (int rowIndex : rowIndexes) {
            grid.getRowConstraints().get(rowIndex).setValignment(vAlignment);
        }
    }

    void setColumnWidths(int... widths) {
        for (int width : widths) {
            grid.getColumnConstraints().add(new ColumnConstraints(width));
        }
    }

    void setColumnHAlignment (HPos hAlignment, int... columnIndexes) {
        for (int columnIndex : columnIndexes) {
            grid.getColumnConstraints().get(columnIndex).setHalignment(hAlignment);
        }
    }
}
