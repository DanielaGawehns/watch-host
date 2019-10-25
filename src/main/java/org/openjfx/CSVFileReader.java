package org.openjfx;

import javafx.scene.chart.XYChart;

import java.io.BufferedReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


// class for reading CSV files
// TODO: extend this class for dealing with all layouts necessary
class CSVFileReader{

    // splitting symbol for csv files
    private static final String COMMA_DELIMITER = ",";

    private static final int DATA_START = 3;

    private int watchNumber;


    // read in data from CSV file
    // reads from file 'filename'
    // returns data as List of DataPoints
    List<DataPoint> readFile(String path){
        System.out.println("Start reading: " + path);
        List<DataPoint> dataList = new ArrayList<>();
        DataPoint point;

        watchNumber = -1;

        try (BufferedReader br = new BufferedReader(new java.io.FileReader(path))) { // read in file
            String line = br.readLine();
            if(line.startsWith("#")){ // read watchnumber
                watchNumber = Integer.parseInt(line.substring(1)) - 1;
                System.out.println("Watch number: " + watchNumber);
            }else{
                System.out.println("No watch number found"); // TODO: make exception
            }
            while ((line = br.readLine()) != null) { // go through all the lines

                String[] record = line.split(COMMA_DELIMITER);
                point = parseRecord(record);
                dataList.add(point);
               //System.out.println("added point with time " + point.getTime().toString());
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        System.out.println("Done reading: " + path);

        return dataList;
    }

    private DataPoint parseRecord(String[] record) throws ParseException {
        if(record.length < 4){
            System.out.println("Record error"); // TODO: make exception
        }
        int dataFields = 1;
        String sensorName = record[0];
        Date date = new SimpleDateFormat("dd/MM/yyyy").parse(record[1]);
        Date time = new SimpleDateFormat("hh:mm:ss").parse(record[2]);
        List<Double> data = new ArrayList<>();

        switch (sensorName){
            case "ACCELEROMETER":
            case "GRAVITY":
                dataFields = 3;
                break;
        }

        for(int i = DATA_START; i < DATA_START + dataFields; i++){
            data.add(Double.parseDouble(record[i]));
        }
        //System.out.println("CSVReader: Parsed record with data: " + sensorName + " , " + date.toString() + " , " + time.toString());
        return new DataPoint(sensorName, date, time, data);
    }

    int getWatchNumber() {
        return watchNumber;
    }
}
