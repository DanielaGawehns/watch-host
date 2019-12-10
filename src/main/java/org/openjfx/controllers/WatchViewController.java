package org.openjfx.controllers;


import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.openjfx.*;
import util.Util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;



/**
 * Class for controlling functions from the watchView screen
 * Controlling watchview.fxml
 */
public class WatchViewController {

    private static final String COMMA_DELIMITER = ",";

    /**
     * Label for measurement duration
     */
    @FXML
    private Label durationLabel;

    /**
     * Vbox for placing the sensor labels
     */
    @FXML
    private  VBox measurementLabels;

    /**
     * ProgressBar for visualizing the memory that is used
     */
    @FXML
    private ProgressBar storageBar;

    /**
     * Label for the storage. Should be of format {@code <used>/<max>}
     */
    @FXML
    private Label storageLabel;

    /**
     * Label for the batteryLevel
     */
    @FXML
    private Label batteryLevelLabel;

    /**
     * Label for the battery time remaining
     */
    @FXML
    private Label batteryRemainingLabel;

    /**
     * Label for the watch nickname
     */
    @FXML
    private Label watchNameLabel;

    /**
     * Label for the watchID
     */
    @FXML
    private Label watchNrLabel;

    /**
     * VBox to contain the charts and comments
     */
    @FXML
    private VBox chartsBox;


    /**
     * VBox in which to place the researchers comments
     */
    private VBox commentsBox = new VBox();


    /**
     * List of {@link Chart} containing all the charts in the watch view
     */
    private List<Chart> charts = new ArrayList<>();

    /**
     * The {@link Smartwatch} of which the overview is showed
     */
    private Smartwatch watch;

    /**
     * Manager for managing the Database connection {@link DBManager}
     */
    private static DBManager dbManager = new DBManager();

    /**
     * Controller of {@link PrimaryController}
     */
    private PrimaryController primaryController;


    /**
     * Sets {@link WatchViewController#watch} and fills the charts using {@link Chart#Chart(SensorData, VBox)}
     * Also calls {@link WatchViewController#setInfo()} to set all the information labels
     * @param _watch The {@link Smartwatch} which data will be shown
     */
    void setWatch(Smartwatch _watch, PrimaryController controller){
        System.out.println("Setting watch... with id " + _watch.getWatchID());
        watch = _watch;
        primaryController = controller;

        setInfo();

        try {
            for (String sensor : watch.getSensorListFromMap()) {
                charts.add(new Chart(watch.getSensorData(sensor), chartsBox));
            }
            setComments();
        }catch (Exception e){ // if data is not found
            System.out.println("No data found for watch: " + " and sensor: TEMP");
            return;
        }
        System.out.println("Done filling chart");
    }


    /**
     * Sets all the information fields in the watch view
     */
    private void setInfo(){
        setStorageInfo();
        setBatteryInfo();
        setWatchInfo();
        setMeasurementInfo();
    }


    /**
     * Sets storage information items in watchInfo section
     */
    private void setStorageInfo(){
        float maxStorage = watch.getWatchData().getMaxStorage();
        float usedStorage = watch.getWatchData().getUsedStorage();
        String labelText = usedStorage + "/" + maxStorage + " MB used";

        storageBar.setProgress(usedStorage / maxStorage);
        storageLabel.setText(labelText);
    }


    /**
     * Sets battery information items in watchInfo section
     */
    private void setBatteryInfo(){
        batteryLevelLabel.setText(watch.getBatteryPercentage() + "%");
        //TODO: add calculation for remaining time
    }


    /**
     * Sets watch information items in watchInfo section
     */
    private void setWatchInfo(){
        watchNameLabel.setText(watch.getWatchName());
        watchNrLabel.setText(watch.getWatchID() + "");
    }


    /**
     * Sets measurement information in measurementInfo section
     */
    private void setMeasurementInfo(){
        Measurement measurement = watch.getMeasurement();
        Label label;

        if(measurement == null || measurement.size() <= 0){
            label = new Label("No measurement active.");
            measurementLabels.getChildren().add(label);
            return;
        }

        var sensors = measurement.getSensors();

        for(var sensor : sensors){
            String text = sensor.first() + " - " + sensor.second() + " ms";
            label = new Label(text);
            measurementLabels.getChildren().add(label);
        }
        String duration = "From " + measurement.getTimeStart() + " to " + measurement.getTimeEnd();
        durationLabel.setText(duration);
    }


    /**
     * Event for the options button
     */
    public void optionsPressed() {
        primaryController.showOptions(watch);
    }


    /**
     * Event for disconnect button. Uses {@link DBManager#removeSmartwatch(int)} and {@link PrimaryController#removeWatch(int)}
     * to remove the watch
     */
    public void disconnectButtonPressed() {
        Alert alert = Util.printChoiceBox("Disconnecting watch...",
                                        "This will remove ALL data about the watch",
                                         "Press OK to continue");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            dbManager.removeSmartwatch(watch.getWatchID());
            primaryController.removeWatch(watch.getWatchID());
        }
    }

    /**
     * Event for Export from one watch button.
     */
    public void exportOneCSVButtonPressed(){
        CSVWriter writer = new CSVWriter();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save file as");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("CSV Files", "*.csv"),
                new FileChooser.ExtensionFilter("All Files", "*.*"));
        Stage stage = new Stage();
        File selectedFile = fileChooser.showSaveDialog(stage);
        if(selectedFile != null) {
            writer.WriteOne(watch, selectedFile, true);
        }
    }


    /**
     * Event for Add Comments button
     */
    public void addComments() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("CSV Files", "*.csv"),
                new FileChooser.ExtensionFilter("Text Files", "*.txt"),
                new FileChooser.ExtensionFilter("All Files", "*.*"));

        Stage stage = new Stage();
        File selectedFile = fileChooser.showOpenDialog(stage);

        if(selectedFile != null){
            readComments(selectedFile);
            setComments();
        }
    }


    /**
     * Event for measurement stop button. Uses {@link DBManager#removeMeasurementFromWatch(int)} and {@link Smartwatch#setMeasurement(Measurement)}
     * to remove the measurement from the watch
     */
    public void stopPressed() {
        if (watch.getMeasurement() != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Removing measurement...");
            alert.setHeaderText("This will stop the measurement on the watch");
            alert.setContentText("Press OK to continue");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
                dbManager.removeMeasurementFromWatch(watch.getWatchID());
                watch.setMeasurement(null);

                //TODO: stop measurement on the watch
            }
            primaryController.loadWatchFXML();
        }
    }


    /**
     * Reads a csv file, parses and stores the data
     * If comments were already present, overwrite them with the content of the new file
     * @param file Specifies file to read from
     */
    private void readComments(File file){
        // todo: store old comments to restore if new file is empty or invalid
        // clear comments and commentbox before reading in a new file
        var comments = watch.getComments();

        if (!comments.isEmpty()) {
            watch.getComments().clear();
            dbManager.removeComments(watch.getWatchID());
            commentsBox.getChildren().clear();
        }

        try (BufferedReader br = new BufferedReader(new java.io.FileReader(file))) { // read in file
            String line;
            while ((line = br.readLine()) != null) { // go through all the lines
                String[] record = line.split(COMMA_DELIMITER);
                System.out.println(record[0]);

                if (record.length < 4) {
                    System.out.println("Record error");
                    throw new ParseException("Length of records too small", 0);
                }

                try {
                    LocalTime t1 = LocalTime.parse(record[0].trim());
                    LocalTime t2 = LocalTime.parse(record[1].trim());
                    // todo: also require date?
                    String body = record[2].trim();
                    String type = record[3].trim();

                    Comment comment = new Comment();
                    comment.setStartingTime(t1);
                    comment.setEndTime(t2);
                    comment.setCommentBody(body);
                    comment.setCommentType(type);
                    watch.addComment(comment);
                    dbManager.addComment(watch.getWatchID(), comment);

                } catch (IllegalArgumentException | NullPointerException e) {
                    // todo: notify user of invalid input file
                    e.printStackTrace();
                }
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }


    /**
     * Places the comments from {@link Smartwatch#getComments()} into {@link WatchViewController#commentsBox} to display them on the screen
     */
    private void setComments() {
        var comments = watch.getComments();

        for (Comment comment : comments) {
            LocalTime t1Date = comment.getStartingTime();
            LocalTime t2Date = comment.getEndTime();
            String t1String = t1Date.toString();
            String t2String = t2Date.toString();
            Label t1 = new Label("  " + t1String + " - ");
            Label t2 = new Label(t2String + "\t");

            String bodyString = comment.getCommentBody();
            Label body = new Label(bodyString);

            Region leadingfiller = new Region();
            leadingfiller.setMinWidth(40.0);

            String type = comment.getCommentType();
            Label typecolor = new Label();
            typecolor.setMinWidth(40.0);
            typecolor.setMinHeight(5.0);
            if (type.equals("planned")) {
                typecolor.setStyle("-fx-fill: cornflowerblue;");
                typecolor.setStyle("-fx-background-color: cornflowerblue;");
            } else {
                typecolor.setStyle("-fx-fill: coral;");
                typecolor.setStyle("-fx-background-color: coral;");
            }

            VBox vbox = new VBox();
            HBox hbox = new HBox();

            System.out.println("t1: " + t1String);
            System.out.println("t2: " + t2String);
            System.out.println("body: " + bodyString);
            System.out.println("type: " + type);

            hbox.getChildren().addAll(leadingfiller, typecolor, t1, t2, body);
            hbox.setAlignment(Pos.CENTER_LEFT);

            vbox.getChildren().addAll(hbox);
            vbox.setAlignment(Pos.CENTER);

            commentsBox.getChildren().add(vbox);
            chartsBox.getChildren().add(commentsBox);
        }
    }
}

