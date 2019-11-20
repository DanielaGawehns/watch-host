package org.openjfx;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Optional;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


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
     * Linechart for the HRM
     */
    @FXML
    private LineChart<String, Number> sensorChart;

    /**
     * VBox in which to place the researchers comments
     */
    @FXML
    private VBox commentsBox;

    /**
     * Stores the comments from the researcher about the measurement, uses {@link Triplet}
     * The first item is the starting time, second is the end time, third is the actual comment of what occured
     */
    private List<Triplet<Date, Date, String>> comments = new ArrayList<>();

    /**
     * Linechart for the PRESSURE
     */
    @FXML
    public LineChart<String, Number> pressureChart;


    /**
     * The {@link Smartwatch} of which the overview is showed
     */
    private Smartwatch watch;

    /**
     * Manager for managing the Database connection {@link DBManager}
     */
    static DBManager dbManager = new DBManager();

    /**
     * Controller of {@link WatchOptionsController}
     */
    private WatchOptionsController watchOptionsController;

    /**
     * Controller of {@link PrimaryController}
     */
    private PrimaryController primaryController;


    /**
     * Sets {@link WatchViewController#watch} and fills the charts using {@link WatchViewController#fillChart(LineChart, String)}
     * @param _watch The {@link Smartwatch} which data will be shown
     */
    void setWatch(Smartwatch _watch, PrimaryController controller){
        System.out.println("Setting watch...");
        watch = _watch;
        primaryController = controller;

        setInfo();

        try {
            fillChart(sensorChart, "HRM"); // fill Chart TODO: change parameter
            //fillChart(pressureChart, "PRESSURE");
            setComments();
        }catch (Exception e){ // if data is not found
            System.out.println("No data found for watch: " + " and sensor: TEMP");
            sensorChart.setDisable(true);
            return;
        }
        System.out.println("Done filling chart");
    }


    // TODO: Make this work with all types of charts
    /**
     * Fills a Linechart with data
     * @param chart The Linechart to be filled
     * @param sensor The sensor that is used
     */
    private void fillChart(LineChart<String, Number> chart, String sensor) {
        XYChart.Series<String, Number> series = new XYChart.Series<>(); // new series for adding data points
        System.out.println("Fill Chart for watch: " + watch.getWatchID() + " and sensor " + sensor);
        SensorData sensorData = watch.getSensorData(sensor); // get data of the right sensor

        chart.setAnimated(false); // disable animation for clearing
        chart.getData().clear();
        chart.setDisable(false); // turn on chart

        series.setName(sensorData.getSensor()); // set title of line for legend
        chart.getData().add(series); // add series to chart
        chart.setTitle(sensorData.getSensor()); // set title of chart

        System.out.println("Data size is " + sensorData.size());

        for(int i = 0; i < sensorData.size(); i += 1){ //TODO: find more robust way to remove unnecessary nodes
            XYChart.Data<String, Number> temp = sensorData.getDataPoint(i); // get dataPoint no. i

            //temp.setNode(createDataNode());
            series.getData().add(temp); // add datapoint to series
        }
    }



    // TODO: maybe not needed
    /**
     * Created a clickable node at a datapoint in a chart and creates a label that can be filled
     * @return The {@code Node} that has been created
     */
    private static Node createDataNode() {
        var label = new Label();

        var pane = new Pane(label);
        pane.setShape(new Circle(4.0));
        pane.setScaleShape(false);
        pane.setStyle("-fx-background-color: transparent");

        pane.setOnMouseEntered(mouseEvent -> {
            pane.setStyle("-fx-background-color: grey");
        });


        pane.setOnMouseExited(mouseEvent -> {
            pane.setStyle("-fx-background-color: transparent");
            label.setText("");
        });

        pane.setOnMouseClicked(mouseEvent -> {
            pinWindow(pane, label);
        });

        label.setLayoutY(-10);

        return pane;
    }


    /**
     * Shown the pinWindow when a {@code Node} is clicked. Shows options to add or remove text to the label
     * @param pane The pane (Node) which was clicked
     * @param label The label that we write text to
     */
    private static void pinWindow(Pane pane, Label label){
        System.out.println("Clicked");
        final Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        VBox dialogVbox = new VBox();
        HBox hbox = new HBox();
        Button buttonSet = new Button("Add pin");
        Button buttonRemove = new Button("Remove pin");
        TextField field = new TextField();
        field.setPromptText("Type pin name...");
        hbox.getChildren().addAll(buttonSet, buttonRemove);
        dialogVbox.getChildren().addAll(new Text("Add pin to this node"), field, hbox);
        Scene dialogScene = new Scene(dialogVbox);
        dialog.setScene(dialogScene);
        dialog.show();

        buttonSet.setOnAction(e -> {
            label.setText(field.getText());
            // TODO: save label to csv
            pane.setStyle("-fx-background-color: red");
            pane.setOnMouseExited(mouseEvent -> {
                pane.setStyle("-fx-background-color: red");
            });
            dialog.close();
        });

        buttonRemove.setOnAction(e -> {
            label.setText("");
            pane.setStyle("-fx-background-color: transparent");
            pane.setOnMouseExited(mouseEvent -> {
                pane.setStyle("-fx-background-color: transparent");
            });
            dialog.close();
        });
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
        String duration = "For: " + measurement.getDuration() + " minutes";
        durationLabel.setText(duration);
        // TODO: check this
    }


    /**
     * Shows the watch options menu controlled by {@link WatchOptionsController}
     * @throws IOException Thrown by {@code FXMLLoader}
     */
    private void showOptions() throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("watchoptions.fxml"));
        Parent watchView = loader.load();
        Stage stage = new Stage();
        stage.setOnCloseRequest(e -> { //TODO: maybe change this
            watch.setWatchID(watchOptionsController.getWatchID());
            watch.setWatchName(watchOptionsController.getWatchName());
            dbManager.setWatchName(watchOptionsController.getWatchID(), watchOptionsController.getWatchName());

            System.out.println("Setting watch info: " + watch.getWatchID() + " " + watch.getWatchName());
        });
        stage.setTitle("Watch Options");
        stage.setScene(new Scene(watchView));
        stage.setResizable(false);
        watchOptionsController = loader.getController();
        watchOptionsController.setWatchData(watch.getWatchID(), watch.getWatchName());

        stage.show();
    }


    /**
     * Event for the options button
     * @throws IOException Thrown by {@link WatchViewController#showOptions()}
     */
    public void optionsPressed() throws IOException {
        showOptions();
    }


    /**
     * Event for disconnect button. Uses {@link DBManager#removeSmartwatch(int)} and {@link PrimaryController#removeWatch(int)}
     * to remove the watch
     */
    public void disconnectButtonPressed() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Disconnecting watch...");
        alert.setHeaderText("This will remove ALL data about the watch");
        alert.setContentText("Press OK to continue");

        Optional<ButtonType> result = alert.showAndWait();
        if(result.get() == ButtonType.OK){
            dbManager.removeSmartwatch(watch.getWatchID());
            primaryController.removeWatch(watch.getWatchID());
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

        readComments(selectedFile);

        setWatch(watch);
    }


    /**
     * Event for measurement stop button. Uses {@link DBManager#removeMeasurementFromWatch(int)} and {@link Smartwatch#setMeasurement(Measurement)}
     * to remove the measurement from the watch
     */
    public void stopPressed() {
        if(watch.getMeasurement() != null) {
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

    /**
     * Reads a csv file, parses and stores the data
     * @param file Specifies file to read from
     */
    void readComments(File file){
        try (BufferedReader br = new BufferedReader(new java.io.FileReader(file))) { // read in file
            String line;
            while ((line = br.readLine()) != null) { // go through all the lines
                String[] record = line.split(COMMA_DELIMITER);

                if (record.length < 3) {
                    System.out.println("Record error");
                    throw new ParseException("Length of records too small", 0);
                }

                try {
                    Date t1 = new SimpleDateFormat("kk:mm:ss").parse(record[0]);
                    Date t2 = new SimpleDateFormat("kk:mm:ss").parse(record[1]);
                    // todo: also require date?
                    String body = record[2];

                    Triplet<Date, Date, String> comment = new Triplet<>(t1, t2, body);
                    comments.add(comment);
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
     * Places the comments from {@link WatchViewController#comments} into {@link WatchViewController#commentsBox} to display them on the screen
     */
    void setComments() {
        for (int i = 0; i < comments.size(); i++) {
            Triplet<Date, Date, String> comment = comments.get(i);

            VBox vbox = new VBox();
            HBox hbox = new HBox();

            Date t1Date = comment.first();
            Date t2Date = comment.second();
            DateFormat timeFormat = new SimpleDateFormat("kk:mm:ss");
            String t1String = timeFormat.format(t1Date);
            String t2String = timeFormat.format(t2Date);
            Label t1 = new Label("\t" + t1String + " - ");
            Label t2 = new Label(t2String + "\t");
            Label body = new Label(comment.third());

            System.out.println("t1: " + t1String);
            System.out.println("t2: " + t2String);
            System.out.println("body: " + comment.third());

            hbox.getChildren().addAll(t1, t2, body);
            hbox.setAlignment(Pos.CENTER_LEFT);

            vbox.getChildren().addAll(hbox);
            vbox.setAlignment(Pos.CENTER);

            commentsBox.getChildren().add(vbox);
        }
    }
}

