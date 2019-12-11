package org.openjfx.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.openjfx.App;
import org.openjfx.DBManager;
import org.openjfx.Smartwatch;
import util.Util;


/**
 * Class for controlling the watch options screen
 * Controller of watchoptions.fxml
 */
public class WatchOptionsController {

    /**
     * Field for filling the watch ID
     */
    @FXML
    private Label labelWatchID;

    /**
     * Field for filling the watch name
     */
    @FXML
    private TextField textfieldWatchName;


    /**
     * The watch of which we are changing options
     */
    private Smartwatch watch;


    /**
     * Constructor
     */
    void setWatchData(Smartwatch _watch){
        watch = _watch;

        labelWatchID.setText(watch.getWatchID() + "");
        textfieldWatchName.setText(watch.getWatchName());
    }


    // TODO sanitation
    /**
     * Saves the values inserted into the Textfields using {@link Smartwatch#setWatchName(String)} and {@link DBManager#setWatchName(int, String)}
     */
    public void saveOptions() {

        if(!textfieldWatchName.getText().equals(watch.getWatchName())){
            watch.setWatchName(textfieldWatchName.getText());
            App.getDbManager().setWatchName(watch.getWatchID(), watch.getWatchName());
        }
        Util.closeStage(textfieldWatchName);
    }
}
