package org.openjfx;


import util.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;


/**
 * Class holding all smartwatch data
 */
class Smartwatch {

    /**
     * Data about the watch {@link WatchData}
     */
    private WatchData watchData;

    /**
     * Data about subject {@link SubjectData}
     */
    private SubjectData subjectData = null;

    /**
     * Nickname for the watch specified by the user
     */
    private String watchName = "NONAME";

    /**
     * List of {@link SensorData} containing data of all the sensors available
     */
    private List<SensorData> sensorDataList = new ArrayList<>();

    /**
     * Map to map sensor name string to integer index for {@link Smartwatch#sensorDataList}
     */
    private Map<String, Integer> sensorMap = new HashMap<>();

    /**
     * The current measurement which is being performed by the watch
     */
    private Measurement measurement;


    /**
     * Constructor
     */
    Smartwatch(WatchData _data, String _name){
        System.out.println("making smartwatch");
        watchData = _data;
        if(!_name.isEmpty()){
            watchName = _name;
        }

        //sensorDataList.add(new SensorData(watchData.getWatchID(), "HRM"));
        //sensorDataList.add(new SensorData(watchData.getWatchID(), "PRESSURE"));
        //sensorDataList.add(new SensorData(watchData.getWatchID(), "ACCELEROMETER"));
    }



    /**
     * Get data of sensor {@code sensor} from {@link Smartwatch#sensorDataList}
     * @param sensor The name of the sensor
     * @return The {@link SensorData} corresponding the the sensor name
     */
    SensorData getSensorData(String sensor){
        return sensorDataList.get(sensorMap.get(sensor));
    }


    /**
     * Gets {@link WatchData#watchID} from {@link Smartwatch#watchData}
     * @return {@link WatchData#watchID} as a integer
     */
    int getWatchID() {
        return watchData.getWatchID();
    }


    /**
     * Sets {@link WatchData#watchID} from {@link Smartwatch#watchData}
     * @param ID The value to set {@link WatchData#watchID}
     */
    void setWatchID(int ID) { watchData.setWatchID(ID); }


    /**
     * Gets {@link WatchData#batteryPercentage} from {@link Smartwatch#watchData}
     * @return {@link WatchData#batteryPercentage} as a integer
     */
    int getBatteryPercentage(){ return watchData.getBatteryPercentage(); }


    /**
     * Getter for {@link Smartwatch#watchData}
     */
    WatchData getWatchData() { return watchData; }


    /**
     * Getter for {@link Smartwatch#watchData}
     */
    String getWatchName() { return watchName; }


    /**
     * Setter for {@link Smartwatch#watchName}
     */
    void setWatchName(String name) { watchName = name; }


    /**
     * Setter for {@link Smartwatch#measurement}
     */
    void setMeasurement(Measurement m) { measurement = m; }


    /**
     * Getter for {@link Smartwatch#measurement}
     */
    Measurement getMeasurement() { return measurement; }


    /**
     * Adds new {@link DataPoint}(s) to the right {@link SensorData#records} in {@link Smartwatch#sensorDataList}
     * @param dataList List of {@link DataPoint}(s) to be added. The points can be from any supported sensor.
     */
    void addData(List<DataPoint> dataList){
        SortedSet<String> sensorDataEdited = new TreeSet<>();
        String sensor;

        for (DataPoint dataPoint : dataList) {
            if(!sensorMap.containsKey(dataPoint.getSensorName())){
                System.out.println("Added sensor: " + dataPoint.getSensorName() + " for watch " + getWatchID());
                addSensor(dataPoint.getSensorName());
            }
            if(!sensorDataList.get(sensorMap.get(dataPoint.getSensorName())).contains(dataPoint.getDate(), dataPoint.getTime())){
                sensorDataList.get(sensorMap.get(dataPoint.getSensorName())).add(dataPoint);
                sensorDataEdited.add(dataPoint.getSensorName());
            }
        }

        for(int i = 0; i < sensorDataEdited.size(); i++){
            sensor = sensorDataEdited.first();
            sensorDataList.get(sensorMap.get(sensor)).mergeDuplicates();
            sensorDataEdited.remove(sensor);
        }
    }


    /**
     * Sets {@link SensorData} of corresponding sensor
     * @param dataList Data that will be set
     */
    void setData(SensorData dataList){
        sensorDataList.set(sensorMap.get(dataList.getSensor()), dataList);
    }


    /**
     * Get a list with sensors from the watch
     * @return List of Strings containing names of the sensors
     */
    List<String> getSensorListFromMap(){
        return new ArrayList<>(sensorMap.keySet());
    }


    /**
     * Adds a new sensor to the watch
     * @param sensor Name of the sensor
     */
    void addSensor(String sensor){
        sensorMap.put(sensor, sensorMap.size());
        System.out.println("Put sensor " + sensor + " place " + (sensorMap.size()-1));
        sensorDataList.add(new SensorData(watchData.getWatchID(), sensor, Util.sensorDataListSize.get(sensor))); // TODO: get the datalist size from input file
    }
}


































