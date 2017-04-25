package cz.zcu.kiv.spade.gui.utils;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public class Record {
    private SimpleStringProperty name;
    private SimpleIntegerProperty count;
    private SimpleDoubleProperty percentage;

    public Record(String name, int count, double percentage){
        this.name = new SimpleStringProperty(name);
        this.count = new SimpleIntegerProperty (count);
        this.percentage = new SimpleDoubleProperty(Math.round(100.0 * percentage) / 100.0);
    }

    public String getName() {
        return name.get();
    }

    public SimpleStringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public int getCount() {
        return count.get();
    }

    public SimpleIntegerProperty countProperty() {
        return count;
    }

    public void setCount(int count) {
        this.count.set(count);
    }

    public double getPercentage() {
        return percentage.get();
    }

    public SimpleDoubleProperty percentageProperty() {
        return percentage;
    }

    public void setPercentage(double percentage) {
        this.percentage.set(percentage);
    }
}
