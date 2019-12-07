package org.openjfx.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.openjfx.CSVWriter;
import org.openjfx.Chart;
import org.openjfx.SensorData;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * Class for controlling function for the overview screen
 * Controller for overview.fxml
 */
public class OverviewController {

    /**
     * Label for count of connected watches
     */
    @FXML
    private Label labelWatchConnected;

    /**
     * Label for count of active measurements
     */
    @FXML
    private Label labelActiveMeasurements;

    /**
     * VBox for placing the charts and other elements
     */
    @FXML
    private VBox chartsVbox;

    private List<Chart> charts = new ArrayList<>();


    void setup(List<SensorData> data, int watches, int measurements){
        setCharts(data);
        setLabels(watches, measurements);
    }

    private void setCharts(List<SensorData> data){
        Chart chart;
        for(SensorData sensorData : data){
            System.out.println("Got data from sensor " + sensorData.getSensor());
            chart = findChart(sensorData.getSensor());
            if(chart != null){
                chart.addData(sensorData);
            }else{
                charts.add(new Chart(sensorData, chartsVbox));
            }
        }
    }

    private Chart findChart(String sensor){
        for(Chart chart : charts){
            if(chart.getSensor().equals(sensor)){
                System.out.println("Found chart for sensor " + sensor);
                return chart;
            }
        }
        System.out.println("No chart found for sensor " + sensor);
        return null;
    }

    /**
     * Sets {@link OverviewController#labelWatchConnected} and {@link OverviewController#labelActiveMeasurements}
     * @param watches value to set {@link OverviewController#labelWatchConnected}
     * @param measurements value to set {@link OverviewController#labelActiveMeasurements}
     */
    private void setLabels(int watches, int measurements){
        labelWatchConnected.setText(watches + "");
        labelActiveMeasurements.setText(measurements + "");
    }


    /**
     * Event for Export from all watches button.
     */
    @FXML
    private void exportButton() {
        CSVWriter writer = new CSVWriter();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save file as");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("CSV Files", "*.csv"),
                new FileChooser.ExtensionFilter("All Files", "*.*"));
        Stage stage = new Stage();
        File selectedFile = fileChooser.showSaveDialog(stage);
        if (selectedFile != null) {
            writer.WriteAll(selectedFile);
        }
    }


}