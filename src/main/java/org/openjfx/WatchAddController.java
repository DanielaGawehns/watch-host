package org.openjfx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class WatchAddController {

    @FXML
    private Button buttonConnect;

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
        String watchName = textfieldName.getText(), watchIP = textfieldIP.getText();
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

        if(!watchName.isEmpty()){
            watch.setWatchName(watchName);
        }

        //TODO: parse IP field

        primaryController.addWatch(watch);
        Util.closeStage(buttonConnect);
    }

    public void connectButtonPressed() {
        parseInput();
    }
}
