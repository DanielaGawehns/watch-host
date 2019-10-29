package org.openjfx;

import java.util.ArrayList;
import java.util.List;

// Class for measurements to send to watch
class Measurement {
    // List of sensors to measure with the corresponding polling interval (in milliseconds)
    private List<Pair<String, Integer>> sensors = new ArrayList<>();

    // List of watches on which to perform the measurement
    private List<Smartwatch> watches = new ArrayList<>();

    // Measurement duration in minutes
    private Integer duration;

    // Set the list of sensors with their interval
    public void setSensors(List<Pair<String, Integer>> list) { sensors = list; }

    // Set the list of watches on which to perform the measurement
    public void setWatches(List<Smartwatch> list) { watches = list; }

    // Set the measurement duration
    public void setDuration(Integer x) { duration = x; }
}
