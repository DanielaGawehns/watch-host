package org.openjfx;

import javafx.scene.chart.XYChart;
import util.Util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;



/**
 * Class for storing {@link DataPoint} of a sensor of a watch
 */
public class SensorData {

    /**
     * List of {@link DataPoint} containing the data from the sensor
     */
    private List<DataPoint> records;

    /**
     * Name of the sensor the data is from
     */
    private final String sensor;


    /**
     * Stores the amount of data columns. These only include the double values
     */
    private final int dataFieldsNumber;

    /**
     * ID of the watch this data belong to
     */
    private final String watchID;

    /**
     * The date from which the data has been loaded into the program from the database. This shows if we need to load
     * older data from the database when changing around the starting dates in {@link org.openjfx.controllers.OverviewController}
     * or {@link org.openjfx.controllers.WatchViewController}
     */
    private LocalDate dataLoadedDate;


    /**
     * Constructor
     */
    SensorData(String _watchID, String _sensor, int _dataFieldsNumber, LocalDate dataLoadedDate){
        records = new ArrayList<>();
        sensor = _sensor;
        watchID = _watchID;
        this.dataLoadedDate = dataLoadedDate;
        dataFieldsNumber = _dataFieldsNumber;
    }

    /**
     * Constructor
     */
    SensorData(String _watchID, String _sensor, int _dataFieldsNumber, LocalDate dataLoadedDate, List<DataPoint> data){
        records = new ArrayList<>();
        sensor = _sensor;
        watchID = _watchID;
        dataFieldsNumber = _dataFieldsNumber;
        this.dataLoadedDate = dataLoadedDate;
        records = data;
    }


    /**
     * Gives the size of {@link SensorData#records}
     * @return Size of {@link SensorData#records} as an integer
     */
    public int size(){
        return records.size();
    }


    /**
     * Clears {@link SensorData#records}
     */
    public void clear(){
        records.clear();
    }


    /**
     * Adds a {@link DataPoint} to {@link SensorData#records}
     * @param point The {@link DataPoint} to be added
     */
    void add(DataPoint point){
        records.add(point);
    }


    // TODO: Delete
    /**
     * **DEBUG** Function to print the contents of {@link SensorData#records}.
     */
    void printRecords(){
        for(int i = 0; i < records.size(); i++){
            DataPoint point = records.get(i);
            System.out.print(i + ": " + point.getDateTime());
            List<Double> list = point.getDataList();
            for (Double aDouble : list) {
                System.out.print("," + aDouble);
            }
            System.out.println();
        }
    }


    /**
     * Getter for {@link SensorData#sensor}
     */
    public String getSensor() {
        return sensor;
    }


    /**
     * Getter for {@link SensorData#watchID}
     */
    String getWatchID() {
        return watchID;
    }


    /**
     * Getter for {@link DataPoint} containing data from sensor
    */
    public List<DataPoint> getRecords(){ return records; }


    /**
     * Getter for {@link SensorData#dataFieldsNumber}
     */
    int getDataFieldsNumber(){ return dataFieldsNumber; }


    /**
     * Checks if there is a {@link DataPoint} that has the same date and time values
     * @param dateTime DateTime of the measurement
     * @return True if it contains a point with the same data and time values. False if not
     */
    boolean contains(LocalDateTime dateTime){
        for(int i = 0; i < size(); i++){
            if(records.get(i).getDateTime().equals(dateTime)){
                return true;
            }
        }
        return false;
    }


    // TODO: delete this if the sensor data received from the watch can be in ms precise
    /**
     * Merges duplicates values
     */
    void mergeDuplicates() {
        List<DataPoint> mergedRecords = new ArrayList<>();
        List<Double> totalValues = new ArrayList<>();
        double duplicateCounter = 0d;

        System.out.println("SensorData: start merge");

        if (records.size() <= 0) {
            return;
        }

        DataPoint old = records.get(0);

        for (int j = 0; j < old.getDataList().size(); j++) { // add old data to totalValues
            totalValues.add(0d);
        }

        for (int i = 1; i < records.size(); i++) {
            DataPoint record = records.get(i);
            Util.addDoubleLists(totalValues, old.getDataList());

            if (old.getDateTime().equals(record.getDateTime())) { // if datetimes are the same
                duplicateCounter++;
            } else { // add point to records
                Util.divideDoubleList(totalValues, duplicateCounter);
                old.setDataList(totalValues); // set the data list to calculated values
                mergedRecords.add(old);

                totalValues = new ArrayList<>();
                for (int j = 0; j < old.getDataList().size(); j++) {
                    totalValues.add(0d);
                }
                duplicateCounter = 1;

            }
            old = record;
        }


        System.out.println("Merged records have size " + mergedRecords.size());
        records = mergedRecords;
        printRecords();
    }

    /**
     * Gets the ist {@link DataPoint} from {@link SensorData#records}
     * @param i The index of the {@link DataPoint} to get
     * @return The {@link DataPoint} to get
     */
    DataPoint get(int i){ return records.get(i); }


    // TODO: make this work for all types of data
    /**
     * Gets a {@link DataPoint} and converts the data to {@link XYChart.Data} for use in charts
     * @param i The index of the {@link DataPoint} to get
     * @return A {@link XYChart.Data} containing the time and the first data column of {@link DataPoint}
     */
    XYChart.Data<String, Number> getDataPoint(int i) {
        return new XYChart.Data<>(records.get(i).getDateTime().toLocalTime().toString(), records.get(i).getDataList().get(0));
    }


    /**
     * Getter for {@link SensorData#dataLoadedDate}
     */
    LocalDate getDataLoadedDate() { return dataLoadedDate; }

    /**
     * Setter for {@link SensorData#dataLoadedDate}
     */
    void setDataLoadedDate(LocalDate dataLoadedDate) { this.dataLoadedDate = dataLoadedDate; }
}























