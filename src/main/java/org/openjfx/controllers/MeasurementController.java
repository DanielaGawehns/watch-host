package org.openjfx.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.openjfx.DBManager;
import org.openjfx.Measurement;
import org.openjfx.Smartwatch;
import org.openjfx.SmartwatchList;
import util.Pair;
import util.Util;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Class for controlling function for the New Measurement screen
 * Controller for measurement.fxml
 */
public class MeasurementController {
    @FXML
    private HBox endTimeHbox;
    @FXML
    private HBox startTimeHbox;
    @FXML
    private VBox watchVbox1;
    @FXML
    private VBox watchVbox2;
    @FXML
    private VBox watchVbox3;


    @FXML
    private VBox sensorVbox1;
    @FXML
    private VBox sensorVbox2;
    @FXML
    private VBox sensorVbox3;

    private DBManager dbManager = new DBManager();

    /**
     * List of all the possible sensors available
     */
    private List<String> allSensors = new ArrayList<>() {
        {
            add("ACCELEROMETER");
            add("GRAVITY");
            add("LINEAR ACCELERATION");
            add("MAGNETIC");
            add("ROTATION VECTOR");
            add("ORIENTATION");
            add("GYROSCOPE");
            add("LIGHT");
            add("PROXIMITY");
            add("PRESSURE");
            add("ULTRAVIOLET");
            add("TEMPERATURE");
            add("HUMIDITY");
            add("HRM");
        }
    };

    // todo: make this a single list which is automatically split
    /**
     * VBox containing the first third of the list of available sensors
     */
    @FXML
    private VBox sensorList;

    /**
     * VBox containing the second third of the list of available sensors
     */
    @FXML
    private VBox sensorList2;

    /**
     * VBox containing the third third of the list of available sensors
     */
    @FXML
    private VBox sensorList3;

    /**
     * HBox containing the TextField in which the user can enter the duration of the measurement
     */
    @FXML
    private HBox durationHBox;

    /**
     * TextField in which the user can enter the desired measurement duration
     */
    private TextField durationTextField = new TextField();

    /**
     * VBox containing the list of connected watches
     */
    @FXML
    private VBox connectedWatches;

    /**
     * List containing the connected watches
     */
    private SmartwatchList connectedWatchesList;

    /**
     * List of sensors which are selected
     */
    private List<Pair<String, TextField>> selectedSensors = new ArrayList<>();

    /**
     * List of {@link Smartwatch} which are selected to perform the measurement
     */
    private List<Smartwatch> selectedWatches = new ArrayList<>();

    /**
     * List of the TextFields for all the intervals entered by the user
     */
   // private List<TextField> intervalFields = new ArrayList<>();

    /**
     * Instance of {@link Measurement}
     */
    private Measurement measurement = new Measurement();

    /**
     * Instance of {@link PrimaryController}
     */
    private PrimaryController primaryController;


    /**
     * Setter for {@link MeasurementController#primaryController}
     */
    void setPrimaryController(PrimaryController controller) { primaryController = controller; }


    /**
     * Loads the sensors into {@link MeasurementController#sensorVbox1}, {@link MeasurementController#sensorVbox2} and {@link MeasurementController#sensorVbox3} which contain a list of all sensors from {@link MeasurementController#allSensors}
     */
    void loadSensors() {
        HBox hbox;
        CheckBox checkBox;
        int rowCount = allSensors.size() / 3 + 1;
        VBox vbox = sensorVbox1;

        for(int i = 0; i < allSensors.size(); i++){
            var sensor = allSensors.get(i);
            checkBox = new CheckBox(sensor);

            if(i ==  rowCount){ // Switch column
                vbox = sensorVbox2;
            }
            if(i == rowCount * 2 ){ // Switch column
                vbox = sensorVbox3;
            }
            hbox = new HBox(checkBox);
            hbox.setSpacing(10);
            hbox.setAlignment(Pos.CENTER_LEFT);

            // Region to space Textfield
            Region region = new Region();
            HBox.setHgrow(region, Priority.ALWAYS);

            TextField field = new TextField("100");
            field.setPrefWidth(50);
            field.setDisable(true);

            hbox.getChildren().addAll(region, field, new Label("ms"));

            // Event for checkBoxes
            CheckBox finalCheckBox = checkBox;
            var sensorPair = new Pair<>(sensor, field);
            checkBox.setOnAction((ActionEvent event) -> {
                if(finalCheckBox.isSelected()){
                    System.out.println("Sensor " + sensor + " is selected!");
                    selectedSensors.add(sensorPair);
                    field.setDisable(false);
                }else{
                    System.out.println("Sensor " + sensor + " is deselected!");
                    selectedSensors.remove(sensorPair);
                    field.setDisable(true);
                }
            });

            // Add all hboxes
            vbox.getChildren().add(hbox);
        }
    }


    /**
     * Loads the connected watches in {@link MeasurementController#connectedWatches} which contains a list of connected watches from {@link MeasurementController#connectedWatchesList}
     */
    void loadWatches() {
        connectedWatchesList = PrimaryController.getWatches();

        HBox hbox;
        CheckBox checkBox;
        int rowCount = connectedWatchesList.size() / 3 + 1;
        VBox vbox = watchVbox1;

        for(int i = 0; i < connectedWatchesList.size(); i++){
            var watch = connectedWatchesList.get(i);
            checkBox = new CheckBox(watch.getWatchName());

            if(i ==  rowCount){ // Switch column
                vbox = watchVbox2;
            }
            if(i == rowCount * 2 ){ // Switch column
                vbox = watchVbox3;
            }
            hbox = new HBox(checkBox);
            hbox.setSpacing(10);
            hbox.setAlignment(Pos.CENTER_LEFT);

            String watchID = watch.getWatchID() + "";
            Label id = new Label("ID: " + watchID);
            id.setStyle("-fx-font-size:10");
            id.setStyle("-fx-padding: 5 10 5 10;");

            // Region to space Label
            Region region = new Region();
            HBox.setHgrow(region, Priority.ALWAYS);
            hbox.getChildren().addAll(region, id);

            // Event for checkBoxes
            CheckBox finalCheckBox = checkBox;
            checkBox.setOnAction((ActionEvent event) -> {
                if(finalCheckBox.isSelected()){
                    System.out.println("Watch " + watch.getWatchID() + " is selected!");
                    selectedWatches.add(watch);
                }else{
                    System.out.println("Watch " + watch.getWatchID() + " is deselected!");
                   selectedWatches.remove(watch);
                }
            });

            // Add all hboxes
            vbox.getChildren().add(hbox);
        }
    }


    /**
     * Loads a timefield (3 TextFields + labels) into a HBox
     * @param msg Message in front of the TextFields
     * @param hbox HBox to store the elements in
     */
    private void loadTimeField(String msg, HBox hbox){
        int size = 50;
        hbox.setAlignment(Pos.CENTER);
        hbox.setSpacing(5);

        // Label with msg
        Label label = new Label(msg);
        label.setPadding(new Insets(0, 20, 0, 0));

        // Textfield with standard value 00
        TextField field = new TextField("00");
        field.setPrefWidth(size);

        hbox.getChildren().addAll(label, field);

        // More textfields
        for(int i = 1; i < 3; i++){
            label = new Label(":");
            field = new TextField("00");
            field.setPrefWidth(size);
            hbox.getChildren().addAll(label, field);
        }
        label = new Label("hh:mm:ss");
        label.setPadding(new Insets(0, 0, 0, 20));
        hbox.getChildren().add(label);
    }


    /**
     * Loads the start and end time fields using {@link MeasurementController#loadTimeField(String, HBox)}
     */
    void loadTimesField() {
        loadTimeField("start: ", startTimeHbox);
        loadTimeField("end: ", endTimeHbox);
    }


    /**
     * Parses the value of a string and checks if it is a valid Minute/Second value (between 0 and 59)
     * @param field Input to be parsed
     * @return Value of the string. {@code -1} on error
     */
    private int parseMinSecField(String field){
        int value;
        try{
            value = Integer.parseInt(field);
        }catch (NumberFormatException e){ // Parse error
            e.printStackTrace();
            Util.printErrorDialog("Time value error", "The chosen hour value is not valid. Choose another value to continue");
            return -1;
        }
        if(value < 0 || value > 59){ // Check if the value is valid
            Util.printErrorDialog("Time value error", "The chosen hour value is not valid. Choose another value to continue");
            return -1;
        }

        return value;
    }


    /**
     * Parses the value of a string and checks if it is a valid Hour value (between 0 and 23)
     * @param field Input to be parsed
     * @return Value of the string. {@code -1} on error
     */
    private int parseHoursField(String field){
        int value;
        try{
            value = Integer.parseInt(field);
        }catch (NumberFormatException e){ // Parse error
            e.printStackTrace();
            Util.printErrorDialog("Hour value error", "The chosen hour value is not valid. Choose another value to continue");
            return -1;
        }
        if(value < 0 || value > 23){ // Check if the value is valid
            Util.printErrorDialog("Hour value error", "The chosen hour value is not valid. Choose another value to continue");
            return -1;
        }

        return value;
    }


    /**
     * Parses a timeField (hh:mm:ss) and checks if the values are valid using {@link MeasurementController#parseHoursField(String)}
     * and {@link MeasurementController#parseMinSecField(String)}
     * @param hbox HBox containing the timeField
     * @return List of 3 integers containing valid time values. {@code null} on error
     */
    private List<Integer> parseTimeField(HBox hbox){
        List<Integer> timeList = new ArrayList<>();
        TextField field = (TextField) hbox.getChildren().get(1);
        int value = parseHoursField(field.getText());
        if(value != -1){
            timeList.add(value);
        }else{
            return null;
        }

        for(int i = 1; i < 3; i++){
            field = (TextField) hbox.getChildren().get(i * 2 + 1);
            value = parseMinSecField(field.getText());
            if(value != -1){
                timeList.add(value);
            }else{
                return null;
            }
        }

        return timeList;
    }


    /**
     * Event for the Start Measurement button. First checks if all data for the measurement is valid
     * If this is the case runs {@link Measurement#setSensors(List)}, {@link Measurement#setTimeStart(LocalTime)} and {@link Measurement#setTimeEnd(LocalTime)} to store the measurement information
     * All the data on the measurement is sent to the selected watches
     * Afterwards runs {@link Util#closeStage(Node)} to close the window
     */
    public void startMeasurement() {
        // todo: check if there is no active measurement
        List<Pair<String, Integer>> sensorList = new ArrayList<>();
        LocalTime startTime, endTime;


        if(selectedSensors.size() < 1){
            Util.printErrorDialog("Sensor error", "No sensor selected. Please choose more sensors to continue.");
            return;
        }

        if(selectedWatches.size() < 1){
            Util.printErrorDialog("Watch error", "No watch selected. Please choose more watches to continue.");
            return;
        }

        // check if the user given interval is valid for each selected sensor
        for (Pair<String, TextField> currentSensor : selectedSensors) {
            TextField currentInterval = currentSensor.second();
            Integer interval;
            try {
                interval = Integer.parseInt(currentInterval.getText());
            } catch (NumberFormatException ex) {
                ex.printStackTrace();
                Util.printErrorDialog("Interval error", "Chosen interval is not valid. Please choose another one to continue.");
                return;
            }

            // check if the user given interval is valid for the current sensor
            if (interval > 0) { // todo: more rigorous checking based on type of sensor
                sensorList.add(new Pair<>(currentSensor.first(), interval)); // interval is valid, store the value
            } else {
                Util.printErrorDialog("Interval error", "Chosen interval is not valid. Please choose another one to continue.");
                return; // interval not valid, cannot start a measurement
            }
        }

        // check if the given start and end times are valid
        var timeFields = parseTimeField(startTimeHbox);
        if(timeFields != null){
            startTime = LocalTime.of(timeFields.get(0), timeFields.get(1), timeFields.get(2));
        }else{
            return;
        }

        timeFields = parseTimeField(endTimeHbox);
        if(timeFields != null){
           endTime = LocalTime.of(timeFields.get(0), timeFields.get(1), timeFields.get(2));
        }else{
            return;
        }


        // store all values for the measurement
        measurement.setSensors(sensorList);
        measurement.setTimeStart(startTime);
        measurement.setTimeEnd(endTime);

        // todo: send signal to watches
        // save measurement for each selected watch
        List<Integer> IDList = new ArrayList<>();
        for (Smartwatch curr : selectedWatches) {
            curr.setMeasurement(measurement);
            IDList.add(curr.getWatchID());
        }
        dbManager.addMeasurement(IDList, measurement);

        // the measurement has started, close window
        Util.closeStage(watchVbox1);
    }
}
