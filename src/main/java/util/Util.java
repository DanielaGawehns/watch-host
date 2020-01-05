package util;

import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.openjfx.controllers.WatchViewController;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility class holding generic functions
 */
public class Util {

    /**
     * Constant value for default start date for the {@link org.openjfx.controllers.WatchViewController} and
     * {@link org.openjfx.controllers.OverviewController} charts
     */
    public static final long standardDaysBack = 365; // TODO: set to something sensible

    /**
     * Prints an error dialog that pops up on the screen
     * @param header String containing the header message
     * @param body String containing the body message
     */
    public static void printErrorDialog(String header, String body){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(header);
        alert.setContentText(body);
        alert.showAndWait();
    }

    /**
     * Creates a choice box window
     * @param title Title of the window
     * @param header Header of the box
     * @param body Body of the box
     * @return Alert window to be printed
     */
    public static Alert printChoiceBox(String title, String header, String body){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(body);
        return alert;
    }


    /**
     * Closes a stage
     * @param node The node of which the stage should be closed
     */
    public static void closeStage(Node node){
        Stage stage = (Stage) node.getScene().getWindow();
        stage.close();
    }


    /**
     * Add two double lists together by adding the items if they are of the same length
     * @param list1 A list of {@code Double}
     * @param list2 A list of {@code Double}
     */
    public static void addDoubleLists(List<Double> list1, List<Double> list2){
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
    public static void divideDoubleList(List<Double> list, Double division){
        for(int i = 0; i < list.size(); i++){
            list.set(i, round(list.get(i) / division, 1));
        }
    }


    /**
     * Rounds a double to an amount of decimal places
     * @param value The value to be rounded
     * @param places The amount of decimals to be rounded to. Should be equal to or greater than 0
     * @return The value with the right amount of decimal places
     */
    public static double round(double value, int places) {
        double scale = Math.pow(10, places);
        return Math.round(value * scale) / scale;
    }


    /**
     * Map containing sensor - dataList size
     */
    public static final Map<String, Integer> sensorDataListSize = new HashMap<>(); // TODO: remove this and get datalist size from input file
    static{
        sensorDataListSize.put("HRM", 1);
        sensorDataListSize.put("PRESSURE", 1);
    }

    /**
     * Creates and sets a day cell factory to disable future dates in the provided DatePicker
     */
    public static void setDateFactory(DatePicker datePicker){
        // Create a day cell factory
        Callback<DatePicker, DateCell> dayCellFactory = new Callback<>() {
            public DateCell call(final DatePicker datePicker) {
                return new DateCell() {
                    @Override
                    public void updateItem(LocalDate item, boolean empty) {
                        // Must call super
                        super.updateItem(item, empty);

                        // Disable all future date cells
                        if (item.isAfter(LocalDate.now())) {
                            this.setDisable(true);
                        }
                    }
                };
            }
        };
        datePicker.setDayCellFactory(dayCellFactory);
    }

}
