package org.openjfx;

import java.util.ArrayList;
import java.util.List;

/**
 * Class for storing information about measurements which are to be sent to watches
 */
class Measurement {

    /**
     * List of pairs of sensors which should be measured with a corresponding interval {@link Pair}
     */
    private List<Pair<String, Integer>> sensors = new ArrayList<>();


    /**
     * The duration of the measurement in minutes
     */
    private int duration = 0;


    /**
     * Setter for {@link Measurement#sensors}
     */
    public void setSensors(List<Pair<String, Integer>> list) { sensors = list; }


    /**
     * Setter for {@link Measurement#duration}
     */
    public void setDuration(int x) { duration = x; }


    /**
     * Getter for {@link Measurement#sensors}
     */
     List<Pair<String, Integer>> getSensors() { return sensors; }

    /**
     * Getter for {@link Measurement#sensors}
     */
    Integer getDuration() { return duration; }


    /**
     * Size of the {@link Measurement#sensors} list
     * @return Size as Integer
     */
    int size(){ return sensors.size(); }
}
