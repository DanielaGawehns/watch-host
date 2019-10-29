package org.openjfx;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;

import java.util.List;

// Class for controlling functions from the watchView screen
// Controlling watchview.fxml
public class WatchViewController {

    // Root BorderPane
    @FXML
    private BorderPane view;

    @FXML
    private GridPane grid;

    @FXML
    private ProgressBar storageBar;

    @FXML
    private Label storageLabel;

    @FXML
    private Label batteryLevelLabel;

    @FXML
    private Label batteryRemainingLabel;

    @FXML
    private Label watchNameLabel;

    @FXML
    private Label watchNrLabel;

    @FXML
    private LineChart<String, Number> sensorChart;

    // The watch of which the overview is showed
    private Smartwatch watch;

    private WatchOptionsController watchOptionsController;

    // set the watch that is shown
    void setWatch(Smartwatch _watch){
        System.out.println("Setting watch...");
        watch = _watch;

        grid.prefWidthProperty().bind(view.widthProperty()); // bind width of grid to the width of the borderPane

        setInfo();

        try {
            fillChart(sensorChart, "HRM"); // fill Chart TODO: change parameter
        }catch (Exception e){ // if data is not found
            System.out.println("No data found for watch: " + " and sensor: TEMP");
            sensorChart.setDisable(true);
            return;
        }
        System.out.println("Done filling chart");
    }

    // fill chart with data from sensor
    // TODO: Make this work with all types of charts
    private void fillChart(LineChart<String, Number> chart, String sensor) {
        XYChart.Series<String, Number> series = new XYChart.Series<>(); // new series for adding data points
        System.out.println("Fill Chart for watch: " + watch.getWatchID());
        SensorData sensorData = watch.getSensorData(sensor); // get data of the right sensor

        chart.setAnimated(false); // disable animation for clearing
        chart.getData().clear();
        chart.setAnimated(true);
        chart.setDisable(false); // turn on chart

        series.setName(sensorData.getSensor()); // set title of line for legend
        chart.getData().add(series); // add series to chart
        chart.setTitle(sensorData.getSensor()); // set title of chart

        System.out.println("Data size is " + sensorData.size());

        for(int i = 0; i < sensorData.size(); i++){
            XYChart.Data<String, Number> temp = sensorData.getDataPoint(i); // get dataPoint no. i
            temp.setNode(createDataNode());
            System.out.println("Adding: " + temp.toString());
            series.getData().add(temp); // add datapoint to series
        }
    }


    // Node that is clickable
    // Prints pin above the node
    // TODO: save these pins for exporting
    private static Node createDataNode(/*XYChart.Data<Number, Number> data*/) {
        //Node node = data.getNode();
        var label = new Label();

        var pane = new Pane(label);
        pane.setShape(new Circle(4.0));
        pane.setScaleShape(false);
        pane.setStyle("-fx-background-color: CHART_COLOR_1");

        pane.setOnMouseEntered(mouseEvent -> {
            pane.setStyle("-fx-background-color: grey");
        });

        pane.setOnMouseExited(mouseEvent -> {
            pane.setStyle("-fx-background-color: CHART_COLOR_1");
        });

        pane.setOnMouseClicked(mouseEvent -> {
            pinWindow(label);


        });
        System.out.println("label pos: " + pane.getBoundsInParent().toString());
        label.setLayoutY(-10);
        System.out.println("Created pin");
        return pane;
    }


    private static void pinWindow(Label label){
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
            dialog.close();
        });

        buttonRemove.setOnAction(e -> {
            label.setText("");
            dialog.close();
        });
    }


    // Sets all info fields
    private void setInfo(){
        setStorageInfo();
        setBatteryInfo();
        setWatchInfo();
        setMeasurementInfo();
    }


    // Sets storage information items in watchInfo section
    private void setStorageInfo(){
        float maxStorage = watch.getWatchData().getMaxStorage();
        float usedStorage = watch.getWatchData().getUsedStorage();
        String labelText = usedStorage + "/" + maxStorage + " MB used";

        storageBar.setProgress(usedStorage / maxStorage);
        storageLabel.setText(labelText);
    }


    // Sets battery information items in watchInfo section
    private void setBatteryInfo(){
        batteryLevelLabel.setText(watch.getBatteryPercentage() + "%");
        //TODO: add calculation for remaining time
    }


    // Sets watch information items in watchInfo section
    private void setWatchInfo(){
        watchNameLabel.setText(watch.getWatchName());
        watchNrLabel.setText(watch.getWatchID() + "");
    }


    // Sets measurement information in measurementInfo section
    private void setMeasurementInfo(){
        // TODO: Fill this
    }

    private void showOptions() throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("watchoptions.fxml"));
        Parent watchView = (Parent) loader.load();
        Stage stage = new Stage();
        stage.setOnCloseRequest(e -> {
            watch.setWatchID(watchOptionsController.getWatchID());
            watch.setWatchName(watchOptionsController.getWatchName());
            System.out.println("Setting watch info: " + watch.getWatchID() + " " + watch.getWatchName());
        });
        stage.setTitle("Watch Options");
        stage.setScene(new Scene(watchView));
        stage.setResizable(false);
        watchOptionsController = loader.getController();
        watchOptionsController.setWatchData(watch.getWatchID(), watch.getWatchName());



        stage.show();



    }

    public void optionsPressed(ActionEvent event) throws IOException {
        showOptions();
    }
}

