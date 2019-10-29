package org.openjfx;

import java.util.ArrayList;
import java.util.List;

// Class for measurements to send to watch
class Measurement {
    // List of sensors to measure with the corresponding polling interval
    private List<Pair<String, Integer>> sensors = new ArrayList<>();

    // List of watches which have to perform the measurement
    private List<Smartwatch> watches = new ArrayList<>();

    // Measurement duration in minutes
    private Integer duration;

    public void setSensors(List<Pair<String, Integer>> list) { sensors = list; }
   /* public void addSensor(Pair<String, Integer> sen) { sensors.add(sen); }
    public void removeSensor(Pair<String, Integer> sen) { sensors.remove(sen); }
    public int getSensorsSize() { return sensors.size(); }
    public Pair<String, Integer> getSensorI(int i) { return sensors.get(i); }*/

    public void setWatches(List<Smartwatch> list) { watches = list; }

    public void setDuration(Integer x) { duration = x; }
}
