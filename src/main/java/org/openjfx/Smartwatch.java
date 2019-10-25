package org.openjfx;

import javafx.collections.transformation.SortedList;

import java.util.*;


// Class holding all smartwatch functionality
class Smartwatch {

    // Data about watch
    private WatchData watchData;

    // Data about subject wearing the watch TODO: Implement
    private SubjectData subjectData;

    // Nickname for watch used in program
    private String watchName;

    // list of data of sensors
    private List<SensorData> sensorDataList = new ArrayList<>();

    // Map to map sensor name string to integer index for sensorDataList
    private static final Map<String, Integer> sensorMap;
    static{
        sensorMap = new HashMap<>();
        sensorMap.put("HRM", 0); // put HRM on spot 0
        sensorMap.put("GRAVITY", 1);
        sensorMap.put("ACCELEROMETER", 2);
    }


    // Constructor
    Smartwatch(WatchData _data, SubjectData _subjectData){
        System.out.println("making smartwatch");
        watchData = _data;
        subjectData = _subjectData;
        watchName = "NO NAME";


        sensorDataList.add(new SensorData(watchData.getWatchID(), "HRM"));
        sensorDataList.add(new SensorData(watchData.getWatchID(), "GRAVITY"));
        sensorDataList.add(new SensorData(watchData.getWatchID(), "ACCELEROMETER"));
    }


    // Get data of sensor 'sensor' from sensorDataList
    SensorData getSensorData(String sensor){
        return sensorDataList.get(sensorMap.get(sensor));
    }


    int getWatchID() {
        return watchData.getWatchID();
    }

    void setWatchID(int ID) { watchData.setWatchID(ID); }

    int getBatteryPercentage(){ return watchData.getBatteryPercentage(); }

    WatchData getWatchData() { return watchData; }

    String getWatchName() { return watchName; }

    void setWatchName(String name) { watchName = name; }

    // add new data points to records
    void addData(List<DataPoint> dataList){
        SortedSet<String> sensorDataEdited = new TreeSet<>();
        String sensor = "";

        for (DataPoint dataPoint : dataList) {
            sensorDataList.get(sensorMap.get(dataPoint.getSensorName())).add(dataPoint);
            sensorDataEdited.add(dataPoint.getSensorName());
        }

        for(int i = 0; i < sensorDataEdited.size(); i++){
            sensor = sensorDataEdited.first();
            sensorDataList.get(sensorMap.get(sensor)).mergeDuplicates();
            sensorDataEdited.remove(sensor);
        }
    }

    public List<SensorData> getSensorDataList() {
        return sensorDataList;
    }
}


































