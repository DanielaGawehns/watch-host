package nl.liacs.watch.protocol.types;

public enum MessageType {
    /**
     * a PING to check if the connection is still open uninterrupted and to
     * check if the other side still responds by sending a PONG.
     */
    PING,
    /**
     * a PONG to respond to a PING.
     */
    PONG,

    /**
     *  an incremental data update for the dashboard, used in live views.
     */
    INCREMENT,
    /**
     *  older collected data to be send to the dashboard.
     */
    PLAYBACK,

    /**
     * setting the poll interval of the given sensor.
     */
    SENSOR_INTERVAL,
    /**
     * setting sensor specific information.
     */
    SENSOR_SETTING,
    /**
     * set the streaming send interval, the live view will be basically throttled.  Events will be queued until the next
     * increment.
     */
    LIVE_INTERVAL,
}
