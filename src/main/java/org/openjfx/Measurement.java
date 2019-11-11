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
     * List of {@link Smartwatch} that are selected to perform the measurement
     */
    private List<Smartwatch> watches = new ArrayList<>();

    /**
     * The duration of the measurement in minutes
     */
    private Integer duration;


    /**
     * Setter for {@link Measurement#sensors}
     */
    public void setSensors(List<Pair<String, Integer>> list) { sensors = list; }


    /**
     * Setter for {@link Measurement#watches}
     */
    public void setWatches(List<Smartwatch> list) { watches = list; }


    /**
     * Setter for {@link Measurement#duration}
     */
    public void setDuration(Integer x) { duration = x; }
}
