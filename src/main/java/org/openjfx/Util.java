package org.openjfx;

import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.util.List;

class Util {

    static void printErrorDialog(String header, String body){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(header);
        alert.setContentText(body);
        alert.showAndWait();
    }

    static void closeStage(Node node){
        // get a handle to the stage
        Stage stage = (Stage) node.getScene().getWindow();
        // do what you have to do
        stage.close();
    }

    // Add two double lists together by adding the items
    // If the lists are not the same length return
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

    // Divides all values in the list by 'division'
    static void divideDoubleList(List<Double> list, Double division){
        for(int i = 0; i < list.size(); i++){
            list.set(i, round(list.get(i) / division, 1));
        }
    }

    // Rounds 'value' to 'places' decimals
    static double round(double value, int places) {
        double scale = Math.pow(10, places);
        return Math.round(value * scale) / scale;
    }
}
