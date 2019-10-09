package org.openjfx;

import javax.security.auth.Subject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Class holding all smartwatch functionality
public class Smartwatch {

    // Data about watch
    private WatchData watchData;

    // ID of subject
    private SubjectData subjectData;

    // list of data of sensors
    private List<SensorData> sensorDataList = new ArrayList<>();

    // Map to map sensor name string to integer index for sensorDataList
    private static final Map<String, Integer> sensorMap;
    static{
        sensorMap = new HashMap<>();
        sensorMap.put("HR", 0); // put HR on spot 0
        sensorMap.put("GRAVITY", 1);
        sensorMap.put("ACCELERO", 2);
        sensorMap.put("STEP", 3);
    }

    // Number of sensors for a smartwatch
    private static final int numberOfSensors = 1;

    // NumberID of the watch
    private int watchID;


    // Constructor
    Smartwatch(WatchData _data, SubjectData _subjectData){
        System.out.println("making smartwatch");
        watchData = _data;
        subjectData = _subjectData;
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
        return watchData.getWatchID();
    }

    int getBatteryPercentage(){ return watchData.getBatteryPercentage(); }
}