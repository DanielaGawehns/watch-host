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


    /**
     * List of {@link Chart} which are present on the overview
     */
    private List<Chart> charts = new ArrayList<>();


    /**
     * Setup function for the controller
     * @param data Initial data to fill {@link Chart}(s) using {@link OverviewController#setCharts(List)}
     * @param watches The amount of watches that are currently active. Will be set in {@link OverviewController#setLabels(int, int)}
     * @param measurements The amount of measurements that are currently active. Will be set in {@link OverviewController#setLabels(int, int)}
     */
    void setup(List<SensorData> data, int watches, int measurements){
        setCharts(data);
        setLabels(watches, measurements);
    }


    /**
     * Sets the initial charts on the screen. It creates a separate chart for every sensor found in the {@code data} list
     * using {@link OverviewController#findChart(String)} and {@link Chart#addData(SensorData)}
     * @param data The list of {@link SensorData} to be added
     */
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


    /**
     * Checks if a {@link Chart} for a specific sensor already exist. If not it creates a new one
     * @param sensor Sensor to check
     * @return {@link Chart} that has been created. {@code null} if a {@link Chart} was already found
     */
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
     * Event for Export from all watches button. Creates a FileChooser popup and exports all data using {@link CSVWriter}
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