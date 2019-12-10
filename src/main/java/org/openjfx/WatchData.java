package org.openjfx;


/**
 * Class for storing technical information about a smartwatch
 */
public class WatchData {

    /**
     * The ID of the watch
     */
    private int watchID;

    /**
     * The battery level of the watch
     */
    private int batteryPercentage = 100;

    /**
     * The max storage amount of the watch. Measured in MB
     */
    private float maxStorage = 1;

    /**
     * The used storage amount of the watch. Measured in MB
     */
    private float usedStorage = 1;


    /**
     * Constructor
     */
    WatchData(int _watchID, int _batteryPercentage, float _maxStorage, float _usedStorage){
        watchID = _watchID;
        batteryPercentage = _batteryPercentage;
        maxStorage = _maxStorage;
        usedStorage = _usedStorage;
    }


    /**
     * Constructor
     */
    public WatchData(int _watchID){
        watchID = _watchID;
    }


    /**
     * Getter for {@link WatchData#watchID}
     */
    public int getWatchID() {
        return watchID;
    }


    /**
     * Setter for {@link WatchData#watchID}
     */
    public void setWatchID(int watchID) {
        this.watchID = watchID;
    }


    /**
     * Getter for {@link WatchData#batteryPercentage}
     */
    public int getBatteryPercentage() {
        return batteryPercentage;
    }


    /**
     * Setter for {@link WatchData#batteryPercentage}
     */
    public void setBatteryPercentage(int batteryPercentage) {
        this.batteryPercentage = batteryPercentage;
    }


    /**
     * Getter for {@link WatchData#maxStorage}
     */
    public float getMaxStorage() {
        return maxStorage;
    }


    /**
     * Setter for {@link WatchData#maxStorage}
     */
    public void setMaxStorage(float maxStorage) {
        this.maxStorage = maxStorage;
    }


    /**
     * Getter for {@link WatchData#usedStorage}
     */
    public float getUsedStorage() {
        return usedStorage;
    }


    /**
     * Setter for {@link WatchData#usedStorage}
     */
    public void setUsedStorage(float usedStorage) {
        this.usedStorage = usedStorage;
    }
}
