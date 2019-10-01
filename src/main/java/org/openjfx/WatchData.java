package org.openjfx;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WatchData {

    private static final String COMMA_DELIMITER = ",";

    private List<List<String>> records = new ArrayList<>();

    private String sensor;

    // constructor
    public WatchData(){

    }

    // read in data from CSV file
    public void readData(String filename, String sensorTemp){
        clear();
        sensor = sensorTemp;
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(COMMA_DELIMITER);
                records.add(Arrays.asList(values));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void printData(){
        for(int i = 0; i < records.size(); i++){
            for(int j = 0; j < records.get(i).size(); j++){
                System.out.print(records.get(i).get(j) + "-");
            }
            System.out.println("");
        }
    }

    public void clear(){
        records.clear();
    }

    public List<List<String>> getRecords() {
        return records;
    }

    public String getSensor() {
        return sensor;
    }
}
