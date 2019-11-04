package org.openjfx;

import javafx.scene.chart.XYChart;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


// Class for storing data of a sensor
public class SensorData {

    // data for charting
    private List<DataPoint> records;

    // name of sensor used
    private String sensor;

    // Number of the watch this data belong to
    private int watchNumber;

    private SimpleDateFormat date = new SimpleDateFormat("dd/MM/yyyy");
    private SimpleDateFormat time = new SimpleDateFormat("hh:mm:ss");


    // constructor
    SensorData(int _watchNumber, String _sensor){
        records = new ArrayList<>();
        sensor = _sensor;
        watchNumber = _watchNumber;
    }


    // returns size of records
    int size(){
        return records.size();
    }


    // clears records
    public void clear(){
        records.clear();
    }


    // adds data point to records
    void add(DataPoint point){
        records.add(point);
    }


    public XYChart.Data<Date, Number> getXYPoint(){
        return null;
    };


    // TODO: Delete
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


    // returns sensor
    String getSensor() {
        return sensor;
    }


    // returns the watchNumber
    int getWatchNumber() {
        System.out.println("Returning watchnumber: " + watchNumber);
        return watchNumber;
    }


    // Merges the duplicates (same time) in records by taking the average value
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
                addDoubleLists(totalValues, old.getDataList());

                if(old.getTime().compareTo(record.getTime()) == 0){ // if times are the same
                    duplicateCounter++;
                }else{ // add point to records
                    divideDoubleList(totalValues, duplicateCounter);
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
        records = mergedRecords;
    }


    // Add two double lists together by adding the items
    // If the lists are not the same length return
    private void addDoubleLists(List<Double> list1, List<Double> list2){
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


    // Rounds 'value' to 'places' decimals
    private static double round(double value, int places) {
        double scale = Math.pow(10, places);
        return Math.round(value * scale) / scale;
    }


    // Divides all values in the list by 'division'
    private void divideDoubleList(List<Double> list, Double division){
        for(int i = 0; i < list.size(); i++){
            list.set(i, round(list.get(i) / division, 1));
        }
    }

    // TODO: make this work for all types of data
    XYChart.Data<String, Number> getDataPoint(int i) {
        return new XYChart.Data<>(time.format(records.get(i).getTime()), records.get(i).getDataList().get(0));
    }

    // TODO: implement to save pins to csv
    DataPoint getDataPoint(String time){
        return null;
    }
}























