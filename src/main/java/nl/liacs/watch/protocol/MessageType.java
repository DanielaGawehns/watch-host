package nl.liacs.watch.protocol;

public enum MessageType {
    // a heartbeat to check if the connection is still open uninterrupted.
    HEARTBEAT,

    // an incremental data update for the dashboard, used in live views.
    INCREMENT,
    // older collected data to be send to the dashboard.
    PLAYBACK,

    // setting the poll interval of the given sensor.
    SENSOR_INTERVAL,
    // setting sensor specific information.
    SENSOR_SETTING,
    // set the streaming send interval, the live view will be basically throttled.  Events will be queued until the next
    // increment.
    LIVE_INTERVAL,
}
