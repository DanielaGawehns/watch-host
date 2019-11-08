package org.openjfx;

import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import java.util.List;

/**
 * Utility class holding generic functions
 */
class Util {

    /**
     * Prints an error dialog that pops up on the screen
     * @param header String containing the header message
     * @param body String containing the body message
     */
    static void printErrorDialog(String header, String body){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(header);
        alert.setContentText(body);
        alert.showAndWait();
    }


    /**
     * Closes a stage
     * @param node The node of which the stage should be closed
     */
    static void closeStage(Node node){
        // get a handle to the stage
        Stage stage = (Stage) node.getScene().getWindow();
        // do what you have to do
        stage.close();
    }


    /**
     * Add two double lists together by adding the items if they are of the same length
     * @param list1 A list of {@code Double}
     * @param list2 A list of {@code Double}
     */
    static void addDoubleLists(List<Double> list1, List<Double> list2){
        if(list1.size() != list2.size()){
            System.out.println("Lists not the same size"); // TODO: make exception
            return;
        }

        double value;

        for(int i = 0; i < list1.size(); i++){
            value = list1.get(i) + list2.get(i);
            list1.set(i, value);
        }
    }


    /**
     * Divides all values in a list by a {@code Double}
     * @param list The list of {@code Double} to be divided
     * @param division The amount the list should be divided by
     */
    static void divideDoubleList(List<Double> list, Double division){
        for(int i = 0; i < list.size(); i++){
            list.set(i, round(list.get(i) / division, 1));
        }
    }


    /**
     * Rounds a double to an amount of decimal places
     * @param value The value to be rounded
     * @param places The amount of decimals to be rounded to. Should >= 0
     * @return The value with the right amount of decimal places
     */
    static double round(double value, int places) {
        double scale = Math.pow(10, places);
        return Math.round(value * scale) / scale;
    }
}
