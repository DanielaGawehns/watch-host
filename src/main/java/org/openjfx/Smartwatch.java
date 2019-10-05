package org.openjfx;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Class holding all smartwatch functionality
public class Smartwatch {

    // list of data of sensors
    private List<SensorData> sensorDataList = new ArrayList<>();

    private static final Map<String, Integer> sensorMap;
    static{
        sensorMap = new HashMap<>();
        sensorMap.put("HR", 0);
    }

    // Number of sensors for a smartwatch
    private static final int numberOfSensors = 1;

    // NumberID of the watch
    private int watchID;


    // Constructor
    Smartwatch(int _watchID){
        System.out.println("making smartwatch");
        watchID = _watchID;
        for(int i = 0; i < numberOfSensors; i++){
            sensorDataList.add(null);
        }
    }


    // Get data of sensor 'sensor' from sensorDataList
    // TODO: make this work
    SensorData getSensorData(String sensor){
        return sensorDataList.get(sensorMap.get("HR"));
    }


    // Set data of sensorDataList
    // if sensor is not found then add to list
    // otherwise replace data
    // TODO: make this work
    void setSensorData(SensorData data){
        sensorDataList.set(sensorMap.get(data.getSensor()), data);
    }


    // Function to fill sensorMap with default values
    private void fillSensorMap(){

    }

    public int getWatchID() {
        return watchID;
    }
}