package org.openjfx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class WatchAddController {

    @FXML
    private TextField textfieldID;

    @FXML
    private TextField textfieldName;

    @FXML
    private TextField textfieldIP;

    // Instance of PrimaryController
    private PrimaryController primaryController;

    // Set the primaryController to return back to the overview tab after starting a measurement
    void setPrimaryController(PrimaryController controller) { primaryController = controller; }


    private void parseInput(){
        int watchID;
        WatchData watchData;
        Smartwatch watch;

        try{
           watchID = Integer.parseInt(textfieldID.getText());
           if(watchID < 0){
               throw new NumberFormatException();
           }
        }catch (NumberFormatException e){
            Util.printErrorDialog("Watch ID: " + textfieldID.getText() + " is not valid!", "Please choose another ID to continue.");
            return;
        }

        if(primaryController.idNotUsed(watchID)){
            watchData = new WatchData(watchID);
            watch = new Smartwatch(watchData);

        }else{
            Util.printErrorDialog("Watch ID: " + watchID + " is already in use!", "Please choose another ID to continue.");
            return;
        }
        primaryController.addWatch(watch);
    }

    public void connectButtonPressed(ActionEvent event) {
        parseInput();
    }
}
