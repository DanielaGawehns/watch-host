package org.openjfx;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;


/**
 * Class holding all smartwatch data
 */
public class Smartwatch {

    /**
     * Manager for managing the Database connection {@link DBManager}
     */
    private static DBManager dbManager = new DBManager();

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
    private static final Map<String, Integer> sensorMap;
    static{
        sensorMap = new HashMap<>();
        sensorMap.put("HRM", 0); // put HRM on spot 0
        //sensorMap.put("PRESSURE", 1);
        //sensorMap.put("ACCELEROMETER", 2);
    }


    /**
     * Constructor
     */
    /*Smartwatch(WatchData _data, SubjectData _subjectData){
        System.out.println("making smartwatch");
        watchData = _data;
        subjectData = _subjectData;

        //sensorDataList.add(new SensorData(watchData.getWatchID(), "HRM"));
        //sensorDataList.add(new SensorData(watchData.getWatchID(), "PRESSURE"));
       // sensorDataList.add(new SensorData(watchData.getWatchID(), "ACCELEROMETER"));
    }*/


    /**
     * Constructor
     */
    Smartwatch(WatchData _data, String name){
        System.out.println("making smartwatch");
        watchData = _data;
        if(!name.isEmpty()){
            watchName = name;
        }

        sensorDataList.add(new SensorData(watchData.getWatchID(), "HRM"));
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
     * Adds new {@link DataPoint}(s) to the right {@link SensorData#records} in {@link Smartwatch#sensorDataList}
     * @param dataList List of {@link DataPoint}(s) to be added. The points can be from any supported sensor.
     */
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

    void setData(SensorData dataList){
        sensorDataList.set(sensorMap.get(dataList.getSensor()), dataList);
    }
}


































