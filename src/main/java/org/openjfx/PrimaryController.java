package org.openjfx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.TabPane;
import javafx.scene.input.MouseEvent;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

// Controller of main screen
public class PrimaryController{

    // lineChart from primary.fxml
    @FXML
    private LineChart<Number, Number> sensorChart;

    // tabPane from primary.fxml
    @FXML
    private TabPane tabPane;

    // List of smartwatches connected
    private List<Smartwatch> watches = new ArrayList<Smartwatch>();

    // Reader for reading CSV files
    private CSVFileReader reader = new CSVFileReader();

    // which smartwatch is selected for charting
    private int currentWatch;

    // constructor
    public void initialize() throws IOException {
        System.out.println("INITIALIZE Primary Controller");
        Random rand = new Random();
        /*for(int i = 0; i < 2; i++){
            watches.add(new Smartwatch(rand.nextInt(10000)));
        }*/
        watches.add(new Smartwatch(1));
        watches.add(new Smartwatch(2));
    }


    // fill sensorChart with data from sensor
    private void fillChart(String sensor) {
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        System.out.println("Fill Chart for watch: " + (currentWatch-1));
        SensorData sensorData = watches.get(currentWatch - 1).getSensorData(sensor);

        if(sensorData == null) // if no data found
            throw new NullPointerException();

        sensorChart.setDisable(false); // turn on chart
        sensorChart.setAnimated(false); // disable animation for clearing
        sensorChart.getData().clear();
        sensorChart.setAnimated(true);

        System.out.println("Added series");
        for(int i = 0; i < sensorData.size(); i++){
            XYChart.Data<Number, Number> temp = sensorData.getDataPoint(i);
            System.out.println("Adding: " + sensorData.getDataPoint(i).toString());
            series.getData().add(sensorData.getDataPoint(i)); // add datapoint to series

        }
        sensorChart.getData().add(series); // add series to chart
        series.setName(sensorData.getSensor()); // set title of line for legend
        sensorChart.setTitle(sensorData.getSensor()); // set title of chart
    }


    // event for logo 1
    public void watchlogo1Pressed(MouseEvent mouseEvent) {
        watchlogoPressed(1);
    }


    // event for logo 2
    public void watchlogo2Pressed(MouseEvent mouseEvent) {
        watchlogoPressed(2);
    }


    // Moves to right tab
    // Sets currentWatch and fills the chart
    private void watchlogoPressed(int number){
        moveToTab(0);
        currentWatch = number;
        try {
            fillChart("HR");
        }catch (Exception e){ // if data is not found
            System.out.println("No data found for watch: " + (currentWatch-1) + " and sensor: TEMP");
            sensorChart.setDisable(true);
            return;
        }

    }


    // switch to overview tab
    // TODO: make this nicer with separate fxml file
    public void switchToOverview(ActionEvent actionEvent) {
        moveToTab(1);
    }


    // handles tab moves
    private void moveToTab(int number){
        tabPane.getSelectionModel().select(number);
    }


    // Go to input dir and read all files
    // TODO: replace this
    private void syncFiles(File folder) {
        SensorData sensorData;
        System.out.println("Start reader folder: " + folder.getAbsolutePath());
        for(final File fileEntry : Objects.requireNonNull(folder.listFiles())){
            System.out.println(fileEntry.getAbsoluteFile());
            sensorData = reader.readFile(fileEntry.getAbsolutePath());
            watches.get(sensorData.getWatchNumber()).setSensorData(sensorData);
        }

    }


    // Event for syncButton
    public void syncButtonPressed(ActionEvent actionEvent) {
        syncFiles(new File(System.getProperty("user.dir") + "\\src\\main\\resources\\input\\"));
    }
}
