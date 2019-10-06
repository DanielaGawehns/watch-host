package org.openjfx;

import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;

// Class for controlling functions from the watchView screen
// Controlling watchview.fxml
public class WatchViewController {

    // Root BorderPane
    @FXML
    private BorderPane view;

    @FXML
    private GridPane grid;

    @FXML
    private LineChart<Number, Number> sensorChart;

    // The watch of which the overview is showed
    private Smartwatch watch;

    // set the watch that is shown
    void setWatch(Smartwatch _watch){
        System.out.println("Setting watch...");
        watch = _watch;

        grid.prefWidthProperty().bind(view.widthProperty()); // bind width of grid to the width of the borderPane
        try {
            fillChart("HR"); // fill Chart TODO: change parameter
        }catch (Exception e){ // if data is not found
            System.out.println("No data found for watch: " + " and sensor: TEMP");
            sensorChart.setDisable(true);
            return;
        }
        System.out.println("Done filling chart");
    }

    // fill sensorChart with data from sensor
    private void fillChart(String sensor) {
        XYChart.Series<Number, Number> series = new XYChart.Series<>(); // new series for adding data points
        System.out.println("Fill Chart for watch: " + watch.getWatchID());
        SensorData sensorData = watch.getSensorData(sensor); // get data of the right sensor

        sensorChart.setAnimated(false); // disable animation for clearing
        sensorChart.getData().clear();
        sensorChart.setAnimated(true);
        sensorChart.setDisable(false); // turn on chart

        for(int i = 0; i < sensorData.size(); i++){
            XYChart.Data<Number, Number> temp = sensorData.getDataPoint(i); // get dataPoint no. i
            System.out.println("Adding: " + temp.toString());
            series.getData().add(temp); // add datapoint to series

        }

        series.setName(sensorData.getSensor()); // set title of line for legend
        sensorChart.getData().add(series); // add series to chart
        sensorChart.setTitle(sensorData.getSensor()); // set title of chart
    }
}
