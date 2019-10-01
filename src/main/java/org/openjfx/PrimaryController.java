package org.openjfx;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.TabPane;
import javafx.scene.input.MouseEvent;

public class PrimaryController{

    @FXML
    private LineChart<Number, Number> sensorChart;

    @FXML
    private TabPane tabPane;

    private WatchData dataWatch = new WatchData();

    public void fillChart() {

        List<List<String>> dataList = dataWatch.getRecords();

        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        sensorChart.getData().clear();
        sensorChart.getData().add(series);
        for(int i = 0; i < dataList.size(); i++){
            XYChart.Data<Number, Number> data = new XYChart.Data<>(Double.parseDouble(dataList.get(i).get(0)),Double.parseDouble(dataList.get(i).get(1)));
            series.getData().add(data);

            // handler for clicking on data point:
            data.getNode().setOnMouseClicked(e ->
                    pressNode(data));

        }
        series.setName("Sensor x");
        sensorChart.setTitle(dataWatch.getSensor());

    }

    private void pressNode(XYChart.Data<Number, Number> data){
        System.out.println("Clicked on data: " + data.getXValue() + "," + data.getYValue());
    }

    public void watchlogo1Pressed(MouseEvent mouseEvent) {
        watchlogoPressed(1);
    }

    public void watchlogo2Pressed(MouseEvent mouseEvent) {
        watchlogoPressed(2);
    }

    private void watchlogoPressed(int number){
        moveToTab(0);
        dataWatch.readData("src\\main\\resources\\input\\testfile" + number + ".csv", "Data for watch " + number);
        fillChart();
    }

    public void switchToOverview(ActionEvent actionEvent) {
        moveToTab(1);
    }

    private void moveToTab(int number){
        tabPane.getSelectionModel().select(number);
    }

}
