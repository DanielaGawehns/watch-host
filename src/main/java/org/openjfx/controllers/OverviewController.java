package org.openjfx.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import org.openjfx.Smartwatch;


import javafx.stage.FileChooser;

import javafx.stage.Stage;
import org.openjfx.CSVWriter;

import java.io.File;

// Class for controlling function for the overview screen
// Controller for overview.fxml

/**
 * Class for controlling function for the overview screen
 * Controller for overview.fxml
 */
public class OverviewController {


    /**
     * The {@link Smartwatch} of which the overview is showed
     */
    private Smartwatch watch;

    /**
     * Controller of {@link PrimaryController}
     */
    //private PrimaryController primaryController;

    /**
     * Event for Export from all watches button.
     */
    public void exportButton() {
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