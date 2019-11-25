package org.openjfx.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;


/**
 * Class for controlling the watch options screen
 * Controller of watchoptions.fxml
 */
public class WatchOptionsController {

    /**
     * Field for filling the watch ID
     */
    @FXML
    public TextField textfieldWatchID;

    /**
     * Field for filling the watch name
     */
    @FXML
    public TextField textfieldWatchName;

    /**
     * ID of the watch
     */
    private int watchID;

    /**
     * Name of the watch
     */
    private String watchName;


    /**
     * Constructor
     */
    void setWatchData(int _watchID, String _watchName){
        watchID = _watchID;
        watchName = _watchName;

        textfieldWatchID.setText(watchID + "");
        textfieldWatchName.setText(watchName);
    }


    /**
     * Getter for {@link WatchOptionsController#watchID}
     */
    public int getWatchID() {
        return watchID;
    }


    /**
     * Getter for {@link WatchOptionsController#watchName}
     */
    public String getWatchName() {
        return watchName;
    }


    // TODO sanitation
    /**
     * Saves the values inserted into the Textfields
     */
    public void saveOptions() {
        watchID = Integer.parseInt(textfieldWatchID.getText());
        watchName = textfieldWatchName.getText();
        //Util.closeStage(textfieldWatchID);
    }
}
