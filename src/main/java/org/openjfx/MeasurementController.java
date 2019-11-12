package org.openjfx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Class for controlling function for the New Measurement screen
 * Controller for measurement.fxml
 */
public class MeasurementController {

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
    private List<Smartwatch> connectedWatchesList;

    /**
     * List of sensors which are selected
     */
     private List<Pair<String, Integer>> selectedSensors = new ArrayList<>();

    /**
     * List of {@link Smartwatch} which are selected to perform the measurement
     */
    private List<Smartwatch> selectedWatches = new ArrayList<>();

    /**
     * List of the TextFields for all the intervals entered by the user
     */
    private List<TextField> intervalFields = new ArrayList<>();

    /**
     * Instance of {@link Measurement}
     */
    Measurement measurement = new Measurement();

    /**
     * Instance of {@link PrimaryController}
     */
    private PrimaryController primaryController;


    /**
     * Setter for {@link MeasurementController#primaryController}
     */
    public void setPrimaryController(PrimaryController controller) { primaryController = controller; }


    /**
     * Loads the sensors into {@link MeasurementController#sensorList}, {@link MeasurementController#sensorList2} and {@link MeasurementController#sensorList3} which contains a list of all sensors from {@link MeasurementController#allSensors}
     */
    public void loadSensors() {
        final ToggleGroup tg = new ToggleGroup(); // needed to make selection of ToggleButtons work

        // place all sensors
        for(int i = 0; i < allSensors.size(); i++){
            String currentSensor = allSensors.get(i);

            VBox vbox = new VBox();
            HBox hbox = new HBox();

            ToggleButton button = new ToggleButton(); // Button to select the sensor
            button.setText(currentSensor);
            button.setStyle("-fx-font-size:10");

            // TextField to enter the interval for how often the sensor must be polled
            TextField interval = new TextField("100"); // todo: set more sensible default values
            interval.setMaxWidth(55.0);

            // ensures that the interval TextfField is aligned with the right of the HBox
            Region filler = new Region();
            hbox.setHgrow(filler, Priority.ALWAYS);

            Label ms = new Label("ms");

            // whitespace between the field for the current sensor and the one to the right
            Region endFiller = new Region();
            endFiller.setMinWidth(15.0);

            // handle selection of button
            // interval is set once the Start Measurement button is pressed, initially it is set to 0
            Pair<String, Integer> sensor = new Pair<>(currentSensor, 0);
            button.setOnAction((ActionEvent event) -> { // If clicked
                if (button.isSelected()) {
                    selectedSensors.add(sensor);
                    intervalFields.add(interval);
                } else {
                    selectedSensors.remove(sensor);
                    intervalFields.remove(interval);
                }
            });

            hbox.getChildren().addAll(button, filler, interval, ms, endFiller);
            hbox.setAlignment(Pos.CENTER_LEFT);

            vbox.getChildren().addAll(hbox);
            vbox.setAlignment(Pos.CENTER);

            // divide sensors over the 3 sensorLists
            if (i < Math.ceil(allSensors.size()/3.0)) {
                sensorList.getChildren().add(vbox);
            } else if (i < 2 * Math.ceil(allSensors.size()/3.0)) {
                sensorList2.getChildren().add(vbox);
            } else {
                sensorList3.getChildren().add(vbox);
            }
        }
    }


    /**
     * Loads the connected watches in {@link MeasurementController#connectedWatches} which contains a list of connected watches from {@link MeasurementController#connectedWatchesList}
     */
    public void loadWatches() {
        connectedWatchesList = PrimaryController.getWatches();

        final ToggleGroup tg = new ToggleGroup(); // needed to make selection of ToggleButtons work

        // place all connected watches
        for (int i = 0; i < connectedWatchesList.size(); i++){
            Smartwatch watch = connectedWatchesList.get(i);

            VBox vbox = new VBox();
            HBox hbox = new HBox();

            ToggleButton button = new ToggleButton();

            String watchID = Integer.toString(watch.getWatchID());
            Label id = new Label( "id: " + watchID);
            id.setStyle("-fx-font-size:10");
            id.setStyle("-fx-padding: 5 10 5 10;");

            String watchName = watch.getWatchName();
            button.setText(watchName);
            button.setStyle("-fx-font-size:10");

            // handle selection of button
            button.setOnAction((ActionEvent event) -> { // If clicked
                if (button.isSelected()) {
                    selectedWatches.add(watch);
                } else {
                    selectedWatches.remove(watch);
                }
            });

            hbox.getChildren().addAll(button, id);
            hbox.setAlignment(Pos.CENTER_LEFT);

            vbox.getChildren().addAll(hbox);
            vbox.setAlignment(Pos.CENTER);

            connectedWatches.getChildren().add(vbox);
        }
    }


    /**
     * Loads the {@link MeasurementController#durationTextField} into {@link MeasurementController#durationHBox}
     */
    public void loadDurationField() {
        VBox vbox = new VBox();
        HBox hbox = new HBox();

        durationTextField.setMaxWidth(60.0);
        Label minutes = new Label("minutes");

        hbox.getChildren().addAll(durationTextField, minutes);
        hbox.setAlignment(Pos.CENTER_LEFT);

        vbox.getChildren().addAll(hbox);
        vbox.setAlignment(Pos.CENTER);

        durationHBox.getChildren().add(vbox);
    }


    /**
     * Event for the Start Measurement button. First checks if all data for the measurement is valid
     * If this is the case runs {@link Measurement#setSensors(List)} and {@link Measurement#setDuration(Integer)} to store the measurement information
     * All the data on the measurement is sent to the selected watches
     * Afterwards run {@link PrimaryController#switchToOverview()} to return to the overview screen
     * @throws IOException Thrown by {@link PrimaryController#switchToOverview()}
     */
    public void startMeasurement() throws IOException {
        // todo: check if there is no active measurement

        // check if the user given interval is valid for each selected sensor
        for (int i = 0; i < selectedSensors.size(); i++) {
            Pair<String, Integer> currentSensor = selectedSensors.get(i);

            TextField currentInterval = intervalFields.get(i);
            Integer interval;
            try {
                interval = Integer.parseInt(currentInterval.getText());
            } catch (NumberFormatException ex) {
                ex.printStackTrace();
                return;
            }

            // check if the user given interval is valid for the current sensor
            if (interval > 0) { // todo: more rigorous checking based on type of sensor
                currentSensor.setSecond(interval); // interval is valid, store the value
            } else {
                return; // interval not valid, cannot start a measurement
            }
        }

        // check if the given duration is a valid number
        Integer duration;
        try {
            duration = Integer.parseInt(durationTextField.getText());
        } catch (NumberFormatException ex) {
            ex.printStackTrace();
            durationTextField.setStyle("-fx-text-inner-color: red;");
            durationTextField.setText("integer expected");
            return;
        }

        // check if given duration is valid
        if (duration <= 0) {
            return;
        }

        // store all values for the measurement
        measurement.setSensors(selectedSensors);
        measurement.setDuration(duration);

        // todo: send signal to watches
        // save measurement for each selected watch
        for (int i = 0; i < selectedWatches.size(); i++) {
            Smartwatch curr = selectedWatches.get(i);
            curr.setMeasurement(measurement);
        }

        // the measurement has started, switch to the overview tab
        primaryController.switchToOverview();
    }
}
