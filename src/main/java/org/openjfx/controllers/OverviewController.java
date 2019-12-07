package org.openjfx.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.openjfx.CSVWriter;

import java.io.File;


/**
 * Class for controlling function for the overview screen
 * Controller for overview.fxml
 */
public class OverviewController {

    /**
     * Label for count of connected watches
     */
    @FXML
    private Label labelWatchConnected;


    /**
     * Label for count of active measurements
     */
    @FXML
    private Label labelActiveMeasurements;


    /**
     * Sets {@link OverviewController#labelWatchConnected} and {@link OverviewController#labelActiveMeasurements}
     * @param watches value to set {@link OverviewController#labelWatchConnected}
     * @param measurements value to set {@link OverviewController#labelActiveMeasurements}
     */
    void setLabels(int watches, int measurements){
        labelWatchConnected.setText(watches + "");
        labelActiveMeasurements.setText(measurements + "");
    }


    /**
     * Event for Export from all watches button.
     */
    @FXML
    private void exportButton() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save file as");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("CSV Files", "*.csv"),
                new FileChooser.ExtensionFilter("All Files", "*.*"));
        Stage stage = new Stage();
        File selectedFile = fileChooser.showSaveDialog(stage);
        if (selectedFile != null) {
            var writer = new CSVWriter(selectedFile);
            writer.WriteAll();
        }
    }


}