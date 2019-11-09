package org.openjfx;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * For storing a data point gathered from a sensor
 * Uses the format: SENSORNAME, DATE, TIME, Double(s)
 */
class DataPoint {

    /**
     * Name of the sensor from which the data came
     */
    private String sensorName;

    /**
     * Date of measurement
     */
    private LocalDate date;

    /**
     * Time of measurement
     */
    private LocalTime time;

    /**
     * List of doubles which saves data of measurement
     */
    private List<Double> dataList;

    /**
     * Comment added by the user
     */
    private String comment;

    /**
     * Constructor
     */
    DataPoint(String _sensorName, LocalDate _date, LocalTime _time, List<Double> _dataList){
        sensorName = _sensorName;
        date = _date;
        time = _time;
        dataList = _dataList;
        comment = "";
    }

    /**
     * Getter for {@link org.openjfx.DataPoint#sensorName}
     */
    String getSensorName() {
        return sensorName;
    }

    /**
     * Getter for {@link org.openjfx.DataPoint#date}
     */
    LocalDate getDate() {
        return date;
    }

    /**
     * Getter for {@link org.openjfx.DataPoint#time}
     * @return
     */
    LocalTime getTime() {
        return time;
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

    /**
     * Setter for {@link org.openjfx.DataPoint#comment}
     * @param _comment String containing the comment
     */
    void setComment(String _comment){ comment = _comment; }
}
