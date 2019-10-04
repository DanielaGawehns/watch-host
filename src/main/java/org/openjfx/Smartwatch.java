package org.openjfx;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// Class holding all smartwatch functionality
public class Smartwatch {

    // list of data of sensors
    private List<SensorData> sensorDataList = new ArrayList<>();


    // Constructor
    public Smartwatch() throws IOException {
        System.out.println("making smartwatch");
    }


    // Get data of sensor 'sensor' from sensorDataList
    // TODO: make this work
    public SensorData getSensorData(String sensor){
        return sensorDataList.get(0);
    }


    // Set data of sensorDataList
    // if sensor is not found then add to list
    // otherwise replace data
    // TODO: make this work
    public void setSensorData(SensorData data, String sensor){
        if(sensorDataList.size() <= 0){
            sensorDataList.add(data);
        }else{
            sensorDataList.set(0, data);
        }

    }
}