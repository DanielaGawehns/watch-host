package org.openjfx;

import java.io.BufferedReader;
import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;



/**
 * Helper class to read and parse data from CSV files received from the watches
 * This class will be replaced by protocol classes
 */
public class CSVFileReader{

    /**
     * Delimiter to split data values
     */
    private static final String COMMA_DELIMITER = ",";

    /**
     * Index of where in the string array the data values start
     */
    private static final int DATA_START = 3;

    /**
     * The ID of the watch the file belongs to
     */
    private String watchID;



    /**
     * Reads a csv file and parses the data using {@link org.openjfx.CSVFileReader#parseRecord(String[])}
     * Expects at least a rule starting with # indicating a watchnumber
     * @param path Specifies file location
     * @return List of {@link org.openjfx.DataPoint} containing the parsed data
     */
    public List<DataPoint> readFile(String path){
        System.out.println("Start reading: " + path);
        List<DataPoint> dataList = new ArrayList<>();
        DataPoint point;

        watchID = null;

        try (BufferedReader br = new BufferedReader(new java.io.FileReader(path))) { // read in file
            String line = br.readLine();
            if(line.startsWith("#")){ // read watch ID
                watchID = line.substring(1);
                System.out.println("Watch ID: " + watchID);
            }else{
                System.out.println("No watch ID found"); // TODO: make exception
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

        System.out.println("Read datalist of size " + dataList.size());
        return dataList;
    }


    /**
     * Parses a split line into usable data
     * @param record Should contain split strings representing a line of a CSV file
     * @return A {@link DataPoint} containing the parses information
     * @throws ParseException If a wrong format is found
     */
    private DataPoint parseRecord(String[] record) throws ParseException {

        DateTimeFormatter formatterDate = DateTimeFormatter.ofPattern("d/MM/yyyy");

        if(record.length < 4){
            System.out.println("Record error");
            throw new ParseException("Length of records too small", 0);
        }
        int dataFields = 1;
        String sensorName = record[0];
        LocalDate date = LocalDate.parse(record[1], formatterDate);
        LocalTime time = LocalTime.parse(record[2]);
        LocalDateTime dateTime = LocalDateTime.of(date, time);
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
        return new DataPoint(sensorName, dateTime, data);
    }


    /**
     * Getter for {@link org.openjfx.CSVFileReader#watchID}
     * @return Int containing the watchNumber
     */
    public String getWatchID() {
        return watchID;
    }
}
