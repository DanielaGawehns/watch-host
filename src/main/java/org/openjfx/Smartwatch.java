package org.openjfx;


import nl.liacs.watch.protocol.server.WrappedConnection;
import org.jetbrains.annotations.NotNull;
import util.Util;

import java.io.Closeable;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;


/**
 * Class holding all smartwatch data
 */
public class Smartwatch implements Closeable {

    /**
     * Data about the watch {@link WatchData}
     */
    private final WatchData watchData;

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
    private final Map<String, SensorData> sensorMap = new HashMap<>();

    /**
     * The current measurement which is being performed by the watch
     */
    private Measurement measurement;

    /**
     * Stores comments about the current watch and measurement using {@link Comment}
     */
    private List<Comment> comments = new ArrayList<>();

    private WatchConnector connector = null;

    /**
     * The date from which we start printing data in the charts in {@link org.openjfx.controllers.WatchViewController}
     */
    private LocalDate startDate = LocalDate.now().minusDays(Util.standardDaysBack);


    /**
     * Constructor
     */
    public Smartwatch(WatchData _data, String _name, WrappedConnection connection) {
        System.out.println("making smartwatch");
        watchData = _data;
        if(!_name.isEmpty()){
            watchName = _name;
        }

        if (connection != null) {
            try {
                this.addConnection(connection);
            } catch (IOException e) { // this can't throw in this case
                e.printStackTrace();
            }
        }
    }

    public void addConnection(@NotNull WrappedConnection connection) throws IOException {
        if (this.connector != null) {
            this.connector.close();
        }
        this.connector = new WatchConnector(this, connection);
    }

    /**
     * Get {@link SensorData} from {@link Smartwatch#sensorMap}
     * @param sensor The name of the sensor
     * @return The {@link SensorData} corresponding the the sensor name
     */
    public SensorData getSensorData(String sensor){
        System.out.println("Getting data from sensor " + sensor);
        return sensorMap.get(sensor);
    }


    /**
     * Get {@link SensorData} from {@link Smartwatch#sensorMap} with all the {@link DataPoint}s between date and current date
     * @param sensor The name of the sensor
     * @param date The start date to get the data.
     * @return The {@link SensorData} corresponding the the sensor name
     */
    public SensorData getSensorData(String sensor, LocalDate date){
        SensorData sensorData = getSensorData(sensor);
        List<DataPoint> newList;

        if(sensorData == null){
            System.out.println("Watch " + getWatchID() + " has no data for sensor " + sensor);
            return null;
        }

        if(date.isBefore(sensorData.getDataLoadedDate())){
            System.out.println("[Smartwatch#getSensorData] retrieving data from watch " + getWatchID() + " of sensor " + sensor);
            setData(App.getDbManager().getDataList(getWatchID(), sensor, date.atStartOfDay(), LocalDateTime.now()));
            sensorData.setDataLoadedDate(date);
        }

        newList = new ArrayList<>();
        sensorData = getSensorData(sensor);



        System.out.println("[Smartwatch#getSensorData] Getting data of watch " + getWatchID() + " from sensor " + sensor + " from after " + date.atStartOfDay());
        System.out.println("\trecords are of size " + sensorData.size());

        for(DataPoint point : sensorData.getRecords()){
            if(point.getDateTime().isAfter(date.atStartOfDay())){
                newList.add(point);
            }
        }
        return new SensorData(sensorData.getWatchID(), sensorData.getSensor(), sensorData.getDataFieldsNumber(), sensorData.getDataLoadedDate(), newList);
    }


    /**
     * Gets all the {@link SensorData} available from {@link Smartwatch#sensorMap}
     * @return A list of all the {@link SensorData}
     */
    List<SensorData> getAllSensorData(){
        return new ArrayList<>(sensorMap.values());
    }

    /**
     * Gets all the {@link SensorData} available from {@link Smartwatch#sensorMap}
     * @return A list of all the {@link SensorData}
     */
    List<SensorData> getAllSensorData(LocalDate date){
        List<SensorData> totalData = getAllSensorData();

        for(SensorData sensorData : totalData){
            if(date.isBefore(sensorData.getDataLoadedDate())){
                setData(App.getDbManager().getDataList(getWatchID(), sensorData.getSensor(), date.atStartOfDay(), LocalDateTime.now()));
                sensorData.setDataLoadedDate(date);
            }
        }

        totalData.clear();

        for(SensorData data : sensorMap.values()){
            totalData.add(getSensorData(data.getSensor(), date));
        }
        return totalData;
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
            if (!sensorData.contains(dataPoint.getDateTime())) {
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
            var data = new SensorData(watchData.getWatchID(), sensor, Util.sensorDataListSize.get(sensor), startDate);
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
     * @return ID as a string
     */
    public String getWatchID() {
        return watchData.getWatchID();
    }


    /**
     * Sets the ID of the watch using {@link WatchData#setWatchID(String)}
     * @param ID The value to set the ID to
     */
    public void setWatchID(String ID) { watchData.setWatchID(ID); }


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


    public LocalDate getStartDate() { return startDate; }

    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    @Override
    public void close() throws IOException {
        if (this.connector != null) {
            this.connector.close();
        }
    }
}
