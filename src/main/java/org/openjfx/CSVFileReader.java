package org.openjfx;

import javafx.scene.chart.XYChart;

import java.io.BufferedReader;
import java.io.IOException;


// class for reading CSV files
// TODO: extend this class for dealing with all layouts necessary
public class CSVFileReader{

    // splitting symbol for csv files
    private static final String COMMA_DELIMITER = ",";


    // read in data from CSV file
    // reads from file 'filename'
    // returns data as SensorData
    // TODO: remove sensorTemp and read sensor type from CSV
    public SensorData readFile(String path){
        System.out.println("Start reading: " + path);
        SensorData sensorData = null;
        XYChart.Data<Number, Number> temp;
        int watchNumber;

        try (BufferedReader br = new BufferedReader(new java.io.FileReader(path))) { // read in file
            String line;
            while ((line = br.readLine()) != null) { // go through all the lines
                if(line.startsWith("#")){
                    watchNumber = Integer.parseInt(line.substring(1)) - 1;
                    System.out.println("Watch number: " + watchNumber);
                    sensorData = new SensorData(watchNumber, "TEMP");
                } else if(Character.isDigit(line.charAt(0))){
                    String[] values = line.split(COMMA_DELIMITER);
                    temp = new XYChart.Data<>();
                    temp.setXValue(Double.parseDouble(values[0])); // parse string to double
                    temp.setYValue(Double.parseDouble(values[1]));
                    assert sensorData != null;
                    sensorData.add(temp); // add to record
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Done reading: " + path);

        return sensorData;
    }

}
