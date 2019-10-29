package org.openjfx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MeasurementController {
    // List of all available sensors
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
    @FXML
    private VBox sensorList;
    @FXML
    private VBox sensorList2;
    @FXML
    private VBox sensorList3;

    @FXML
    private Button startMeasurementButton;

    @FXML
    private HBox durationHBox;
    private TextField durationTextField = new TextField();

    @FXML
    private VBox connectedWatches;
    private List<Smartwatch> connectedWatchesList;

    // List of sensors to measure with the desired interval
     private List<Pair<String, Integer>> selectedSensors = new ArrayList<>();

    // List of watches that have been selected to perform the measurement on
    private List<Smartwatch> selectedWatches = new ArrayList<>();

    // List of the textfields for all selected sensors
    // used to check if the given intervals are valid when the measurement is started
    private List<TextField> intervalFields = new ArrayList<>();

    // instance of Measurement class to store all data that must be sent to watches
    Measurement measurement = new Measurement();

    private PrimaryController primaryController;

    public void setPrimaryController(PrimaryController controller) { primaryController = controller; }

    public void loadSensors() {
        final ToggleGroup tg = new ToggleGroup();
        for(int i = 0; i < allSensors.size(); i++){
            VBox vbox = new VBox();
            HBox hbox = new HBox();

            ToggleButton button = new ToggleButton();
            TextField interval = new TextField("100"); // todo: set sensible default values

            // filler ensures that the interval textfield is aligned with the right of the hbox
            Region filler = new Region();
            hbox.setHgrow(filler, Priority.ALWAYS);

            interval.setMaxWidth(55.0);
            Label ms = new Label("ms");

            // create some whitespace between "ms" and the sensor to the right
            Region endFiller = new Region();
            endFiller.setMinWidth(15.0);


            String currentSensor = allSensors.get(i);
            button.setText(currentSensor);
            button.setStyle("-fx-font-size:10");

            // handle selection of button
            // todo: review if necessary after implementing the ability to start a measurement
            // interval is set once the Start Measurement button is pressed
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

            if (i < Math.ceil(allSensors.size()/3.0)) {
                sensorList.getChildren().add(vbox);
            } else if (i < 2 * Math.ceil(allSensors.size()/3.0)) {
                sensorList2.getChildren().add(vbox);
            } else {
                sensorList3.getChildren().add(vbox);
            }
        }
    }

    public void loadWatches() {
        connectedWatchesList = PrimaryController.getWatches();

        final ToggleGroup tg = new ToggleGroup();
        for (int i = 0; i < connectedWatchesList.size(); i++){
            VBox vbox = new VBox();
            HBox hbox = new HBox();

            ToggleButton button = new ToggleButton();

            Smartwatch watch = connectedWatchesList.get(i);
            String watchID = Integer.toString(watch.getWatchID());
            Label id = new Label( "id: " + watchID);
            id.setStyle("-fx-font-size:10");
            id.setStyle("-fx-padding: 5 10 5 10;");

            String watchName = watch.getWatchName();
            button.setText(watchName);
            button.setStyle("-fx-font-size:10");

            // handle selection of button
            // todo: review if necessary after implementing the ability to start a measurement
            button.setOnAction((ActionEvent event) -> { // If clicked
                if (button.isSelected()) { // watch is now selected
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

    public void startMeasurement(ActionEvent event) throws IOException {
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

            // check if the user given interval is valid
            if (interval > 0) { // todo: more rigorous checking based on type of sensor
                // interval is valid, store the value
                currentSensor.setSecond(interval);
            } else {
                return;
            }

            System.out.println("SENSOR: "+ currentSensor.first());
            System.out.println("INTERVAL: "+ currentSensor.second());
            System.out.println("");
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

        if (duration <= 0) {
            return;
        }

        measurement.setSensors(selectedSensors);
        measurement.setWatches(selectedWatches);
        measurement.setDuration(duration);

        // todo: send signal to watches

        primaryController.switchToOverview(event);
    }
}
