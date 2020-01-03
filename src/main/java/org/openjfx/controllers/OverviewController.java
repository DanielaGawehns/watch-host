package org.openjfx.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.openjfx.*;
import util.Util;

import java.io.File;
import java.time.LocalDate;
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
     * DatePicker to select the starting date for printing data to charts
     */
    @FXML
    private DatePicker datePicker;


    /**
     * List of {@link Chart} which are present on the overview
     */
    private final List<Chart> charts = new ArrayList<>();


    /**
     * List of all smartwatches to extract data to fill the charts
     */
    private SmartwatchList watches;


    /**
     * Initializer to set {@link OverviewController#datePicker} standard value
     */
    public void initialize(){
        datePicker.setValue(LocalDate.now().minusDays(365)); // will be overwritten in setup
        Util.setDateFactory(datePicker);
    }

    /**
     * Setup function for the controller. Sets standard value of {@link OverviewController#datePicker}, fills charts
     * using {@link OverviewController#setCharts(List)} and sets information labels using {@link OverviewController#setLabels(int, int)}
     * @param watches The {@link SmartwatchList} with the data to be filled into the charts
     */
    void setup(SmartwatchList watches){
        this.watches = watches;
        datePicker.setValue(watches.getStartDate());
        setCharts(watches.getAllSensorData(watches.getStartDate()));
        setLabels(watches.size(), watches.getNumberOfMeasurements());
    }


    /**
     * Sets the initial charts on the screen. It creates a separate chart for every sensor found in the {@code data} list
     * using {@link OverviewController#findChart(String)} and {@link Chart#addData(SensorData)}
     * @param data The list of {@link SensorData} to be added
     */
    private void setCharts(List<SensorData> data){
        System.out.println("[OverviewController#setCharts] ********");
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

    /**
     * Event for the datePicker. Sets the start date of {@link OverviewController#watches} if it is different from current.
     * Then replaces data of charts with new start date value
     */
    public void startDatePressed() {
        LocalDate date = datePicker.getValue();

        System.out.println("\nSelected date " + date + "... Changing charts");
        if(watches.getStartDate() != date){
            watches.setStartDate(date);
            for(Chart chart : charts){
                for(Smartwatch watch : watches) {
                    System.out.println("[OverviewController#startDatePressed] changing data chart of sensor " + chart.getSensor()
                    + " and watch " + watch.getWatchID());
                    chart.setData(watch.getWatchID(), watch.getSensorData(chart.getSensor(), watches.getStartDate()));
                }
            }
        }
    }
}