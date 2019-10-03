package org.openjfx;

import javafx.scene.chart.XYChart;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WatchData {

    // splitting symbol for csv files
    private static final String COMMA_DELIMITER = ",";

    // data for charting
    private List<XYChart.Data<Number, Number>> records;

    // name of sensor used
    private String sensor;

    // constructor
    public WatchData(){
        records = new ArrayList<>();
        sensor = "NO SENSOR SPECIFIED";
    }

    // read in data from CSV file
    // reads from file 'filename'
    // TODO: remove sensorTemp and read sensor type from CSV
    public void readData(String filename, String sensorTemp){
        XYChart.Data<Number, Number> temp;
        int count = 0;
        clear();
        sensor = sensorTemp;
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) { // read in file
            String line;
            while ((line = br.readLine()) != null) { // go through all the lines
                String[] values = line.split(COMMA_DELIMITER);
                temp = new XYChart.Data<>();
                temp.setXValue(Double.parseDouble(values[0])); // parse string to double
                temp.setYValue(Double.parseDouble(values[1]));
                records.add(count, temp); // add to record
                count++;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // function to print values in records
    // TODO: delete
    public void printData(){
        for(int i = 0; i < records.size(); i++){
            XYChart.Data<Number, Number> data = records.get(i);
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

    // returns sensor
    public String getSensor() {
        return sensor;
    }
}
