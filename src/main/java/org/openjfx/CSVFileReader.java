package org.openjfx;

import java.io.BufferedReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;



/**
 * Helper class to read and parse data from CSV files received from the watches
 */
class CSVFileReader{

    private static final String COMMA_DELIMITER = ",";

    private static final int DATA_START = 3;

    /**
     * The number of the watch the file belongs to
     */
    private int watchNumber;



    /**
     * Reads a csv file and parses the data using {@link org.openjfx.CSVFileReader#parseRecord(String[])}
     * Expects at least a rule starting with # indicating a watchnumber
     * @param path Specifies file location
     * @return List of {@link org.openjfx.DataPoint} containing the parsed data
     */
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
                try {
                    point = parseRecord(record); // TODO: make try catch
                }catch (ParseException e){
                    System.out.println(e.getMessage());
                    continue; // don't add
                }

                dataList.add(point);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Done reading: " + path);

        return dataList;
    }

    /**
     * Parses a split line into usable data
     * @param record Should contain split strings representing a line of a CSV file
     * @return A {@link DataPoint} containing the parses information
     * @throws ParseException If a wrong format is found
     */
    private DataPoint parseRecord(String[] record) throws ParseException {

        if(record.length < 4){
            System.out.println("Record error");
            throw new ParseException("Length of records too small", 0);
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
            try {
                data.add(Double.parseDouble(record[i]));
            }catch (NumberFormatException e){
                throw new ParseException("Datapoint has wrong format", 0);
            }

        }
        return new DataPoint(sensorName, date, time, data);
    }

    /**
     * Getter for {@link org.openjfx.CSVFileReader#watchNumber}
     * @return Int containing the watchNumber
     */
    int getWatchNumber() {
        return watchNumber;
    }
}
