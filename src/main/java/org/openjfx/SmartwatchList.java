package org.openjfx;

import util.Util;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Class holding a list of {@link Smartwatch}
 */
public class SmartwatchList extends ArrayList<Smartwatch> {

    public SmartwatchList() {
        super();
    }


    /**
     * start date of which we load data into the charts in {@link org.openjfx.controllers.OverviewController}
     */
    private LocalDate startDate = LocalDate.now().minusDays(Util.standardDaysBack);


    /**
     * Find the index of the watch per its ID.
     * @param id The ID of the watch to find.
     * @return -1 if the watch is not found, it's index in the list othewise.
     */
    private int findWithID(String id) {
        for (int i = 0; i < this.size(); i++) {
            if (this.get(i).getWatchID().equals(id)) {
                return i;
            }
        }
        return -1;
    }


    /**
     * Get from list
     * @param id Watch ID
     */
    public Smartwatch getWithID(String id){
        int index = this.findWithID(id);
        if (index == -1) {
            return null;
        }

        return this.get(index);
    }


    /**
     * Removes the watch with the given ID from the list.
     * @param id The ID of the watch to remove.
     * @throws IllegalArgumentException Throws illegal argument exception when there is no watch found with the given ID.
     */
    public void removeWithID(String id) throws IllegalArgumentException {
        int index = this.findWithID(id);
        if (index == -1) {
            throw new IllegalArgumentException("no watch found with given id");
        }

        this.remove(index);
    }


    /**
     * Goes through all the connected watches and counts how many have an measurement attached
     * @return Number of measurements counted
     */
    public int getNumberOfMeasurements(){
        int count = 0;
        for(Smartwatch watch : this){
            if(watch.getMeasurement() != null){
                count++;
            }
        }
        return count;
    }


    /**
     * Gets a list of all the {@link SensorData} available in all {@link Smartwatch}
     * @return The list of data
     */
    public List<SensorData> getAllSensorData(){
        List<SensorData> totalDataList = new ArrayList<>();
        for(Smartwatch watch : this){
            totalDataList.addAll(watch.getAllSensorData());
        }
        return totalDataList;
    }


    /**
     * Gets a list of all the {@link SensorData} available in all {@link Smartwatch} with data from after the specified date
     * @param date The date after which the data will be gathered
     * @return The list of data
     */
    public List<SensorData> getAllSensorData(LocalDate date){
        List<SensorData> totalDataList = new ArrayList<>();
        for(Smartwatch watch : this){
            totalDataList.addAll(watch.getAllSensorData(date));
        }
        return totalDataList;
    }


    /**
     * Getter of {@link SmartwatchList#startDate}
     */
    public LocalDate getStartDate() { return startDate; }


    /**
     * Setter of {@link SmartwatchList#startDate}
     */
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
}
