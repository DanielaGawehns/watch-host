package org.openjfx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class WatchOptionsController {

    @FXML
    public TextField textfieldWatchID;

    @FXML
    public TextField textfieldWatchName;

    private int watchID;

    private String watchName;


    void setWatchData(int _watchID, String _watchName){
        watchID = _watchID;
        watchName = _watchName;

        textfieldWatchID.setText(watchID + "");
        textfieldWatchName.setText(watchName);
    }

    public int getWatchID() {
        return watchID;
    }

    public String getWatchName() {
        return watchName;
    }

    public void saveOptions(ActionEvent event) {
        watchID = Integer.parseInt(textfieldWatchID.getText());
        watchName = textfieldWatchName.getText();
    }
}
