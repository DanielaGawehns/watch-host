package org.openjfx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


// Controller of main screen
// Controller of primary.fxml
public class PrimaryController{

    @FXML
    private BorderPane view;

    // List of smartwatches connected
    private List<Smartwatch> watches = new ArrayList<>();

    // Reader for reading CSV files
    private CSVFileReader reader = new CSVFileReader();

    // Controller for the watchView
    private WatchViewController watchController;

    // which smartwatch is selected for charting
    private int currentWatch;

    // constructor
    // TODO: remove adding of smartwatches
    public void initialize() throws IOException {
        System.out.println("INITIALIZE Primary Controller");
        /*Random rand = new Random();
        for(int i = 0; i < 2; i++){
            watches.add(new Smartwatch(rand.nextInt(10000)));
        }*/
        watches.add(new Smartwatch(1)); // TEMP: add watch 1
        watches.add(new Smartwatch(2)); // TEMP: add watch 2
        currentWatch = 1;
        loadOverviewFXML();
    }


    // event for logo 1
    public void watchlogo1Pressed(MouseEvent mouseEvent) throws IOException {
        watchlogoPressed(1);
    }


    // event for logo 2
    public void watchlogo2Pressed(MouseEvent mouseEvent) throws IOException {
        watchlogoPressed(2);
    }


    private void loadOverviewFXML() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("overview.fxml")); // load fxml file
        BorderPane newPane = loader.load(); // load file into replacement pane

        newPane.prefWidthProperty().bind(view.widthProperty()); // bind width of newPane to the old one
        view.setCenter(newPane); // set newPane as center of borderPane
    }


    // Loading watchView FXML into view
    private void loadWatchFXML() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("watchview.fxml")); // load fxml file
        BorderPane newPane = loader.load(); // load file into replacement pane

        watchController = loader.getController(); // set controller to controller of new file
        watchController.setWatch(watches.get(currentWatch-1)); // send Data of the watch being viewed to the controller

        newPane.prefWidthProperty().bind(view.widthProperty()); // bind width of newPane to the old one
        view.setCenter(newPane); // set newPane as center of borderPane
    }


    // Moves to right tab
    // Sets currentWatch and fills the chart
    private void watchlogoPressed(int number) throws IOException {
        currentWatch = number; // set current watch
        loadWatchFXML();
    }


    // switch to overview tab
    public void switchToOverview(ActionEvent actionEvent) throws IOException {
        loadOverviewFXML();
    }


    // Go to input dir and read all files
    private void syncFiles(File folder) {
        SensorData sensorData;

        for(final File fileEntry : Objects.requireNonNull(folder.listFiles())){ // for all folders in map 'folder'
            sensorData = reader.readFile(fileEntry.getAbsolutePath()); // read sensorData
            watches.get(sensorData.getWatchNumber()).setSensorData(sensorData); // add data to the right watch
        }
    }


    // Event for syncButton
    public void syncButtonPressed(ActionEvent actionEvent) {
        syncFiles(new File(System.getProperty("user.dir") + "\\src\\main\\resources\\input\\")); // read files in input folder
        //watchController.setWatch(watches.get(currentWatch-1)); // set watch to last accessed watch TODO: maybe change this to prevent double/unnecessary setWatch
    }
}
