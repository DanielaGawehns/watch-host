package org.openjfx.controllers;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openjfx.App;
import org.openjfx.CSVFileReader;
import org.openjfx.DBManager;
import org.openjfx.DataPoint;
import org.openjfx.SensorData;
import org.openjfx.Smartwatch;
import org.openjfx.SmartwatchList;
import org.openjfx.WatchData;

import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Separator;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import util.Util;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


/**
 * Class controlling the main screen
 * Controller of primary.fxml
 * Houses the main components of the program
 */
public class PrimaryController{

    /**
     * Vbox containing the list of connected watches
     */
    @FXML
    private VBox watchBar;

    /**
     * BorderPane resembling the main view. Should be filled by various fxml files
     */
    @FXML
    private BorderPane view;

    /**
     * List of smartwatches connected {@link Smartwatch}
     */
    private static SmartwatchList watches = null;

    /**
     * Reader for reading CSV files {@link CSVFileReader}
     */
    private final CSVFileReader reader = new CSVFileReader();

    /**
     * Controller for the watchView {@link WatchViewController}
     */
    private WatchViewController watchController;

    /**
     * Controller for measurement setup screen {@link MeasurementController}
     */
    private MeasurementController measurementController;

    /**
     * Controller for overview screen {@link OverviewController}
     */
    private OverviewController overviewController;

    /**
     * Controller for watch register screen {@link WatchAddController}
     */
    private WatchAddController watchAddController = new WatchAddController();

    /**
     * Which smartwatch is selected for charting
     */
    private int currentWatch;

    /**
     * Service for launching the about web page
     */
    private HostServices hostServices ;

    /**
     * Initializes the main view by printing the sidebar and overview
     * It also gets all the data stored in the database by using {@link DBManager#getAllWatches(LocalDateTime, LocalDateTime)}
     */
    public void initialize() {
        System.out.println("INITIALIZE Primary Controller");

        watches = App.getDbManager().getAllWatches(LocalDateTime.now().minusDays(Util.standardDaysBack), LocalDateTime.now());

        App.getConnectionManager().addConnectionConsumer(wrappedConnection -> {
            Logger.getGlobal().log(Level.INFO, "Got a new watch connection");
            try {
                var uidFut = wrappedConnection.getValues("system.uid");
                Logger.getGlobal().log(Level.INFO, "Asked watch for UID");

                uidFut.thenAccept(params -> {
                    var uid = params[0].asString().getValue();

                    Logger.getGlobal().log(Level.INFO, "Got watch ID: " + uid);

                    // try reusing watch
                    {
                        final var watch = watches.getWithID(uid);
                        if (watch != null) {
                            try {
                                watch.addConnection(wrappedConnection);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            return;
                        }
                    }

                    final var watch = new Smartwatch(new WatchData(uid), "", wrappedConnection);
                    Platform.runLater(() -> this.addWatch(watch));
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        currentWatch = -1;
        loadOverviewFXML();
        loadSideBar();
    }


    /**
     * Getter for {@link PrimaryController#view}
     */
    public BorderPane getView() { return view; }


    /**
     * Getter for {@link PrimaryController#watches}
     */
    static SmartwatchList getWatches() { return PrimaryController.watches; }


    /**
     * Adds a new {@link Smartwatch} to the {@link PrimaryController#watches} list. Then inserts that watch into the
     * database using {@link DBManager#insertWatch(Smartwatch)}
     * Also reloads the sideBar to show added watch
     * @param watch The watch to be added
     */
    void addWatch(Smartwatch watch){
        watches.add(watch);
        App.getDbManager().insertWatch(watch);
        loadSideBar();
    }


    /**
     * Removes a watch from {@link PrimaryController#watches} and reloads the sideBar to update
     * @param ID Watch ID
     */
    void removeWatch(String ID){
        watches.removeWithID(ID);
        loadSideBar();
    }


    /**
     * Checks if the id given by {@code ID} is not used by another watch
     * @param ID The ID to be checked
     * @return True if ID is not used. False if the ID is already in use
     */
    boolean idNotUsed(String ID){
        for (Smartwatch watch : watches) {
            if (watch.getWatchID().equals(ID)) {
                return false;
            }
        }
        return true;
    }


    /**
     * Loads the overview (overview.fxml) into the {@link PrimaryController#view}.
     * Also binds the width of the overview to the {@link PrimaryController#view}
     */
    private void loadOverviewFXML() {
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/overview.fxml")); // load fxml file
            BorderPane newPane = loader.load(); // load file into replacement pane
            overviewController = loader.getController();
            overviewController.setup(watches);

            newPane.prefWidthProperty().bind(view.widthProperty()); // bind width of newPane to the old one
            view.setCenter(newPane); // set newPane as center of borderPane
        }catch (IOException e){
            System.out.println(e.getMessage());
        }
    }


    /**
     * Loads the watch view (watchView.fxml) into the {@link PrimaryController#view}.
     * Also binds the width of the overview to the {@link PrimaryController#view} and sends the {@link Smartwatch} data to be displayed to the {@link WatchViewController}
     */
    void loadWatchFXML() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/watchview.fxml")); // load fxml file
            BorderPane newPane = loader.load(); // load file into replacement pane

            watchController = loader.getController(); // set controller to controller of new file
            System.out.println("Setting watch on place " + (currentWatch - 1));
            watchController.setWatch(watches.get(currentWatch - 1), this); // send Data of the watch being viewed to the controller

            newPane.prefWidthProperty().bind(view.widthProperty()); // bind width of newPane to the old one
            view.setCenter(newPane); // set newPane as center of borderPane
        }catch (IOException e){
            System.out.println(e.getMessage());
        }
        System.out.println("Switching to watchview for watch " + watches.get(currentWatch-1).getWatchID());
        System.out.println("- name: " + watches.get(currentWatch-1).getWatchName());
    }


    /**
     * Loads the measurement view (measurement.fxml) into the {@link PrimaryController#view}.
     * Also binds the width of the overview to the {@link PrimaryController#view} and sends the {@link PrimaryController} data to be used in the {@link MeasurementController}
     */
    private void loadMeasurementSetupFXML() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/measurement.fxml")); // load fxml file
            BorderPane newPane = loader.load(); // load file into replacement pane
            measurementController = loader.getController(); // set controller to controller of new file
            measurementController.loadSensors();
            measurementController.loadWatches();
            measurementController.loadTimesField();
            //newPane.prefWidthProperty().bind(view.widthProperty()); // bind width of newPane to the old one
            //view.setCenter(newPane); // set newPane as center of borderPane
            Stage stage = new Stage();
            stage.setTitle("Register watch");
            stage.setScene(new Scene(newPane));
            stage.setResizable(true);
            stage.show();
        }catch (IOException e){
            System.out.println(e.getMessage());
        }
    }


    /**
     * Moves to the watch view and sets {@link PrimaryController#currentWatch} appropriately
     * @param number The number of the watch to be displayed
     */
    private void watchlogoPressed(int number) {
        currentWatch = number; // set current watch
        loadWatchFXML();
    }


    /**
     * Event for the overview button. Runs {@link PrimaryController#loadOverviewFXML()}
     */
    public void switchToOverview() {
        loadOverviewFXML();
    }


    /**
     * Event for the measurement setup button. Runs {@link PrimaryController#loadMeasurementSetupFXML()}
     * Runs initializing functions for {@link MeasurementController}
     */
    public void switchToMeasurementSetup() {
        loadMeasurementSetupFXML();
    }


    /**
     * Finds all the files in the folder specified by {@code folder} and runs the {@link PrimaryController#reader} on them. Then saves the data to the database using {@link DBManager#insertDatalist(String, SensorData)}
     * @param folder The folder to crawl
     */
    private void syncFiles(File folder) {
        int success = 1;
        List<String> editedSensors;
        for(final File fileEntry : Objects.requireNonNull(folder.listFiles())){ // for all files in map 'folder'
            List<DataPoint> dataList = reader.readFile(fileEntry.getAbsolutePath()); // read sensorData
            System.out.println("Read list of size " + dataList.size());
            Smartwatch watch = watches.getWithID(reader.getWatchID());
            editedSensors = watch.addData(dataList); // add data stream to watch
            for(String sensor : editedSensors){
                success = App.getDbManager().insertDatalist(reader.getWatchID(), watches.getWithID(reader.getWatchID()).getSensorData(sensor));
                if(success != 0)
                    break;
            }
            if(success == 0 && fileEntry.delete()) { // delete the file
                System.out.println("File deleted!");
            }else{
                System.out.println("File deletion failed! Error in reading in file.");
            }
        }
    }


    /**
     * Event for the sync button. Runs {@link PrimaryController#syncFiles(File)}
     */
    public void syncButtonPressed() {
        System.out.println(System.getProperty("user.dir"));
        syncFiles(new File(System.getProperty("user.dir") + "/src/main/resources/input/test")); // read files in input folder
    }


    /**
     * Loads the sidebar in {@link PrimaryController#watchBar} which contains a list of connected watches from {@link PrimaryController#watches}
     */
    private void loadSideBar(){
        watchBar.getChildren().clear();
        for(int i = 0; i < watches.size(); i++){
            VBox vbox = new VBox();
            HBox hbox = new HBox();
            SplitMenuButton button = new SplitMenuButton();
            Image image = new Image(getClass().getResource("/images/smartwatch.png").toExternalForm());
            ImageView imageView = new ImageView(image);
            int batteryLevel = watches.get(i).getBatteryPercentage();
            int batteryType;
            int finalI = i + 1;

            button.setText("Watch " + watches.get(i).getWatchID());
            button.setGraphic(imageView);

            button.setOnAction((ActionEvent event) ->{ // If clicked
                watchlogoPressed(finalI);
            });

            MenuItem options = new MenuItem("Options...");

            int finalI1 = i;
            options.setOnAction((ActionEvent event) ->{
                System.out.println("Showing options for watch " + finalI1);
                showOptions(watches.get(finalI1));
            });

            MenuItem disconnect = new MenuItem("Disconnect");

            int finalI2 = i;
            disconnect.setOnAction((ActionEvent event)->{
                System.out.println("Disconnecting watch " + finalI2);
                Alert alert = Util.printChoiceBox("Disconnecting watch...",
                        "This will remove ALL data about the watch",
                        "Press OK to continue");

                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == ButtonType.OK) {

                    App.getDbManager().removeSmartwatch(watches.get(finalI2).getWatchID());
                    removeWatch(watches.get(finalI2).getWatchID());
                }
            });

            button.getItems().addAll(options, disconnect);

            if(batteryLevel < 20){
                batteryType = 1;
            }else if(batteryLevel < 60){
                batteryType = 2;
            }else{
                batteryType = 3;
            }

            Image imageBattery = new Image(getClass().getResource("/images/battery" + batteryType + ".png").toExternalForm());
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


    /**
     * Loads the watch add dialog for adding new watches.
     * Runs initializing functions for {@link PrimaryController#watchAddController}
     */
    private void loadWatchAdd() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/watchadd.fxml"));
            Parent watchView = loader.load();
            watchAddController = loader.getController();
            watchAddController.setPrimaryController(this);

            Stage stage = new Stage();
            stage.setTitle("Register watch");
            stage.setScene(new Scene(watchView));
            stage.setResizable(false);
            stage.show();
        }
        catch (IOException e){
            System.err.println(e.getMessage());
        }
    }


    /**
     * Event for add watch button. Runs {@link PrimaryController#loadWatchAdd()}
     */
    public void drawWatchAddScreen() {
        loadWatchAdd();
    }


    /**
     * Shows the watch options menu controlled by {@link WatchOptionsController}
     */
    void showOptions(Smartwatch smartwatch) {
       try {
           FXMLLoader loader = new FXMLLoader(getClass().getResource("/watchoptions.fxml"));
           Parent watchView = loader.load();
           WatchOptionsController watchOptionsController = loader.getController();
           Stage stage = new Stage();

           watchOptionsController.setWatchData(smartwatch);

           stage.setTitle("Watch Options");
           stage.setScene(new Scene(watchView));
           stage.setResizable(false);
           stage.show();

           stage.setOnHiding(e -> {
               System.out.println("CLOSED STAGE!");
               if(currentWatch > 0 && currentWatch < watches.size()){
                   loadWatchFXML();
               }

           });
       }catch (IOException e){
           e.printStackTrace();
       }
    }


    /**
     * Event for the wiki button in the Menubar
     */
    public void menuWikiPressed() {
        hostServices.showDocument("https://git.liacs.nl/softwareengineering_wearables/watch-host");
    }


    /**
     * Event for the Watch Add button in Menubar
     */
    public void menuAddPressed() {
        loadWatchAdd();
    }


    /**
     * Setter for {@link PrimaryController#hostServices}
     */
    public void setHostServices(HostServices hostServices) { this.hostServices = hostServices ; }

    /**
     * Closes all watch connections
     *
     * @throws IOException IO error when failing to close the watch connection.
     */
    public static void shutdown() throws IOException {
        for (Smartwatch watch : PrimaryController.watches) {
            watch.close();
        }
    }
}
