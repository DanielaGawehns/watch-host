package org.openjfx;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * CSV format: WatchID,Sensor,Date,Time,Double(s)
 */
class CSVWriter {
    private static final String COMMA_DELIMITER = ",";
    private static final String NEW_LINE_SEPARATOR = "\n";
    //private static final String FILE_HEADER = "WatchID,Sensor,Date,Time,Data1,Data2,Data3";
    private String filename;
    private Smartwatch watch;

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

    public CSVWriter(String _filename, Smartwatch _Watch) {
        filename = _filename;
        watch = _Watch;
    }

    public void WriteFile() {
        FileWriter fileWriter = null;
        StringBuilder sb = new StringBuilder();
        int maxDim = 0; // maximum dimensionality of data point

        try {
            fileWriter = new FileWriter(filename);

            for (int i = 0; i < allSensors.size(); i++) {
                SensorData _SensorData = watch.getSensorData(allSensors.get(i));
                if (_SensorData == null) continue;
                List<DataPoint> _DataPoint = _SensorData.getRecords();
                for (int j = 0; j < _SensorData.size(); j++) {
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

            for (var sensor : allSensors) {
                var sensorData = watch.getSensorData(sensor);
                if (sensorData == null) continue;
                List<DataPoint> datapoints = sensorData.getRecords();
                for (var datapoint : datapoints) {
                    sb.append(watch.getWatchID());
                    sb.append(COMMA_DELIMITER);
                    sb.append(datapoint.getSensorName());     // is datapoint gelijk te zetten in csv?
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
        } catch (Exception e) {
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
