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

    // lineChart from primary.fxml
    @FXML
    private LineChart<Number, Number> sensorChart;

    // tabPane from primary.fxml
    @FXML
    private TabPane tabPane;

    // data
    private WatchData dataWatch = new WatchData();

    // fill sensorChart with data from dataWatch
    public void fillChart() {
        XYChart.Series<Number, Number> series = new XYChart.Series<>();

        sensorChart.getData().clear();
        sensorChart.getData().add(series); // add series to chart

        for(int i = 0; i < dataWatch.size(); i++){
            XYChart.Data<Number, Number> temp = dataWatch.getDataPoint(i);
            series.getData().add(dataWatch.getDataPoint(i)); // add datapoint to series

            // handler for clicking on data point:
            temp.getNode().setOnMouseClicked(e ->
                    pressNode(temp));

        }
        series.setName("Sensor x"); // set title of line for legend
        sensorChart.setTitle(dataWatch.getSensor()); // set title of chart
    }

    // prints out values x,y in the console
    // TODO: make a popup window with more information about measurements
    private void pressNode(XYChart.Data<Number, Number> data){
        System.out.println("Clicked on data: " + data.getXValue() + "," + data.getYValue());
    }

    // event for logo 1
    public void watchlogo1Pressed(MouseEvent mouseEvent) {
        watchlogoPressed(1);
    }

    // event for logo 2
    public void watchlogo2Pressed(MouseEvent mouseEvent) {
        watchlogoPressed(2);
    }

    // reads data from file and calls fillChart
    // TODO: read in data for all watches beforehand
    private void watchlogoPressed(int number){
        moveToTab(0);
        dataWatch.readData("src\\main\\resources\\input\\testfile" + number + ".csv", "Data for watch " + number);
        fillChart();
    }

    // switch to overview tab
    // TODO: make this nicer with seperate fxml file
    public void switchToOverview(ActionEvent actionEvent) {
        moveToTab(1);
    }

    // handles tab moves
    private void moveToTab(int number){
        tabPane.getSelectionModel().select(number);
    }

}
