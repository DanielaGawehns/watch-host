package org.openjfx.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import org.openjfx.Smartwatch;
import org.openjfx.WatchData;
import util.IpValidator;
import util.Util;


//TODO may need extending
/**
 * Class for controlling the watch add screen
 * Controller of watchadd.fxml
 */
public class WatchAddController {

    /**
     * Connect button
     */
    @FXML
    private Button buttonConnect;

    /**
     * Field for filling the ID option
     */
    @FXML
    private TextField textfieldID;

    /**
     * Field for filling the Name option
     */
    @FXML
    private TextField textfieldName;

    /**
     * Field for filling the IP option
     */
    @FXML
    private TextField textfieldIP;
    
    /**
     * Instance of PrimaryController
     */
    private PrimaryController primaryController;


    /**
     * Validator for Ip addresses
     */
    private IpValidator validator = new IpValidator();
    

    /**
     * Setter of {@link WatchAddController#primaryController}
     */
    void setPrimaryController(PrimaryController controller) { primaryController = controller; }


    // TODO do something with the IP field
    /**
     * Parses the input from the Text fields and adds an new watch using {@link PrimaryController#addWatch(Smartwatch)}
     */
    private void parseInput(){
        int watchID;
        String watchName = textfieldName.getText(), watchIP = textfieldIP.getText();
        WatchData watchData;
        Smartwatch watch;

        // Parse ID field
        try{
           watchID = Integer.parseInt(textfieldID.getText());
           if(watchID < 0){
               throw new NumberFormatException();
           }
        }catch (NumberFormatException e){
            Util.printErrorDialog("Watch ID: " + textfieldID.getText() + " is not valid!", "Please choose another ID to continue.");
            return;
        }

        // Check the ID
        if(!primaryController.idNotUsed(watchID)){
            Util.printErrorDialog("Watch ID: " + watchID + " is already in use!", "Please choose another ID to continue.");
            return;
        }

        watchData = new WatchData(watchID);

        //TODO: parse IP field
        if(!validator.isValid(watchIP)){
            Util.printErrorDialog("Watch IP: " + watchIP + " is not valid!", "Please choose another IP to continue.");
            return;
        }
        watchData.setIpAdress(watchIP);
        watch = new Smartwatch(watchData, watchName);

        // Add the watch
        primaryController.addWatch(watch);
        Util.closeStage(buttonConnect);
    }


    /**
     * Even for the connect button
     */
    public void connectButtonPressed() {
        parseInput();
    }
}
