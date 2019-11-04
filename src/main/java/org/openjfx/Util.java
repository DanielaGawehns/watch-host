package org.openjfx;

import javafx.scene.control.Alert;

public class Util {

    public static void printErrorDialog(String header, String body){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(header);
        alert.setContentText(body);
        alert.showAndWait();
    }
}
