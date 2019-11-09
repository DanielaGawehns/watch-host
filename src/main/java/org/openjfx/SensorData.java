package org.openjfx;

import javafx.scene.chart.XYChart;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;



/**
 * Class for storing {@link DataPoint} of a sensor
 */
public class SensorData {

    /**
     * List of {@link DataPoint} containing the data from the sensor
     */
    private List<DataPoint> records;

    /**
     * Name of the sensor the data is from
     */
    private String sensor;

    /**
     * Number of the watch this data belong to
     */
    private int watchNumber;

    /**
     * Format used for dates
     */
    private final static SimpleDateFormat date = new SimpleDateFormat("dd/MM/yyyy");

    /**
     * Format for times
     */
    private final static SimpleDateFormat time = new SimpleDateFormat("hh:mm:ss");


    /**
     * Constructor
     */
    SensorData(int _watchNumber, String _sensor){
        records = new ArrayList<>();
        sensor = _sensor;
        watchNumber = _watchNumber;
    }


    /**
     * Gives the size of {@link SensorData#records}
     * @return Size of {@link SensorData#records} as an integer
     */
    int size(){
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
            System.out.print(i + ": " + date.format(point.getDate()) + " , " + time.format(point.getTime()));
            List<Double> list = point.getDataList();
            for (Double aDouble : list) {
                System.out.print(" , " + aDouble);
            }
            System.out.println("");
        }
    }


    /**
     * Getter for {@link SensorData#sensor}
     */
    String getSensor() {
        return sensor;
    }


    /**
     * Getter for {@link SensorData#watchNumber}
     */
    int getWatchNumber() {
        return watchNumber;
    }


    /**
     * Merges duplicates ({@link DataPoint} with the same {@link DataPoint#time}) by averaging the {@link DataPoint#dataList} values
     */
    void mergeDuplicates(){
        List<DataPoint> mergedRecords = new ArrayList<>();
        List<Double> totalValues = new ArrayList<>();
        double duplicateCounter = 1d;

        System.out.println("SensorData: start merge");

        if(records.size() > 0){
            DataPoint old = records.get(0);

            for(int j = 0; j < old.getDataList().size(); j++){ // add old data to totalValues
                totalValues.add(0d);
            }

            for(int i = 1; i < records.size(); i++){
                DataPoint record = records.get(i);
                Util.addDoubleLists(totalValues, old.getDataList());

                if(old.getTime().compareTo(record.getTime()) == 0){ // if times are the same
                    duplicateCounter++;
                }else{ // add point to records
                    Util.divideDoubleList(totalValues, duplicateCounter);
                    old.setDataList(totalValues); // set the data list to calculated values
                    mergedRecords.add(old);

                    totalValues = new ArrayList<>();
                    for(int j = 0; j < old.getDataList().size(); j++){
                        totalValues.add(0d);
                    }
                    duplicateCounter = 1;

                }

                old = record;
            }
        }
        System.out.println("Merged records have size " + mergedRecords.size());
        records = mergedRecords;
    }

    DataPoint get(int i){ return records.get(i); }


    // TODO: make this work for all types of data
    /**
     * Gets a {@link DataPoint} and converts the data to {@link XYChart.Data} for use in charts
     * @param i The {@link DataPoint} to get
     * @return A {@link XYChart.Data} containing {@link DataPoint#time} and the first item of {@link DataPoint#dataList}
     */
    XYChart.Data<String, Number> getDataPoint(int i) {
        return new XYChart.Data<>(time.format(records.get(i).getTime()), records.get(i).getDataList().get(0));
    }
}























