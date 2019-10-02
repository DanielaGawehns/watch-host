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

    private static final String COMMA_DELIMITER = ",";

    private List<XYChart.Data<Number, Number>> records;

    private String sensor;

    // constructor
    public WatchData(){
        records = new ArrayList<>();
        sensor = "NO SENSOR SPECIFIED";
    }

    // read in data from CSV file
    public void readData(String filename, String sensorTemp){
        XYChart.Data<Number, Number> temp;
        int count = 0;
        clear();
        sensor = sensorTemp;
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(COMMA_DELIMITER);
                temp = new XYChart.Data<>();
                temp.setXValue(Double.parseDouble(values[0]));
                temp.setYValue(Double.parseDouble(values[1]));
                records.add(count, temp);
                count++;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void printData(){
        for(int i = 0; i < records.size(); i++){
            XYChart.Data<Number, Number> data = records.get(i);
            System.out.println(data.getXValue() + "-" + data.getYValue());
        }
    }

    public XYChart.Data<Number, Number> getDataPoint(int index){
        return records.get(index);
    }

    public int size(){
        return records.size();
    }

    public void clear(){
        records.clear();
    }

    public String getSensor() {
        return sensor;
    }
}
