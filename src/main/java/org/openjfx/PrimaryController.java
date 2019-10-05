package org.openjfx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TabPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

// Controller of main screen
public class PrimaryController{

    /*// lineChart from primary.fxml
    @FXML
    private LineChart<Number, Number> sensorChart;

    // tabPane from primary.fxml
    @FXML
    private TabPane tabPane;*/

    // ScrollPane for filling in screen
    @FXML
    private ScrollPane scrollPane;

    // List of smartwatches connected
    private List<Smartwatch> watches = new ArrayList<>();

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


    // event for logo 1
    public void watchlogo1Pressed(MouseEvent mouseEvent) throws IOException {
        watchlogoPressed(1);
    }


    // event for logo 2
    public void watchlogo2Pressed(MouseEvent mouseEvent) throws IOException {
        watchlogoPressed(2);
    }


    private void loadFXML() throws IOException {
        System.out.println(System.getProperty("user.dir") + "\\src\\main\\resources\\org.openjfx\\watchView.fxml");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("watchView.fxml"));
        System.out.printf(loader.toString());

        //Parent root = loader.load();

        ScrollPane newPane = loader.load();

        WatchViewController watchController = loader.getController();
        System.out.println(watchController);
        watchController.setWatch(watches.get(currentWatch-1));



       // Region n = (Region) loader.load();
        newPane.prefWidthProperty().bind(scrollPane.widthProperty().subtract(5));
        //newPane.prefHeightProperty().bind(scrollPane.heightProperty().subtract(5));
        scrollPane.setContent(newPane);

        System.out.println("Filled pane");
    }


    // Moves to right tab
    // Sets currentWatch and fills the chart
    private void watchlogoPressed(int number) throws IOException {
        currentWatch = number;
        System.out.println("Current watch is now: " + currentWatch);
        moveToTab(0);

        /*try {
            fillChart("HR");
        }catch (Exception e){ // if data is not found
            System.out.println("No data found for watch: " + (currentWatch-1) + " and sensor: TEMP");
            sensorChart.setDisable(true);
            return;
        }*/
        System.out.println("Moved to tab KAAS");
    }


    // switch to overview tab
    // TODO: make this nicer with separate fxml file
    public void switchToOverview(ActionEvent actionEvent) throws IOException {
        moveToTab(1);
    }


    // handles tab moves
    private void moveToTab(int number) throws IOException {
        //tabPane.getSelectionModel().select(number);
        loadFXML();
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
