package org.openjfx;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
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
    private Label labelWatchID;;

    /**
     * Field for filling the watch name
     */
    @FXML
    private TextField textfieldWatchName;


    /**
     * The watch of which we are changing options
     */
    private Smartwatch watch;

    private DBManager dbManager = new DBManager();


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
     * Saves the values inserted into the Textfields
     */
    public void saveOptions() {

        if(!textfieldWatchName.getText().equals(watch.getWatchName())){
            watch.setWatchName(textfieldWatchName.getText());
            dbManager.setWatchName(watch.getWatchID(), watch.getWatchName());
        }
        Util.closeStage(textfieldWatchName);
    }
}
