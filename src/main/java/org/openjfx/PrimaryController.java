package org.openjfx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

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
        WatchData data1 = new WatchData(1, 45, 8000, 4123);
        WatchData data2 = new WatchData(2, 12, 8000, 6452);
        WatchData data3 = new WatchData(3, 89, 8000, 1235);
        watches.add(new Smartwatch(data1)); // TEMP: add watch 1
        watches.add(new Smartwatch(data2)); // TEMP: add watch 2
        watches.add(new Smartwatch(data3)); // TEMP: add watch 3

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
        if(currentWatch > 0){
            watchController.setWatch(watches.get(currentWatch-1)); // set watch to last accessed watch
        }
    }


    // Loads watch buttons into sidebar
    private void loadSideBar(){
        for(int i = 0; i < watches.size(); i++){
            VBox vbox = new VBox();
            HBox hbox = new HBox();
            Button button = new Button("Watch " + i);
            Image image = new Image("\\images\\smartwatch.png");
            ImageView imageView = new ImageView(image);
            int batteryLevel = watches.get(i).getBatteryPercentage();
            int batteryType;
            int finalI = i + 1;

            button.setGraphic(imageView);

            button.setOnAction((ActionEvent event) ->{ // If clicked
                try {
                    watchlogoPressed(finalI);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });

            if(batteryLevel < 20){
                batteryType = 1;
            }else if(batteryLevel < 60){
                batteryType = 2;
            }else{
                batteryType = 3;
            }

            Image imageBattery = new Image("\\images\\battery" + batteryType + ".png");
            ImageView imageViewBattery = new ImageView(imageBattery);

            Label label = new Label(batteryLevel + "%");
            Separator sep = new Separator();

            hbox.getChildren().addAll(button, imageViewBattery, label);
            hbox.setAlignment(Pos.CENTER);

            vbox.getChildren().addAll(hbox, sep);
            vbox.setAlignment(Pos.CENTER);

            watchBar.getChildren().add(vbox);
        }
    }
}
