package org.openjfx;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;

public class PrimaryController implements Initializable {

    @FXML
    private LineChart<Number, Number> sensorChart;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        final XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.getData().add(new XYChart.Data<>(1, 23));
        series.getData().add(new XYChart.Data<>(2, 14));
        series.getData().add(new XYChart.Data<>(3, 15));
        series.getData().add(new XYChart.Data<>(4, 24));
        series.getData().add(new XYChart.Data<>(5, 34));
        series.getData().add(new XYChart.Data<>(6, 36));
        series.getData().add(new XYChart.Data<>(7, 22));
        series.getData().add(new XYChart.Data<>(8, 45));
        series.getData().add(new XYChart.Data<>(9, 43));
        series.getData().add(new XYChart.Data<>(10, 17));
        series.getData().add(new XYChart.Data<>(11, 29));
        series.getData().add(new XYChart.Data<>(12, 25));

        sensorChart.getData().add(series);
    }
}
