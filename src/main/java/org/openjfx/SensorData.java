package org.openjfx;

import javafx.scene.chart.XYChart;

import java.util.ArrayList;
import java.util.List;


// Class for storing data of a sensor
public class SensorData {

    // data for charting
    private List<XYChart.Data<Number, Number>> records;


    // name of sensor used
    private String sensor;


    // Number of the watch this data belong to
    private int watchNumber;


    // constructor
    public SensorData(int _watchNumber, String _sensor){
        records = new ArrayList<>();
        sensor = _sensor;
        watchNumber = _watchNumber;
        //readData(filename);
    }


    // function to print values in records
    // TODO: delete
    public void printData(){
        for (XYChart.Data<Number, Number> data : records) {
            System.out.println(data.getXValue() + "-" + data.getYValue());
        }
    }


    // returns datapoint 'index' from records
    public XYChart.Data<Number, Number> getDataPoint(int index){
        return records.get(index);
    }


    // returns size of records
    public int size(){
        return records.size();
    }


    // clears records
    public void clear(){
        records.clear();
    }


    // adds data point to records
    public void add(XYChart.Data<Number, Number> data){ records.add(data); }


    // returns sensor
    public String getSensor() {
        return sensor;
    }


    // returns the watchNumber
    public int getWatchNumber() {
        System.out.println("Returning watchnumber: " + watchNumber);
        return watchNumber;
    }
}
