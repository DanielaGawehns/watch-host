package org.openjfx;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * Class for writing the data from database to CSV files
 * CSV format: WatchID,Sensor,Date,Time,Double(s)
 */
public class CSVWriter {
    private static final String COMMA_DELIMITER = ",";
    private static final String NEW_LINE_SEPARATOR = "\n";


    /**
     * {@link FileWriter} object which will write data to a file
     */
    private FileWriter fileWriter = null;



    /**
     * Retrieve data from one watch fom database and writes into a CSV file
     */
    public void WriteOne(Smartwatch watch, File filename, boolean header) {
        StringBuilder sb = new StringBuilder();
        DBManager DB = App.getDbManager();
        int maxDim = 0; // maximum dimensionality of data point
        boolean skip = true;

        try {
            if(fileWriter == null) {
                fileWriter = new FileWriter(filename);
                skip = false;
            }
            String id = watch.getWatchID();
            List<SensorData> sensorDataList = DB.getAllDataLists(id);

            if ( header ) {
                for (SensorData sensorData : sensorDataList) {
                    if (sensorData == null) {
                        System.err.println("ERROR: sensordata is null");
                        continue;
                    }
                    List<DataPoint> _DataPoint = sensorData.getRecords();
                    for (int j = 0; j < sensorData.size(); j++) {
                        var dataPointDim = _DataPoint.get(j).getDataList().size();
                        if (dataPointDim > maxDim) {
                            maxDim = dataPointDim;
                        }
                    }
                }
                sb.append("WatchID,Sensor,Date,Time");
                for (int i = 1; i < maxDim + 1; i++) {
                    sb.append(COMMA_DELIMITER);
                    sb.append("data");
                    sb.append(i);
                }

                fileWriter.append(sb);
                sb.setLength(0);
                fileWriter.append(NEW_LINE_SEPARATOR);
            }

            for (SensorData sensorData : sensorDataList) {
                if (sensorData == null) {
                    System.err.println("ERROR: sensordata is null");
                    continue;
                }
                List<DataPoint> datapoints = sensorData.getRecords();
                for (var datapoint : datapoints) {
                    sb.append(watch.getWatchID());
                    sb.append(COMMA_DELIMITER);
                    sb.append(datapoint.getSensorName());
                    sb.append(COMMA_DELIMITER);
                    sb.append(datapoint.getDate());
                    sb.append(COMMA_DELIMITER);
                    sb.append(datapoint.getTime());
                    for (int i = 0; i < datapoint.getDataList().size(); i++) {
                        sb.append(COMMA_DELIMITER);
                        sb.append(datapoint.getDataList().get(i));
                    }
                    fileWriter.append(sb);
                    sb.setLength(0);
                    fileWriter.append(NEW_LINE_SEPARATOR);
                }
            }
        }catch (Exception e) {
            System.out.println("Error in CsvFileWriter !!!");
            e.printStackTrace();
        } finally {
            try {
                if(!skip){
                    fileWriter.flush();
                    fileWriter.close();
                }
            } catch (IOException e) {
                System.out.println("Error while flushing/closing fileWriter !!!");
                e.printStackTrace();
            }
        }
    }


    /**
     * Retrieve data from all the watches fom database and writes into a CSV file
     */
    public void WriteAll(File filename) {
        DBManager DB = App.getDbManager();
        boolean header = true;

        try {
            fileWriter = new FileWriter(filename);
            for (Smartwatch _watch : DB.getAllWatches()) {
                System.out.println("Writing data for watch " + _watch.getWatchID());
                WriteOne(_watch, filename, header);
                header = false;
            }
        } catch (Exception e) {
            System.out.println("Error in CsvFileAllWriter !!!");
            e.printStackTrace();
        }finally {
            try {
                fileWriter.flush();
                fileWriter.close();
            } catch (IOException e) {
                System.out.println("Error while flushing/closing fileWriter !!!");
                e.printStackTrace();
            }
        }
    }
}