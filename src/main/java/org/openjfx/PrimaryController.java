package org.openjfx;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;

public class PrimaryController{

    @FXML
    private LineChart<Number, Number> sensorChart;

    public void fillChart(){
        WatchData data = new WatchData();
        data.readData("C:\\Users\\Willi\\IdeaProjects\\watch-host\\src\\main\\java\\org\\openjfx\\testfile.csv", "Sensor23");
        List<List<String>> dataList = data.getRecords();
        final XYChart.Series<Number, Number> series = new XYChart.Series<>();
        for(int i = 0; i < dataList.size(); i++){
            series.getData().add(new XYChart.Data<Number, Number>(Double.parseDouble(dataList.get(i).get(0)), Double.parseDouble(dataList.get(i).get(1))));
        }
        series.setName("Intensity");
        sensorChart.setTitle(data.getSensor());
        sensorChart.getData().removeAll();
        sensorChart.getData().add(series);
    }

   /* @Override
    public void initialize(URL location, ResourceBundle resources) {
        WatchData data = new WatchData();
        data.readData("C:\\Users\\Willi\\IdeaProjects\\watch-host\\src\\main\\java\\org\\openjfx\\testfile.csv", "Sensor23");
        List<List<String>> dataList = data.getRecords();
        final XYChart.Series<Number, Number> series = new XYChart.Series<>();
        for(int i = 0; i < dataList.size(); i++){
            series.getData().add(new XYChart.Data<Number, Number>(Double.parseDouble(dataList.get(i).get(0)), Double.parseDouble(dataList.get(i).get(1))));
        }

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
        series.setName("Intensity");
        sensorChart.setTitle(data.getSensor());
        sensorChart.getData().add(series);

    }*/
}
