package org.openjfx;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Class holding all smartwatch functionality
public class Smartwatch {

    // list of data of sensors
    private List<SensorData> sensorDataList = new ArrayList<>();

    // Map to map sensor name string to integer index for sensorDataList
    private static final Map<String, Integer> sensorMap;
    static{
        sensorMap = new HashMap<>();
        sensorMap.put("HR", 0); // put HR on spot 0
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
            sensorDataList.add(null); // add empty sensorData
        }
    }


    // Get data of sensor 'sensor' from sensorDataList
    SensorData getSensorData(String sensor){
        return sensorDataList.get(sensorMap.get(sensor));
    }


    // Set data of sensorDataList
    // replaces current data
    void setSensorData(SensorData data){
        sensorDataList.set(sensorMap.get(data.getSensor()), data);
    }


    int getWatchID() {
        return watchID;
    }
}