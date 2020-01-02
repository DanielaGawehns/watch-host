package org.openjfx;

import java.time.LocalDateTime;
import java.util.List;

/**
 * For storing a data point gathered from a sensor
 * Uses the format: SENSORNAME, DATE, TIME, Double(s)
 */
public class DataPoint {

    /**
     * Name of the sensor from which the data came
     */
    private final String sensorName;

    /**
     * Date and time of measurement
     */
    private final LocalDateTime dateTime;

    /**
     * List of doubles which saves data of measurement
     */
    private List<Double> dataList;


    /**
     * Constructor
     */
    DataPoint(String _sensorName, LocalDateTime _dateTime, List<Double> _dataList){
        sensorName = _sensorName;
        dateTime = _dateTime;
        dataList = _dataList;
    }

    /**
     * Getter for {@link org.openjfx.DataPoint#sensorName}
     */
    String getSensorName() {
        return sensorName;
    }

    /**
     * Getter for {@link org.openjfx.DataPoint#dateTime}
     */
    LocalDateTime getDateTime() {
        return dateTime;
    }

    /**
     * Getter for {@link org.openjfx.DataPoint#dataList}
     */
    List<Double> getDataList() {
        return dataList;
    }

    /**
     * Setter for {@link org.openjfx.DataPoint#dataList}
     * @param dataList A list of doubles. Can be of any length
     */
    void setDataList(List<Double> dataList) {
        this.dataList = dataList; //TODO make size of datalist match the sensor
    }

}
