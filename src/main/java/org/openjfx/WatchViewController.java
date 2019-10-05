package org.openjfx;

import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;

public class WatchViewController {

    @FXML
    private ScrollPane scroll;

    @FXML
    private GridPane grid;

    @FXML
    private LineChart<Number, Number> sensorChart;

    private Smartwatch watch;

    public void setWatch(Smartwatch _watch){
        System.out.println("Setting watch...");
        watch = _watch;

        grid.prefWidthProperty().bind(scroll.widthProperty());
        try {
            fillChart("HR");
        }catch (Exception e){ // if data is not found
            System.out.println("No data found for watch: " + " and sensor: TEMP");
            sensorChart.setDisable(true);
            return;
        }
        System.out.println("Done filling chart");
    }

    // fill sensorChart with data from sensor
    private void fillChart(String sensor) {
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        System.out.println("Fill Chart for watch: " + watch.getWatchID());
        SensorData sensorData = watch.getSensorData(sensor);

        /*if(sensorData == null) // if no data found
            throw new NullPointerException();*/

        sensorChart.setDisable(false); // turn on chart
        sensorChart.setAnimated(false); // disable animation for clearing
        sensorChart.getData().clear();
        sensorChart.setAnimated(true);

        System.out.println("Added series");
        for(int i = 0; i < sensorData.size(); i++){
            XYChart.Data<Number, Number> temp = sensorData.getDataPoint(i);
            System.out.println("Adding: " + sensorData.getDataPoint(i).toString());
            series.getData().add(temp); // add datapoint to series

        }
        System.out.println("Done adding");
        series.setName(sensorData.getSensor()); // set title of line for legend
        System.out.println("Set series name");
        sensorChart.getData().add(series); // add series to chart
        System.out.println("Added series");

        sensorChart.setTitle(sensorData.getSensor()); // set title of chart
        System.out.println("Set title");
    }
}
