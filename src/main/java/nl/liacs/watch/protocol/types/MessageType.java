package nl.liacs.watch.protocol.types;

public enum MessageType {
    /**
     * a PING to check if the connection is still open uninterrupted and to
     * check if the other side still responds by sending a PONG.
     */
    PING(0),
    /**
     * a PONG to respond to a PING.
     */
    PONG(1),

    /**
     * an incremental data update for the dashboard, used in live views.
     */
    INCREMENT(2),
    /**
     * older collected data to be send to the dashboard.
     */
    PLAYBACK(3),

    /**
     * setting the poll interval of the given sensor.
     */
    SENSOR_INTERVAL(4),
    /**
     * setting sensor specific information.
     */
    SENSOR_SETTING(5),
    /**
     * set the streaming send interval, the live view will be basically throttled.  Events will be queued until the next
     * increment.
     */
    LIVE_INTERVAL(5);

    private int id;

    MessageType(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }
}
