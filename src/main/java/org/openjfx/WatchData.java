package org.openjfx;

import javafx.beans.binding.Binding;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableValue;

import java.util.List;

// Class for storing data about the watch for managing
class WatchData {

    private List<Measurement> measurements;

    private int watchID;

    private int batteryPercentage;

    private float maxStorage;

    private float usedStorage;

    WatchData(int _watchID, int _batteryPercentage, float _maxStorage, float _usedStorage){
        watchID = _watchID;
        batteryPercentage = _batteryPercentage;
        maxStorage = _maxStorage;
        usedStorage = _usedStorage;
    }

    public int getWatchID() {
        return watchID;
    }

    public void setWatchID(int watchID) {
        this.watchID = watchID;
    }

    public int getBatteryPercentage() {
        return batteryPercentage;
    }

    public void setBatteryPercentage(int batteryPercentage) {
        this.batteryPercentage = batteryPercentage;
    }

    public float getMaxStorage() {
        return maxStorage;
    }

    public void setMaxStorage(float maxStorage) {
        this.maxStorage = maxStorage;
    }

    public float getUsedStorage() {
        return usedStorage;
    }

    public void setUsedStorage(float usedStorage) {
        this.usedStorage = usedStorage;
    }

    ObservableValue changeProperty(){
        return Bindings.concat(watchID, batteryPercentage, maxStorage, usedStorage);
    }
}
