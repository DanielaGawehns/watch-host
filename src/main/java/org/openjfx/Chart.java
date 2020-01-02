package org.openjfx;

import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.VBox;
import java.util.ArrayList;
import java.util.List;


/**
 * Class for creating charts. It creates a lineChart with given {@link SensorData} and places it in a supplied vbox
 */
public class Chart {


    /**
     * The chart (string, number) to fill
     */
    private LineChart<String, Number> chart;

    /**
     * List of data to print in a chart. One sensorData depicts one line in the chart
     */
    private final List<SensorData> dataList;

    /**
     * Sensor which the data in the chart is from
     */
    private final String sensor;


    /**
     * Constructor with a list of {@link SensorData}. These SensorData object should be for the same sensor to represent
     * multiple lines in a chart
     */
    public Chart(List<SensorData> list, VBox vbox){
        dataList = list;
        sensor = dataList.get(0).getSensor();
        placeChartOnScreen(sensor, vbox);
    }

    /**
     * Constructor with a single {@link SensorData}
     */
    public Chart(SensorData data, VBox vbox){
        dataList = new ArrayList<>();
        dataList.add(data);
        sensor = dataList.get(0).getSensor();
        placeChartOnScreen(sensor, vbox);
    }

    /**
     * Creates a chart and fills it using {@link Chart#fillChart(LineChart, String)}. Then places the chart in {@code VBox} provided to display them on the screen
     * @param sensorName the name of the sensor for which we want to make a chart
     */
    private void placeChartOnScreen(String sensorName, VBox charts) {
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Time");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel(Sensors.sensorNameToFriendlyString(sensorName));

        chart = new LineChart<>(xAxis, yAxis);
        fillChart(chart, sensorName);
        charts.getChildren().add(chart);
    }

    // TODO: Make this work with all types of charts
    /**
     * Fills a Linechart with data
     * @param chart The Linechart to be filled
     * @param sensor The sensor that is used
     */
    private void fillChart(LineChart<String, Number> chart, String sensor) {

        System.out.println("Fill Chart for sensor " + sensor);
        SensorData sensorData = dataList.get(0); // get data of the right sensor

        chart.setAnimated(false); // disable animation for clearing
        chart.getData().clear();
        chart.setDisable(false); // turn on chart

        for(SensorData data : dataList){
            chart.getData().add(fillSeries(data)); // add series to chart
        }

        chart.setTitle(sensorData.getSensor()); // set title of chart

        System.out.println("Data size is " + sensorData.size());
    }

    /**
     * Creates a series with data taken from a sensor
     * @param sensorData {@link SensorData} of a sensor
     * @return Series containing the data. This series can be added to a compatible chart
     */
    private XYChart.Series<String, Number> fillSeries(SensorData sensorData){
        XYChart.Series<String, Number> series = new XYChart.Series<>(); // new series for adding data points

        series.setName(sensorData.getSensor()); // set title of line for legend
        for(int i = 0; i < sensorData.size(); i += 1){ //TODO: find more robust way to remove unnecessary nodes
            XYChart.Data<String, Number> temp = sensorData.getDataPoint(i); // get dataPoint no. i
            series.getData().add(temp); // add datapoint to series
        }

        return series;
    }


    /**
     * Adds a {@link SensorData} to the watch. This will create a new series with {@link Chart#fillSeries(SensorData)}
     * and add it to the chart
     * @param data The {@link SensorData} to be added
     */
    public void addData(SensorData data){
        if(!data.getSensor().equals(sensor)){
            System.err.println("ERROR: sensor of added data does not match chart sensor!");
            return;
        }
        dataList.add(data);
        chart.getData().add(fillSeries(data));

    }


    /**
     * Replaces a SensorData in {@link Chart#dataList} if a SensorData with corresponding watchID is found
     * @param watchID The watchID to be checked
     * @param data The data to replace the current data
     */
    public void setData(String watchID, SensorData data){
        int i = 0;
        for(SensorData sensorData : dataList){
            if(sensorData.getWatchID().equals(watchID)){
                dataList.set(i, data);
                fillChart(chart, sensor);
            }
            i++;
        }
        System.out.println("Chart has no SensorData for watch with id " + watchID);
    }

    /**
     * Getter for {@link Chart#sensor}
     */
    public String getSensor() { return sensor; }
}
