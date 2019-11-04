package org.openjfx;

import java.util.Date;
import java.util.List;

class DataPoint {

    private String sensorName;

    private Date date;

    private Date time;

    private List<Double> dataList;

    private String comment;

    DataPoint(String _sensorName, Date _date, Date _time, List<Double> _dataList){
        sensorName = _sensorName;
        date = _date;
        time = _time;
        dataList = _dataList;
        comment = "";
    }

    String getSensorName() {
        return sensorName;
    }

    Date getDate() {
        return date;
    }

    Date getTime() {
        return time;
    }

    List<Double> getDataList() {
        return dataList;
    }

    void setDataList(List<Double> dataList) {
        this.dataList = dataList;
    }

    void setComment(String _comment){ comment = _comment; }
}
