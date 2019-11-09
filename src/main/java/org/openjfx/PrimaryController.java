package org.openjfx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
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
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


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

    private List<String> allSensors = new ArrayList<>() {
        {
            add("HRM");
        }
    };

    /**
     * List of smartwatches connected {@link Smartwatch}
     */
    private static SmartwatchList watches = new SmartwatchList();

    /**
     * Reader for reading CSV files {@link CSVFileReader}
     */
    private CSVFileReader reader = new CSVFileReader();

    /**
     * Manager for managing the Database connection {@link DBManager}
     */
    private static DBManager dbManager = new DBManager();

    /**
     * Controller for the watchView {@link WatchViewController}
     */
    private WatchViewController watchController;

    /**
     * Controller for measurement setup screen {@link MeasurementController}
     */
    private MeasurementController measurementController;

    /**
     * Controller for watch register screen {@link WatchAddController}
     */
    private WatchAddController watchAddController;

    /**
     * Which smartwatch is selected for charting
     */
    private int currentWatch;


    /**
     * Initializes the main view by printing the sidebar and overview
     * @throws IOException Thrown by {@link PrimaryController#loadOverviewFXML()}
     */
    public void initialize() throws IOException {
        System.out.println("INITIALIZE Primary Controller");

        List<Integer> watchIDList = dbManager.getAllWatchId();

        for(Integer ID : watchIDList){
            WatchData data = new WatchData(ID, 69, 8000, 6969);
            String name = dbManager.getWatchName(ID);
            watches.add(new Smartwatch(data, name));
        }

        currentWatch = -1;
        loadOverviewFXML();
        loadSideBar();
        //syncButtonPressed();
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
     * Adds a new {@link Smartwatch} to the {@link PrimaryController#watches} list
     * Also reloads the sideBar to show added watch
     * @param watch The watch to be added
     */
    void addWatch(Smartwatch watch){
        watches.add(watch);
        dbManager.insertWatch(watch.getWatchID());
        for(String sensor : allSensors){
            dbManager.insertSensor(watch.getWatchID(), sensor);
        }
        loadSideBar();
    }


    /**
     * Checks if the id given by {@code ID} is not used by another watch
     * @param ID The ID to be checked
     * @return True if ID is not used. False if the ID is already in use
     */
    boolean idNotUsed(int ID){
        for (int i = 0; i < watches.size(); i++) {
            if (watches.get(i).getWatchID() == ID) {
                return false;
            }
        }
        return true;
    }


    /**
     * Loads the overview (overfiew.fxml) into the {@link PrimaryController#view}.
     * Also binds the width of the overview to the {@link PrimaryController#view}
     * @throws IOException Thrown by {@code FXMLLoader}
     */
    private void loadOverviewFXML() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("overview.fxml")); // load fxml file
        BorderPane newPane = loader.load(); // load file into replacement pane

        newPane.prefWidthProperty().bind(view.widthProperty()); // bind width of newPane to the old one
        view.setCenter(newPane); // set newPane as center of borderPane
    }


    /**
     * Loads the watch view (watchView.fxml) into the {@link PrimaryController#view}.
     * Also binds the width of the overview to the {@link PrimaryController#view} and sends the {@link Smartwatch} data to be displayed to the {@link WatchViewController}
     * @throws IOException Thrown by {@code FXMLLoader}
     */
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


    /**
     * Loads the measurement view (measurement.fxml) into the {@link PrimaryController#view}.
     * Also binds the width of the overview to the {@link PrimaryController#view} and sends the {@link PrimaryController} data to be used in the {@link MeasurementController}
     * @throws IOException Thrown by {@code FXMLLoader}
     */
    private void loadMeasurementSetupFXML() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("measurement.fxml")); // load fxml file
        BorderPane newPane = loader.load(); // load file into replacement pane
        measurementController = loader.getController(); // set controller to controller of new file
        measurementController.setPrimaryController(this); // pass current Primary class to measurementController

        newPane.prefWidthProperty().bind(view.widthProperty()); // bind width of newPane to the old one
        view.setCenter(newPane); // set newPane as center of borderPane
    }


    /**
     * Moves to the watch view and sets {@link PrimaryController#currentWatch} appropriately
     * @param number The number of the watch to be displayed
     * @throws IOException Thrown by {@link PrimaryController#loadWatchFXML()}
     */
    private void watchlogoPressed(int number) throws IOException {
        currentWatch = number; // set current watch
        loadWatchFXML();
    }

    // Switch to overview tab

    /**
     * Event for the overview button. Runs {@link PrimaryController#loadOverviewFXML()}
     * @throws IOException Thrown by {@link PrimaryController#loadOverviewFXML()}
     */
    public void switchToOverview() throws IOException {
        loadOverviewFXML();
    }


    /**
     * Event for the measurement setup button. Runs {@link PrimaryController#loadMeasurementSetupFXML()}
     * Runs initializing functions for {@link MeasurementController}
     * @throws IOException Thrown by {@link PrimaryController#loadOverviewFXML()}
     */
    public void switchToMeasurementSetup() throws IOException {
        loadMeasurementSetupFXML();
        measurementController.loadSensors();
        measurementController.loadWatches();
        measurementController.loadDurationField();
    }


    /**
     * Finds all the files in the folder specified by {@code folder} and runs the {@link PrimaryController#reader} on them
     * @param folder The folder to crawl
     */
    private void syncFiles(File folder) {
        for(final File fileEntry : Objects.requireNonNull(folder.listFiles())){ // for all folders in map 'folder'
            List<DataPoint> dataList = reader.readFile(fileEntry.getAbsolutePath()); // read sensorData
            System.out.println("Read list of size " + dataList.size());
            watches.getFromID(reader.getWatchNumber()).addData(dataList); // add data stream to watch
            for(String sensor : allSensors){
                dbManager.insertDatalist(reader.getWatchNumber(), watches.getFromID(reader.getWatchNumber()).getSensorData(sensor));
            }
        }
    }


    /**
     * Event for the sync button. Runs {@link PrimaryController#syncFiles(File)}
     */
    public void syncButtonPressed() {
        syncFiles(new File(System.getProperty("user.dir") + "/src/main/resources/input/test")); // read files in input folder
        /*if(currentWatch > 0){
            watchController.setWatch(watches.get(currentWatch-1)); // set watch to last accessed watch
        }*/
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


    /**
     * Loads the watch add dialog for adding new watches.
     * Runs initializing functions for {@link PrimaryController#watchAddController}
     * @throws IOException Thrown by {@code FXMLLoader}
     */
    private void loadWatchAdd() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("watchadd.fxml"));
        Parent watchView = loader.load();

        watchAddController = loader.getController();
        watchAddController.setPrimaryController(this);

        Stage stage = new Stage();

        stage.setTitle("Register watch");
        stage.setScene(new Scene(watchView));
        stage.setResizable(false);

        stage.show();
    }


    /**
     * Event for add watch button. Runs {@link PrimaryController#loadWatchAdd()}
     * @throws IOException
     */
    public void drawWatchAddScreen() throws IOException {
        loadWatchAdd();
    }
}
