package org.openjfx;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.io.File;

/**
 * Class for writing the data from database to CSV files
 * CSV format: WatchID,Sensor,Date,Time,Double(s)
 */
public class CSVWriter {
    private static final String COMMA_DELIMITER = ",";
    private static final String NEW_LINE_SEPARATOR = "\n";
    private File filename;
   // private Smartwatch watch;

    /**
     * Constructor
     */
    public CSVWriter(File _filename) {
        filename = _filename;
        //watch = _Watch;
    }


    /**
     * Retrieve data from one watch fom database and writes into a CSV file
     * @throws IOException If it is not able to write a CSV file
     */
    public void WriteOne(Smartwatch watch, boolean header) {
        FileWriter _FileWriter = null;
        StringBuilder sb = new StringBuilder();
        DBManager DB = new DBManager();
        int maxDim = 0; // maximum dimensionality of data point
       // int id = watch.getWatchID();
       // List<SensorData> sensorDataList = DB.getAllDataLists(id);

        try {
            _FileWriter = new FileWriter(filename);
            int id = watch.getWatchID();
            List<SensorData> sensorDataList = DB.getAllDataLists(id);

            if ( header ) {
                for (SensorData sensorData : sensorDataList) {
                    if (sensorData == null) continue;
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

                _FileWriter.append(sb);
                sb.setLength(0);
                _FileWriter.append(NEW_LINE_SEPARATOR);

            }


            for (SensorData sensorData : sensorDataList) {
                if (sensorData == null) continue;
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
                    _FileWriter.append(sb);
                    sb.setLength(0);
                    _FileWriter.append(NEW_LINE_SEPARATOR);
                }
            }
        }catch (Exception e) {
            System.out.println("Error in CsvFileWriter !!!");
            e.printStackTrace();
        } finally {
            try {
                _FileWriter.flush();
                _FileWriter.close();
            } catch (IOException e) {
                System.out.println("Error while flushing/closing fileWriter !!!");
                e.printStackTrace();
            }
        }


    }

    /**
     * Retrieve data from all the watches fom database and writes into a CSV file
     * @throws IOException If it is not able to write a CSV file
     */
    public void WriteAll() {
        DBManager DB = new DBManager();
        boolean header = true;
        try {
            for (Smartwatch _watch : DB.getAllWatches()) {

                    WriteOne(_watch, header);
                    header = false;
            }
        } catch (Exception e) {
            System.out.println("Error in CsvFileAllWriter !!!");
            e.printStackTrace();
        }
    }
}
/*
aray[int] x 452678
for (var Nummmers: x)

for( watchid id : lijstwatchid){
        writeone(id)
        }
*/