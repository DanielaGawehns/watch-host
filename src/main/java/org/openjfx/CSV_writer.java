package org.openjfx;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
* CSV format: WatchID,Sensor,Date,Time,Double(s)
 * */
public class CSV_writer {
    private static final String COMMA_DELIMITER = ",";
    private static final String NEW_LINE_SEPARATOR = "\n";
    //private static final String FILE_HEADER = "WatchID,Sensor,Date,Time,Data1,Data2,Data3";


    public CSV_writer(String fileName, Smartwatch _Watch ){



        private List<String> allSensors = new ArrayList<>() {
            {
                add("ACCELEROMETER");
                add("GRAVITY");
                add("LINEAR ACCELERATION");
                add("MAGNETIC");
                add("ROTATION VECTOR");
                add("ORIENTATION");
                add("GYROSCOPE");
                add("LIGHT");
                add("PROXIMITY");
                add("PRESSURE");
                add("ULTRAVIOLET");
                add("TEMPERATURE");
                add("HUMIDITY");
                add("HRM");
            }
        };

        FileWriter fileWriter = null;
        StringBuilder sb = new StringBuilder();
        int maxsize = 0; //size of data point

        try{
            fileWriter = new FileWriter(fileName);

            for ( int i = 0; i < allSensors.size() ; i ++) {
                SensorData _SensorData = _Watch.getSensorData(allSensors.get(i));
                List<DataPoint> _DataPoint = _SensorData.getRecords();
                for (int j = 0; j < _SensorData.size(); j++) {
                    if (_DataPoint.get(j).getDataList().size() > maxsize){
                        maxsize = _DataPoint.get(j).getDataList().size();
                    }
                }
            }
            sb.append ("WatchID,Sensor,Date,Time");
            for ( int i = 1 ; i < maxsize+1 ; i ++){
                sb.append( COMMA_DELIMITER);
                sb.append( "data");
                sb.append ( i );
             }

            fileWriter.append(sb);
            sb.setLength(0);
            fileWriter.append(NEW_LINE_SEPARATOR);

            for ( int i = 0; i < allSensors.size() ; i ++) {
                SensorData _SensorData = _Watch.getSensorData(allSensors.get(i));
                List<DataPoint> _DataPoint = _SensorData.getRecords();
                for (int j = 0; j < _SensorData.size(); j++) {
                    sb.append(_Watch.getWatchID());
                    sb.append(COMMA_DELIMITER);
                    sb.append(_DataPoint.get(j).getSensorName());     // is datapoint gelijk te zetten in csv?
                    sb.append(COMMA_DELIMITER);
                    sb.append(_DataPoint.get(j).getDate());
                    sb.append(COMMA_DELIMITER);
                    sb.append(_DataPoint.get(j).getTime());
                    for (int k = 0; k <_DataPoint.get(j).getDataList().size() ;k++){
                        sb.append(COMMA_DELIMITER);
                        sb.append( _DataPoint.get(j).getDataList().get(k) );
                    }

                    fileWriter.append(sb);
                    sb.setLength(0);
                    fileWriter.append(NEW_LINE_SEPARATOR);
                }
            }
         }  catch (Exception e) {
              System.out.println("Error in CsvFileWriter !!!");
              e.printStackTrace();
            } finally {
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
