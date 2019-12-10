package org.openjfx;


import nl.liacs.watch.protocol.server.WrappedConnection;
import nl.liacs.watch.protocol.types.Message;
import nl.liacs.watch.protocol.types.ParameterType;
import util.Util;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;


/**
 * Class holding all smartwatch data
 */
public class Smartwatch {

    /**
     * The connection with the watch.
     */
    private final WrappedConnection connection;
    /**
     * Data about the watch {@link WatchData}
     */
    private WatchData watchData;

    /**
     * Data about subject {@link SubjectData}
     */
    private SubjectData subjectData = null; // TODO: do something with this

    /**
     * Nickname for the watch specified by the user
     */
    private String watchName = "NONAME";

    /**
     * Map to map sensor name string to a {@link SensorData} instance containing data of all the sensors available
     */
    private Map<String, SensorData> sensorMap = new HashMap<>();

    /**
     * The current measurement which is being performed by the watch
     */
    private Measurement measurement;

    /**
     * Stores comments about the current watch and measurement using {@link Comment}
     */
    private List<Comment> comments = new ArrayList<>();

    private final Thread thread;

    /**
     * Constructor
     */
    public Smartwatch(WatchData _data, String _name, WrappedConnection connection){
        System.out.println("making smartwatch");
        watchData = _data;
        if(!_name.isEmpty()){
            watchName = _name;
        }

        this.connection = connection;

        this.thread = new Thread(() -> {
            while (this.connection.isOpen()) {
                Message item = null;
                try {
                    item = this.connection.receive();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    continue;
                }

                System.out.println("0");
                switch (item.type) {
                    case INCREMENT:
                        System.out.println("1");
                        item.parameters[0].setType(ParameterType.STRING);
                        var sensor = item.parameters[0].getString();
                        System.out.println(sensor);
                        if (sensor.equals("BATTERY")) {
                            System.out.println("2");
                            item.parameters[2].setType(ParameterType.DOUBLE);
                            System.out.println("3");
                            var percentage = item.parameters[2].getDouble();
                            System.out.println("4");
                            System.out.println(percentage);
                            this.watchData.setBatteryPercentage(percentage.intValue());
                            System.out.println("battery set!");
                            break;
                        }

                        if (!this.sensorMap.containsKey(sensor)) {
                            var data = new SensorData(watchData.getWatchID(), sensor, item.parameters.length-2);
                            this.sensorMap.put(sensor, data);
                        }

                        var values = Arrays.stream(item.parameters).skip(2).map((param) -> {
                            param.setType(ParameterType.DOUBLE);
                            return param.getDouble();
                        }).collect(Collectors.toList());
                        var dataPoint = new DataPoint(sensor, LocalDate.now(), LocalTime.now(), values);
                        this.sensorMap.get(sensor).add(dataPoint);

                        break;
                }
            }
        });
        if (this.connection != null) {
            this.thread.run();
        }
    }


    /**
     * Get {@link SensorData} from {@link Smartwatch#sensorMap}
     * @param sensor The name of the sensor
     * @return The {@link SensorData} corresponding the the sensor name
     */
    public SensorData getSensorData(String sensor){
        return sensorMap.get(sensor);
    }


    /**
     * Gets all the {@link SensorData} available from {@link Smartwatch#sensorMap}
     * @return A list of all the {@link SensorData}
     */
    List<SensorData> getAllSensorData(){
        return new ArrayList<>(sensorMap.values());
    }


    /**
     * Adds new {@link DataPoint}(s) to the right {@link SensorData} in {@link Smartwatch#sensorMap}
     * @param dataList List of {@link DataPoint}(s) to be added. The points can be from any supported sensor.
     */
    public List<String> addData(List<DataPoint> dataList){
        LinkedHashSet<String> sensorDataEdited = new LinkedHashSet<>();

        for (DataPoint dataPoint : dataList) {
            if(!sensorMap.containsKey(dataPoint.getSensorName())){
                System.out.println("Added sensor: " + dataPoint.getSensorName() + " for watch " + getWatchID());
                addSensor(dataPoint.getSensorName());
            }

            var sensorData = sensorMap.get(dataPoint.getSensorName());
            if (!sensorData.contains(dataPoint.getDate(), dataPoint.getTime())) {
                sensorData.add(dataPoint);
                sensorDataEdited.add(dataPoint.getSensorName());
            }
        }

        // ?!?!?!?!?!?
        // TODO: if data also gives ms this is not needed. For now we have to filter duplicates as we get multiple
        //  datapoint with the same time

        // TODO merging seems broken but maybe its not needed as seen above ^
       /* for (String s : sensorDataEdited) {
            sensorMap.get(s).mergeDuplicates();
        }*/
        return new ArrayList<>(sensorDataEdited);
    }


    /**
     * Sets {@link SensorData} of corresponding sensor
     * @param dataList Data that will be set
     */
    void setData(SensorData dataList){
        sensorMap.put(dataList.getSensor(), dataList);
    }


    /**
     * Get a list with sensors from the watch
     * @return List of Strings containing names of the sensors
     */
    public List<String> getSensorListFromMap(){
        return new ArrayList<>(sensorMap.keySet());
    }


    /**
     * Adds a new sensor to the watch by inserting into {@link Smartwatch#sensorMap}
     * @param sensor Name of the sensor
     */
    void addSensor(String sensor){
        if (!sensorMap.containsKey(sensor)) {
            System.out.println("Put sensor " + sensor + " place " + sensorMap.size());
            var data = new SensorData(watchData.getWatchID(), sensor, Util.sensorDataListSize.get(sensor));
            sensorMap.put(sensor, data);
        }
    }


    /**
     * Getter for {@link Smartwatch#comments}
     */
    public List<Comment> getComments(){ return comments; }


    /**
     * Adds comment to the list
     * @param comment Comment to be stored
     */
    public void addComment(Comment comment){ comments.add(comment); }


    /**
     * Setter for {@link Smartwatch#comments}
     */
    void setComments(List<Comment> _comments){ comments = _comments; }


    /**
     * Gets the ID of the watch using {@link WatchData#getWatchID()}
     * @return ID as a integer
     */
    public int getWatchID() {
        return watchData.getWatchID();
    }


    /**
     * Sets the ID of the watch using {@link WatchData#setWatchID(int)}
     * @param ID The value to set the ID to
     */
    public void setWatchID(int ID) { watchData.setWatchID(ID); }


    /**
     * Gets the battery percentage of the watch using {@link WatchData#getBatteryPercentage()}
     * @return Battery percentage as an integer
     */
    public int getBatteryPercentage(){ return watchData.getBatteryPercentage(); }


    /**
     * Getter for {@link Smartwatch#watchData}
     */
    public WatchData getWatchData() { return watchData; }


    /**
     * Getter for {@link Smartwatch#watchData}
     */
    public String getWatchName() { return watchName; }


    /**
     * Setter for {@link Smartwatch#watchName}
     */
    public void setWatchName(String name) { watchName = name; }


    /**
     * Setter for {@link Smartwatch#measurement}
     */
    public void setMeasurement(Measurement m) { measurement = m; }


    /**
     * Getter for {@link Smartwatch#measurement}
     */
    public Measurement getMeasurement() { return measurement; }
}
