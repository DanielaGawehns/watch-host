package org.openjfx;

import util.Pair;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Class for storing information about measurements which are to be sent to watches
 */
public class Measurement {

    /**
     * List of pairs of sensors which should be measured with a corresponding interval {@link Pair}
     */
    private List<Pair<String, Integer>> sensors = new ArrayList<>();

    /**
     * The starting time of the measurement
     */
    private LocalTime timeStart;


    /**
     * The ending time of the measurement
     */
    private LocalTime timeEnd;


    /**
     * Setter for {@link Measurement#sensors}
     */
    public void setSensors(List<Pair<String, Integer>> list) { sensors = list; }



    /**
     * Getter for {@link Measurement#sensors}
     */
    public List<Pair<String, Integer>> getSensors() { return sensors; }


    /**
     * Size of the {@link Measurement#sensors} list
     * @return Size as Integer
     */
    public int size(){ return sensors.size(); }

    public LocalTime getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(LocalTime timeStart) {
        this.timeStart = timeStart;
    }

    public LocalTime getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(LocalTime timeEnd) {
        this.timeEnd = timeEnd;
    }
}
