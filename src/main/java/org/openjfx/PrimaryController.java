package org.openjfx;

import javafx.event.ActionEvent;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


// Controller of main screen
// Controller of primary.fxml
public class PrimaryController{

    @FXML
    private VBox watchBar;

    @FXML
    private BorderPane view;
    public BorderPane getView() { return view; }

    // List of smartwatches connected
    private static List<Smartwatch> watches = new ArrayList<>();

    // Reader for reading CSV files
    private CSVFileReader reader = new CSVFileReader();

    // Controller for the watchView
    private WatchViewController watchController;

    private MeasurementController measurementController;

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
        WatchData data1 = new WatchData(1, 45, 8000, 4123);
        WatchData data2 = new WatchData(2, 12, 8000, 6452);
        WatchData data3 = new WatchData(3, 89, 8000, 1235);
        SubjectData subjectData1 = new SubjectData(1);
        SubjectData subjectData2 = new SubjectData(2);
        SubjectData subjectData3 = new SubjectData(3);


        watches.add(new Smartwatch(data1, subjectData1)); // TEMP: add watch 1
        watches.add(new Smartwatch(data2, subjectData2)); // TEMP: add watch 2
        watches.add(new Smartwatch(data3, subjectData3)); // TEMP: add watch 3

        currentWatch = -1;
        loadOverviewFXML();
        loadSideBar();
    }

    // Loading overview FXML into view
    private void loadOverviewFXML() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("overview.fxml")); // load fxml file
        BorderPane newPane = loader.load(); // load file into replacement pane

        newPane.prefWidthProperty().bind(view.widthProperty()); // bind width of newPane to the old one
        view.setCenter(newPane); // set newPane as center of borderPane
    }


    // Loading watchview FXML into view
    private void loadWatchFXML() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("watchview.fxml")); // load fxml file
        BorderPane newPane = loader.load(); // load file into replacement pane

        watchController = loader.getController(); // set controller to controller of new file
        watchController.setWatch(watches.get(currentWatch-1)); // send Data of the watch being viewed to the controller

        newPane.prefWidthProperty().bind(view.widthProperty()); // bind width of newPane to the old one
        view.setCenter(newPane); // set newPane as center of borderPane
        System.out.println("Switching to watchview for watch " + watches.get(currentWatch-1).getWatchID());
        System.out.println("- name: " + watches.get(currentWatch-1).getWatchName());
    }

    // Loading overview FXML into view
    private void loadMeasurementSetupFXML() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("measurement.fxml")); // load fxml file
        BorderPane newPane = loader.load(); // load file into replacement pane
        measurementController = loader.getController();
        measurementController.setPrimaryController(this);

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

    // switch to measurement setup tab
    public void switchToMeasurementSetup(ActionEvent actionEvent) throws IOException {
        loadMeasurementSetupFXML();
        measurementController.loadSensors();
        measurementController.loadWatches();
        measurementController.loadDurationField();
    }

    // Go to input dir and read all files
    private void syncFiles(File folder) {

        for(final File fileEntry : Objects.requireNonNull(folder.listFiles())){ // for all folders in map 'folder'
            List<DataPoint> dataList = reader.readFile(fileEntry.getAbsolutePath()); // read sensorData
            watches.get(reader.getWatchNumber()).addData(dataList); // add data stream to watch
        }


       // watches.get(reader.getWatchNumber()).getSensorData("HRM").mergeDuplicates();
        watches.get(reader.getWatchNumber()).getSensorData("HRM").printRecords();
    }


    // Event for syncButton
    public void syncButtonPressed(ActionEvent actionEvent) {
        syncFiles(new File(System.getProperty("user.dir") + "/src/main/resources/input/test")); // read files in input folder
        if(currentWatch > 0){
            watchController.setWatch(watches.get(currentWatch-1)); // set watch to last accessed watch
        }
    }


    // Loads watch buttons into sidebar
    private void loadSideBar(){
        for(int i = 0; i < watches.size(); i++){
            VBox vbox = new VBox();
            HBox hbox = new HBox();
            SplitMenuButton button = new SplitMenuButton();
            Image image = new Image("/images/smartwatch.png");
            ImageView imageView = new ImageView(image);
            int batteryLevel = watches.get(i).getBatteryPercentage();
            int batteryType;
            int finalI = i + 1;

            button.setText("Watch " + watches.get(i).getWatchID());
            button.setGraphic(imageView);

            button.setOnAction((ActionEvent event) ->{ // If clicked
                try {
                    watchlogoPressed(finalI);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });

            button.getItems().addAll(new MenuItem("Options..."), new MenuItem("Disconnect"));

            /*
            MenuItem select = new MenuItem("Select");
            MenuItem options = new MenuItem("Options...");
            MenuItem disconnect = new MenuItem("Disconnect");
            button.getItems().addAll(select, options, disconnect);

            select.setOnAction((e)-> {
                watchController.selectWatch(finalI);
                System.out.println("Choice 1 selected");
            });
            options.setOnAction((e)-> {
                System.out.println("Choice 2 selected");
            });
            disconnect.setOnAction((e)-> {
                System.out.println("Choice 3 selected");
            });
*/
            if(batteryLevel < 20){
                batteryType = 1;
            }else if(batteryLevel < 60){
                batteryType = 2;
            }else{
                batteryType = 3;
            }

            Image imageBattery = new Image("/images/battery" + batteryType + ".png");
            ImageView imageViewBattery = new ImageView(imageBattery);

            Label label = new Label(batteryLevel + "%");
            Separator sep = new Separator();

            hbox.getChildren().addAll(button, imageViewBattery, label);
            hbox.setAlignment(Pos.CENTER);
            hbox.setSpacing(5);

            vbox.getChildren().addAll(hbox, sep);
            vbox.setAlignment(Pos.CENTER);

            watchBar.getChildren().add(vbox);
        }
    }

    // Draws new window for adding smartwatch
    // TODO: develop this
    private void drawRegisterWatchScreen(){
        final Stage dialog = new Stage();
        VBox dialogVbox = new VBox(20);
        dialogVbox.setAlignment(Pos.CENTER);
        dialogVbox.getChildren().add(new Text("Register your watch here"));
        Scene dialogScene = new Scene(dialogVbox, 300, 200);
        dialog.setScene(dialogScene);
        dialog.show();
    }


    // Event for add watch button
    public void drawNewWatchScreen(ActionEvent actionEvent) {
        drawRegisterWatchScreen();
    }

    // return the list of currently connected watches
    public static List<Smartwatch> getWatches() { return PrimaryController.watches; }
}
